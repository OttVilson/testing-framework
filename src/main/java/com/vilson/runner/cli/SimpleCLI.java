package com.vilson.runner.cli;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vilson.environment.Environment;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.cli.operations.Operation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Scanner;

public class SimpleCLI {

    public static void main(String[] args) throws IOException {
        Environment environment = null;
        Gson gson = new Gson();
        if (args.length != 0 && args[0] != null) {
            environment = getEnvironment(args[0], gson);
        }

        Scanner scanner = new Scanner(System.in);
        MainOperations mainOperations = getMainOperations(environment, scanner, gson);

        SingleChooser<Operation> chooseOperation =
                new SingleChooser<>(mainOperations.getOperations(), scanner, String::valueOf);

        ForIntroduction.printIntroduction();
        while (true) {
            Operation op = chooseOperation.chooseFromList();
            if (op.equals(Operation.EXIT)) {
                mainOperations.stop();
                break;
            }
            op.process();
        }
    }

    private static Environment getEnvironment(String json, Gson gson) {
        try {
            return gson.fromJson(json, Environment.class);
        } catch (JsonSyntaxException e) {
            System.out.println("The input did not correspond to Environment structure.");
        }

        return null;
    }

    private static MainOperations getMainOperations(@Nullable Environment environment, Scanner scanner, Gson gson) {
        if (environment == null)
            return new MainOperations(scanner, gson);
        else
            return new MainOperations(scanner, environment, gson);
    }
}
