package com.vilson.runner.cli.chooser;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class SingleChooser<T> extends Chooser<T> {

    public SingleChooser(List<T> availableOptions, Scanner scanner, Function<T, String> outputFormat) {
        super(availableOptions, scanner, outputFormat);
    }

    public T chooseFromList() {
        showAvailableOptions();
        int index = getNumber();
        T chosenOne = availableOptions.get(index);
        showChosenOne(chosenOne);
        return chosenOne;
    }

    private int getNumber() {
        while (true) {
            System.out.println("Type a number");
            try {
                return readInNumber();
            } catch (IllegalArgumentException e) {
                System.out.println("The input was not a number or out of bounds.");
            }
        }
    }

    private int readInNumber() {
        int index = minusOneToGetZeroBasedIndex(Integer.parseInt(scanner.next()));
        if (index < 0 || index >= availableOptions.size()) {
            String message = String.format("Out of bounds: submitted %d, lower bound %d, upper bound %s",
                    index, 0, availableOptions.size());
            throw new IllegalArgumentException(message);
        }
        return index;
    }

    private void showChosenOne(T chosenOne) {
        System.out.println("You chose:");
        System.out.println(outputFormat.apply(chosenOne));
    }
}
