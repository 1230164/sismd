package completablefuture;

import sequential.Page;
import sequential.Pages;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordCountCompletable {
    static final int maxPages = 100000;
    static final String fileName = "inputs/enwiki-20250201-pages-articles-multistream1.xml";
    static final int numThreads = 4;

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        List<Page> allPages = new ArrayList<>();
        Iterator<Page> it = new Pages(maxPages, fileName).iterator();

        while (it.hasNext()) {
            Page p = it.next();
            if (p != null) allPages.add(p);
        }

        int chunkSize = allPages.size() / numThreads;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Map<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int startIdx = i * chunkSize;
            int endIdx = (i == numThreads - 1) ? allPages.size() : (i + 1) * chunkSize;
            List<Page> sublist = allPages.subList(startIdx, endIdx);
            CompletableFuture<Map<String, Integer>> future =
                    CompletableFuture.supplyAsync(() -> WordProcessor.countWords(sublist), pool);
            futures.add(future);
        }

        CompletableFuture<Map<String, Integer>> combinedFuture = futures.get(0);
        for (int i = 1; i < futures.size(); i++) {
            combinedFuture = combinedFuture.thenCombine(futures.get(i), WordProcessor::merge);
        }

        Map<String, Integer> result = combinedFuture.join();
        pool.shutdown();

        long end = System.currentTimeMillis();
        System.out.println("Processed pages: " + allPages.size());
        System.out.println("Elapsed time: " + (end - start) + " ms");

        result.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(e ->
                        System.out.println("Word: '" + e.getKey() + "' with total " + e.getValue() + " occurrences!"));
    }
}