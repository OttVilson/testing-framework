package com.vilson.runner.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ForIntroduction {

    static void printIntroduction() {
        ForIntroduction fi = new ForIntroduction();
        ClassLoader loader = fi.getClass().getClassLoader();
        InputStream is = loader.getResourceAsStream("forIntroduction");
        if (is != null) {
            printLinesFromFile(is);
        }
    }

    private static void printLinesFromFile(InputStream is) {
        try (InputStream inputStream = is;
             InputStreamReader isr = new InputStreamReader(inputStream);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
