package com.yvr;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class LambdaPlay {

    /*  Chapter 01 Exercise 02
        Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class, write a method that returns all
        subdirectories of a given directory. Use a lambda expression instead of a FileFilter object. Repeat with a method expression.
    */
    public List<File> ch01Ex02(final String directory_in) {
        List<File> result = Arrays.stream((new File(directory_in)).listFiles()).filter(x -> x.isDirectory()).collect(Collectors.toList());
        return result;
    }

    /*  Chapter 02 Exercise 03
        Measure the difference when counting long words with a parallelStream instead of a stream.
        Call System.currentTimeMillis before and after the call,
        and print the difference. Switch to a larger document (such as War and Peace) if you have a fast computer.
    */
    public void ch02Ex03_OrderA(final String file_in) throws FileNotFoundException {
        final List<String> words = Util.getWordsFromFile(file_in);
        final int bigWordLimit = 10;

        final List<String> words4ParallelWithTimeLimit = new ArrayList<>(words);
        long parallelWithThreadLimitTime = Util.measureExecution(() -> {
            ForkJoinPool forkJoinPool = new ForkJoinPool(Util.THREAD_COUNT_LIMIT);
            try {
                forkJoinPool.submit(() -> {
                    final long wordCount = words4ParallelWithTimeLimit.parallelStream().filter(x -> x.length() > bigWordLimit).count();
                }).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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

    public void ch02Ex03_OrderB(final String file_in) throws FileNotFoundException {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("linearTime: %d, parallelTime: %d, delta: %d, parallelWithThreadLimitTime: %d%n", linearTime, parallelTime, linearTime - parallelTime, parallelWithThreadLimitTime);

    }

    /*
        Enhance the lazy logging technique by providing conditional logging.
        A typical call would be logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]).
        Don’t evaluate the condition if the logger won’t log the message.
     */
    public void ch03Ex01() {
        logIf(false, () -> System.out.println("Do not write out"));
        logIf(true, () -> System.out.println("Write out"));

        try {
            logIf2(false, () -> {
                System.out.println("Do not write out");
                return 0;
            });
            logIf2(true, () -> {
                System.out.println("Write out");
                return 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logIf(final boolean flag_in, Runnable runnable_in) {
        if (flag_in) {
            runnable_in.run();
        }
    }

    private void logIf2(final boolean flag_in, Callable<Integer> callable_in) throws Exception {
        if (flag_in) {
            callable_in.call();
        }
    }

    public void ch06_01_AtomicValues() {
        AtomicLong atomic = new AtomicLong();
        long newValue = atomic.incrementAndGet();
        System.out.printf("After increment new value: %d%n", newValue);

        newValue = atomic.updateAndGet(x -> Math.max(x, 0));
        System.out.printf("Trying to update 1 new value: %d%n", newValue);
        newValue = atomic.updateAndGet(x -> Math.max(x, 2));
        System.out.printf("Trying to update 2 new value: %d%n", newValue);

        long beforeValue = atomic.getAndUpdate(x -> Math.max(x, 1));
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomic.get());
        beforeValue = atomic.getAndUpdate(x -> Math.max(x, 3));
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomic.get());

        newValue = atomic.accumulateAndGet(2, Math::max);
        System.out.printf("Trying to update 1 new value: %d%n", newValue);
        newValue = atomic.accumulateAndGet(4, Math::max);
        System.out.printf("Trying to update 2 new value: %d%n", newValue);

        beforeValue = atomic.getAndAccumulate(3, Math::max);
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomic.get());
        beforeValue = atomic.getAndAccumulate(5, Math::max);
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomic.get());

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final LongAdder adder = new LongAdder();
        for (int i = 0; i < 10; ++i) {
            executor.submit(() -> {
                adder.increment();
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final long total = adder.sum();
        System.out.printf("Total: %d%n", total);
    }

    /*
        Write a program that asks the user for a URL, then reads the web page at that URL, and then displays all the links.
        Use a CompletableFuture for each stage. Don’t call get.
        To prevent your program from terminating prematurely, call ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);

        Itt lesz az igazan jo megoldas:
        https://github.com/galperin/Solutions-for-exercises-from-Java-SE-8-for-the-Really-Impatient-by-Horstmann/blob/master/src/main/java/de/galperin/javase8/capitel6/C6E10.java
     */
    public void ch06ex10() throws Exception {
        final URL url = new URL("https://stackoverflow.com/");
        CompletableFuture<List<String>> firstBatch = getUrl(url);
        CompletableFuture<List<String>> secondBatch = firstBatch.thenCompose(this::getLinks);

        //ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
        for (int i = 0; i < 20; ++i) {
            System.out.println("I am doing something else!");
        }

        final List<String> resultList = secondBatch.get();
        for (final String line : resultList) {
            System.out.println(line);
        }
    }

    CompletableFuture<List<String>> getUrl(final URL url) throws Exception {
        try (final InputStream is = url.openStream()) {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<String> result = br.lines().collect(Collectors.toList());
                return CompletableFuture.supplyAsync(() -> result);
            }
        }
    }

    CompletableFuture<List<String>> getLinks(final List<String> webpage) {
        final List<String> result = new ArrayList<>();
        for (final String line : webpage) {
            if (line.contains("a href=")) {
                result.add(line);
            }
        }
        return CompletableFuture.supplyAsync(() -> result);
    }
}
