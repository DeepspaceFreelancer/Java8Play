package com.yvr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {

    static List<String> getWordsFromFile(final String fileName_in) throws FileNotFoundException {
        List<String> wordList = new ArrayList<String>();
        try (Scanner s = new Scanner(new File(fileName_in))) {
            while (s.hasNext()) {
                wordList.add(s.next());
            }
        }
        return wordList;
    }

    static long measureExecution(final Runnable runnable) {
        final long before = System.currentTimeMillis();
        runnable.run();
        final long after = System.currentTimeMillis();
        return after - before;
    }

    static final int THREAD_COUNT_LIMIT = (Runtime.getRuntime().availableProcessors() >> 1);
}
