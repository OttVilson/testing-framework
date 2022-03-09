package com.vilson.runner.cli.chooser;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

class Chooser<T> {

    final List<T> availableOptions;
    final Scanner scanner;
    final Function<T, String> outputFormat;

    Chooser(List<T> availableOptions, Scanner scanner, Function<T, String> outputFormat) {
        this.availableOptions = availableOptions;
        this.scanner = scanner;
        this.outputFormat = outputFormat;
    }

    void showAvailableOptions() {
        System.out.println();
        System.out.println(String.format("The available %ss are",
                availableOptions.get(0).getClass().getSimpleName()));
        printList(availableOptions);
    }

    void printList(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            printRow(i, list.get(i));
        }
    }

    private void printRow(int index, T element) {
        System.out.print(index + 1);
        System.out.print(")\t");
        System.out.println(outputFormat.apply(element));
    }

    Integer minusOneToGetZeroBasedIndex(Integer i) {
        return i - 1;
    }
}
