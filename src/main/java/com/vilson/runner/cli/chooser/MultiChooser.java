package com.vilson.runner.cli.chooser;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiChooser<T> extends Chooser<T> {

    public MultiChooser(List<T> availableOptions, Scanner scanner, Function<T, String> outputFormat) {
        super(availableOptions, scanner, outputFormat);
    }

    public List<T> chooseFromList() {
        showAvailableOptions();
        List<Integer> indices = readInNumbers();
        removeInvalidIndices(indices);
        List<T> chosenOnes = getChosenOnes(indices);
        showChosenOnes(chosenOnes);
        return chosenOnes;
    }

    private List<Integer> readInNumbers() {
        while (true) {
            System.out.println("Choose by typing in numbers as comma separated list, e.g. 1, 3, 4.");
            try {
                return getInput();
            } catch (NumberFormatException e) {
                System.out.println("The input couldn't be parsed as list of suitable integers.");
            }
        }
    }

    private void removeInvalidIndices(List<Integer> indices) {
        indices.removeIf(i -> i < 0 || i >= availableOptions.size());
    }

    private List<T> getChosenOnes(List<Integer> indices) {
        return indices.stream()
                .map(availableOptions::get)
                .collect(Collectors.toList());
    }

    private void showChosenOnes(List<T> chosenOnes) {
        System.out.println("You chose:");
        printList(chosenOnes);
    }

    private List<Integer> getInput() {
        String line = scanner.nextLine();
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .map(this::minusOneToGetZeroBasedIndex)
                .collect(Collectors.toList());
    }
}
