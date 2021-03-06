package com.yvr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {

    public static final int THREAD_COUNT_LIMIT = (Runtime.getRuntime().availableProcessors() >> 1);

    private static class WordHolder {
        private static final List<String> WORDS = getWords();

        static List<String> getWords() {
            final String aTextFile = "resources/text.txt";
            try {
                return Util.getWordsFromFile(aTextFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File was not found: " + aTextFile, e);
            }
        }
    }

    public static List<String> getWords() {
        return WordHolder.getWords();
    }

    public static List<String> getWordsFromFile(final String fileName_in) throws FileNotFoundException {
        List<String> wordList = new ArrayList<String>();
        try (Scanner s = new Scanner(new File(fileName_in))) {
            while (s.hasNext()) {
                wordList.add(s.next());
            }
        }
        return wordList;
    }

    public static long measureExecution(final Runnable runnable) {
        final long before = System.currentTimeMillis();
        runnable.run();
        final long after = System.currentTimeMillis();
        return after - before;
    }
}
