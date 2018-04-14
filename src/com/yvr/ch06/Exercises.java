package com.yvr.ch06;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Exercises {

    //region Chapter 06 Lectures ---------------------------------------------------------------------------------------
    // Chapter 06 Lecture 01
    public void ch06_01_AtomicValues() {
        AtomicLong atomicLong = new AtomicLong();
        long newValue = atomicLong.incrementAndGet();
        System.out.printf("After increment new value: %d%n", newValue);

        newValue = atomicLong.updateAndGet(x -> Math.max(x, 0));
        System.out.printf("Trying to update 1 new value: %d%n", newValue);
        newValue = atomicLong.updateAndGet(x -> Math.max(x, 2));
        System.out.printf("Trying to update 2 new value: %d%n", newValue);

        long beforeValue = atomicLong.getAndUpdate(x -> Math.max(x, 1));
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomicLong.get());
        beforeValue = atomicLong.getAndUpdate(x -> Math.max(x, 3));
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomicLong.get());

        newValue = atomicLong.accumulateAndGet(2, Math::max);
        System.out.printf("Trying to update 1 new value: %d%n", newValue);
        newValue = atomicLong.accumulateAndGet(4, Math::max);
        System.out.printf("Trying to update 2 new value: %d%n", newValue);

        beforeValue = atomicLong.getAndAccumulate(3, Math::max);
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomicLong.get());
        beforeValue = atomicLong.getAndAccumulate(5, Math::max);
        System.out.printf("Trying to get and update old value: %d, new value: %d%n", beforeValue, atomicLong.get());

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final LongAdder longAdder = new LongAdder();
        for (int i = 0; i < 10; ++i) {
            executor.submit(() -> {
                longAdder.increment();
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final long total = longAdder.sum();
        System.out.printf("Total: %d%n", total);
    }

    public void ch06_02_ConcurrentHashMap() {

        final String key = "key";
        // Thread safe increment on a ConcurrentHashMap + AtomicLong;
        {
            Map<String, AtomicLong> concurrentHashMap = new ConcurrentHashMap<>();
            concurrentHashMap.putIfAbsent(key, new AtomicLong());
            concurrentHashMap.get(key).incrementAndGet();
        }

        // Thread safe increment on a ConcurrentHashMap + LongAdder;
        {
            Map<String, LongAdder> concurrentHashMap = new ConcurrentHashMap<>();
            concurrentHashMap.putIfAbsent(key, new LongAdder());
            concurrentHashMap.get(key).increment();
        }

        // Better ways of writing something
        {
            Map<String, AtomicInteger> concurrentHashMap = new ConcurrentHashMap<>();

            // This is definitely bad!
            if (!concurrentHashMap.containsKey(key)) {
                concurrentHashMap.put(key, new AtomicInteger());
            }

            // This is the good way:
            concurrentHashMap.putIfAbsent(key, new AtomicInteger());

            // It also works with simple Hash too, however it is not worthy, because putIfAbsent and computeIfAbsent difference
            Map<String, List<String>> map = new HashMap<>();
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add("This is the way to do it!");

            // This is a very bad move, because fn will be evaluated! Wrong!
            concurrentHashMap.putIfAbsent(key, ch06_02_HelperMethod_newInstance());               // Bad
            // This is the right way of doing it
            concurrentHashMap.computeIfAbsent(key, k -> ch06_02_HelperMethod_newInstance());      // Right
            concurrentHashMap.computeIfAbsent(key, k -> {
                throw new RuntimeException("ch06_02_ConcurrentHashMap, Bad call");
            });      // Right

            // Small demonstrative code, to show that k is the key
            concurrentHashMap.computeIfAbsent(UUID.randomUUID().toString(), k -> {
                System.out.println("k is the key: " + k);
                return new AtomicInteger();
            });      // Right

            // Simple Hashmap of integer compute
            Map<String, Integer> mapOfIntegers = new HashMap<>();
            mapOfIntegers.compute(key, (k, v) -> v == null ? 0 : v + 1);
            mapOfIntegers.compute(key, (k, v) -> v == null ? 0 : v + 1);
            mapOfIntegers.compute(key, (k, v) -> v == null ? 0 : v + 1);
            System.out.println("Map of Integer compute demo: " + mapOfIntegers.get(key)); // This will output 2

            // Compute if present demostration
            mapOfIntegers.computeIfPresent(key, (k, v) -> v + 1);
            concurrentHashMap.computeIfPresent(UUID.randomUUID().toString(), (k, v) -> {
                throw new RuntimeException("ch06_02_ConcurrentHashMap, Second Bad call");
            });

            // Demonstration of merge So instead of that one can have
            final String newItem = "A new item";
            if (map.containsKey(key)) {
                map.get(key).add(newItem);
            } else {
                map.put(key, Collections.singletonList(newItem));
            }

            map.merge(key, Collections.singletonList(newItem), (list1, list2) ->
                Stream.of(list1, list2)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
        }
    }

    private AtomicInteger ch06_02_HelperMethod_newInstance() {
        System.out.println("ch06_02_HelperMethod_newInstance function got ran");
        return new AtomicInteger();
    }

    //endregion Chapter 06 Lectures ------------------------------------------------------------------------------------

    //region Chapter 06 Exercise 10 ------------------------------------------------------------------------------------
    /* Chapter 06 Exercise 10
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
    //endregion Chapter 06 Exercise 10 ---------------------------------------------------------------------------------
}
