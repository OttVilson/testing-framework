package com.vilson.environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// https://mkyong.com/java/java-read-a-file-from-resources-folder/
public class EnvironmentConfigurations {

    private static final String ENVIRONMENTS_FOLDER = "environments";

    private final List<String> listOfEnvironments;
    private final Map<String, Environment> environments = new HashMap<>();
    private final Gson gson;

    public EnvironmentConfigurations(Gson gson) {
        this.gson = gson;
        listOfEnvironments = getFileNames();
        populateEnvironmentsMap();
    }

    public EnvironmentConfigurations(Environment environment, Gson gson) {
        this(gson);
        Environment production = environments.get("production");
        environment = MergeRules.merge(production, environment);
        environments.put("custom", environment);
        listOfEnvironments.add("custom");
    }

    public List<String> getListOfEnvironments() {
        return new ArrayList<>(listOfEnvironments);
    }

    public Optional<Environment> getEnvironment(String environmentName) {
        if (environments.containsKey(environmentName))
            return Optional.of(environments.get(environmentName));
        else
            return Optional.empty();
    }

    private List<String> getFileNames() {
        String path = ENVIRONMENTS_FOLDER + "/list";
        return fromInputStream(path,
                bufferedReader -> bufferedReader.lines()
                        .filter(line -> !line.startsWith("#"))
                        .collect(Collectors.toList()));
    }

    private void populateEnvironmentsMap() {
        listOfEnvironments.forEach(this::processEnvironment);
        mergeEnvironments();
    }

    private void processEnvironment(String fileName) {
        String path = String.format("%s/%s.json", ENVIRONMENTS_FOLDER, fileName);
        Environment env = fromInputStream(path, bufferedReader -> gson.fromJson(bufferedReader, Environment.class));
        environments.put(fileName, env);
    }

    private void mergeEnvironments() {
        DependencyTree tree = new DependencyTree(listOfEnvironments, gson);
        Optional<List<String>> roots = tree.getChildrenOf(null);
        roots.stream()
                .flatMap(Collection::stream)
                .forEach(root -> mergeRecursively(tree, root));
    }

    private void mergeRecursively(DependencyTree tree, String parent) {
        Environment parentEnvironment = environments.get(parent);
        if (parentEnvironment == null) return;

        Optional<List<String>> children = tree.getChildrenOf(parent);
        children.stream().flatMap(Collection::stream).forEach(child -> {
            Environment childEnvironment = environments.get(child);
            if (childEnvironment == null) return;

            environments.put(child, MergeRules.merge(parentEnvironment, childEnvironment));
            mergeRecursively(tree, child);
        });
    }

    private <T> T fromInputStream(String path, Function<BufferedReader, T> channeler) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inInitial = classLoader.getResourceAsStream(path);
        if (inInitial != null) {
            try (InputStream in = inInitial;
                 InputStreamReader isr = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(isr)) {
                return channeler.apply(br);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read input from " + path);
            }
        } else {
            throw new MissingResourceException("No resource found from " + path, this.getClass().getName(), path);
        }
    }
}
