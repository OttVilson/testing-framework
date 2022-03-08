package com.vilson.runner.cli.operations;

import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.run.TestNgConfiguration;
import org.testng.xml.XmlSuite;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

class ConfigurationInput {

    private final SingleChooser<XmlSuite.ParallelMode> parallelModeChooser;
    private final Scanner scanner;

    ConfigurationInput(Scanner scanner) {
        this.scanner = scanner;
        parallelModeChooser = new SingleChooser<>(List.of(XmlSuite.ParallelMode.values()), scanner, String::valueOf);
    }

    TestNgConfiguration getConfiguration() {
        TestNgConfiguration conf = new TestNgConfiguration();
        conf.setVerbose(getVerbosity());
        conf.setThreadCount(getnumberOfThreads());
        conf.setPreserveOrder(getPreserveOrder());
        conf.setParallelMode(parallelModeChooser.chooseFromList());

        return conf;
    }

    private int getVerbosity() {
        Consumer<Integer> validateBounds = i -> {
            if (i < 0 || i > 10) {
                String format = "Out of bounds: provided %d, lower bound %d, upper bound %d.";
                String message = String.format(format, i, 0, 10);
                throw new IllegalArgumentException(message);
            }
        };

        return getInt("Choose an integer 0-10 for verbosity.", validateBounds);
    }

    private int getnumberOfThreads() {
        Consumer<Integer> validateBounds = i -> {
            if (i < 1) throw new IllegalArgumentException("Number of threads must be positive.");
        };

        return getInt("Choose number of threads", validateBounds);
    }

    private boolean getPreserveOrder() {
        while (true) {
            System.out.println("Choose whether order should be preserved (true/false).");
            try {
                String input = scanner.next();
                if (!"true".equals(input) && !"false".equals(input))
                    throw new IllegalArgumentException("");
                return Boolean.parseBoolean(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Input didn't match patter (true/false)");
            }
        }
    }

    private int getInt(String startMessage, Consumer<Integer> validateBounds) {
        while (true) {
            System.out.println(startMessage);
            try {
                int number = Integer.parseInt(scanner.next());
                validateBounds.accept(number);
                return number;
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to parse number or out of bounds.");
            }
        }
    }
}
