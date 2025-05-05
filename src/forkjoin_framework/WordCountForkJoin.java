package forkjoin_framework;

import sequential.Pages;
import sequential.Page;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class WordCountForkJoin {
    static final int maxPages = 100;
    static final String fileName = "src/inputs/enwiki-latest-pages-articles-multistream1.xml-p1p41242";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        List<Page> allPages = new ArrayList<>();
        for (Page page : new Pages(maxPages, fileName)) {
            if (page != null) {
                allPages.add(page);
            }
        }

        ForkJoinPool pool = new ForkJoinPool();
        WordCounterTask task = new WordCounterTask(allPages);
        Map<String, Integer> finalResult = pool.invoke(task);

        long end = System.currentTimeMillis();
        System.out.println("Processed pages: " + allPages.size());
        System.out.println("Elapsed time: " + (end - start) + " ms");

        finalResult.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry ->
                        System.out.println("Word: '" + entry.getKey() + "' with total " + entry.getValue() + " occurrences!")
                );
    }
}