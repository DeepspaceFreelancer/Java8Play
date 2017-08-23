package com.yvr.ch02;

import com.yvr.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class Exercises {

    //region Chapter 02 Exercise 03 ------------------------------------------------------------------------------------
    /*  Chapter 02 Exercise 03
        Measure the difference when counting long words with a parallelStream instead of a stream.
        Call System.currentTimeMillis before and after the call,
        and print the difference. Switch to a larger document (such as War and Peace) if you have a fast computer.
    */
    public void ch02Ex03_v1_StepExecutionOrderA(final String file_in) throws FileNotFoundException {
        final List<String> words = Util.getWordsFromFile(file_in);
        final int bigWordLimit = 10;

        final List<String> words4ParallelWithTimeLimit = new ArrayList<>(words);
        long parallelWithThreadLimitTime = Util.measureExecution(() -> {
            ForkJoinPool forkJoinPool = new ForkJoinPool(Util.THREAD_COUNT_LIMIT);
            try {
                forkJoinPool.submit(() -> {
                    final long wordCount = words4ParallelWithTimeLimit.parallelStream().filter(x -> x.length() > bigWordLimit).count();
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        final List<String> words4Parallel = new ArrayList<>(words);
        long parallelTime = Util.measureExecution(() -> {
            final long wordCount = words4Parallel.parallelStream().filter(x -> x.length() > bigWordLimit).count();
        });

        final List<String> words4linear = new ArrayList<>(words);
        long linearTime = Util.measureExecution(() -> {
            final long wordCount = words4linear.stream().filter(x -> x.length() > bigWordLimit).count();
        });

        System.out.printf("linearTime: %d, parallelTime: %d, delta: %d, parallelWithThreadLimitTime: %d%n", linearTime, parallelTime, linearTime - parallelTime, parallelWithThreadLimitTime);
    }

    public void ch02Ex03_v1_StepExecutionOrderB(final String file_in) throws FileNotFoundException {
        final List<String> words = Util.getWordsFromFile(file_in);
        final int bigWordLimit = 10;

        final List<String> words4linear = new ArrayList<>(words);
        long linearTime = Util.measureExecution(() -> {
            final long wordCount = words4linear.stream().filter(x -> x.length() > bigWordLimit).count();
        });

        final List<String> words4Parallel = new ArrayList<>(words);
        long parallelTime = Util.measureExecution(() -> {
            final long wordCount = words4Parallel.parallelStream().filter(x -> x.length() > bigWordLimit).count();
        });

        final List<String> words4ParallelWithTimeLimit = new ArrayList<>(words);
        long parallelWithThreadLimitTime = Util.measureExecution(() -> {
            ForkJoinPool forkJoinPool = new ForkJoinPool(Util.THREAD_COUNT_LIMIT);
            try {
                forkJoinPool.submit(() -> {
                    final long wordCount = words4ParallelWithTimeLimit.parallelStream().filter(x -> x.length() > bigWordLimit).count();
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("linearTime: %d, parallelTime: %d, delta: %d, parallelWithThreadLimitTime: %d%n", linearTime, parallelTime, linearTime - parallelTime, parallelWithThreadLimitTime);
    }
    //endregion Chapter 02 Exercise 03 ---------------------------------------------------------------------------------
}
