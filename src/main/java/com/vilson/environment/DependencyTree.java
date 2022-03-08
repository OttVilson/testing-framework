package com.vilson.environment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

class DependencyTree {
    private final Map<String, List<String>> mergeTo = new HashMap<>();
    private final Gson gson = new Gson();

    DependencyTree(List<String> fileNames) {
        initializeMergeTo(fileNames);
    }

    Optional<List<String>> getChildrenOf(@Nullable String parent) {
        if (mergeTo.containsKey(parent))
            return Optional.of(mergeTo.get(parent));
        else
            return Optional.empty();
    }

    private void initializeMergeTo(List<String> fileNames) {
        fileNames.forEach(this::insertForFile);
    }

    private void insertForFile(String fileName) {
        String path = String.format("environments/%s.json", fileName);
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream inInitial = loader.getResourceAsStream(path);
        if (inInitial != null) {
            try (InputStream in = inInitial;
                 InputStreamReader isr = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(isr)) {
                processInsert(br, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to read from " + path);
        }
    }

    private void processInsert(Reader reader, String fileName) {
        String parent = null;
        JsonElement mergeTo = gson.fromJson(reader, JsonObject.class).get("merge-to");
        if (mergeTo != null) parent = mergeTo.getAsString();
        insertToMap(parent, fileName);
    }

    private void insertToMap(@Nullable String parent, String child) {
        List<String> children = mergeTo.getOrDefault(parent, new ArrayList<>());
        children.add(child);
        mergeTo.put(parent, children);
    }
}
