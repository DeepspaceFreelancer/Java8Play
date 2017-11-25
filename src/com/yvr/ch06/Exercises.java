package com.yvr.ch06;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Exercises {

    //region Chapter 06 Lectures ---------------------------------------------------------------------------------------
    // Chapter 06 Lecture 01
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
    //endregion Chapter 06 Lectures ------------------------------------------------------------------------------------

    //region Chapter 06 Exercise 01 ------------------------------------------------------------------------------------
    /* Chapter 06 Exercise 01
        Write a program that asks the user for a URL, then reads the web page at that URL, and then displays all the links.
        Use a CompletableFuture for each stage. Don’t call get.
        To prevent your program from terminating prematurely, call ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
    */
    public void ch06ex10_v1() throws Exception {
        final URL url = new URL("https://stackoverflow.com/");
        CompletableFuture<List<String>> firstBatch = getUrl(url);
        CompletableFuture<List<String>> secondBatch = firstBatch.thenCompose(this::getLinks);

        for (int i = 0; i < 20; ++i) {
            System.out.println("I am doing something else!");
        }

        final List<String> resultList = secondBatch.get();
        for (final String line : resultList) {
            System.out.println(line);
        }
    }

    private CompletableFuture<List<String>> getUrl(final URL url) throws Exception {
        try (final InputStream is = url.openStream()) {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<String> result = br.lines().collect(Collectors.toList());
                return CompletableFuture.supplyAsync(() -> result);
            }
        }
    }

    private CompletableFuture<List<String>> getLinks(final List<String> webpage) {
        final List<String> result = new ArrayList<>();
        for (final String line : webpage) {
            if (line.contains("a href=")) {
                result.add(line);
            }
        }
        return CompletableFuture.supplyAsync(() -> result);
    }

    public void ch06ex10_v2() throws Exception {
        CompletableFuture.supplyAsync(() -> readPage("https://stackoverflow.com/"))
                .thenApply(this::getLinks)
                .handle((l, e) -> {
                    if (e != null) {
                        System.out.println(e.getMessage());
                        return new ArrayList<>();
                    } else {
                        return l;
                    }
                })
                .thenAccept(l -> l.forEach(System.out::println));
        ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
    }

    private String readPage(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
            URLConnection conn = url.openConnection();
            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    content.append(inputLine);
                }
            }
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getLinks(String content) {
        List<String> links = new ArrayList<>();
        Pattern p = Pattern.compile("(?i)href=\"http://(.*?)\"");
        Matcher m = p.matcher(content);
        while (m.find()) {
            links.add(m.group(1));
        }
        return links;
    }

}
