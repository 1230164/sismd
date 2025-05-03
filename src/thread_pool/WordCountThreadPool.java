package thread_pool;

import sequential.Page;
import sequential.Pages;
import sequential.Words;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public class WordCountThreadPool {

    static final int maxPages = 100000;
    static final String fileName = "InputFiles/inputFile.xml";
    static final int THREADS = Runtime.getRuntime().availableProcessors();
    static final int BATCH_SIZE = 10; // Páginas por tarefa

    private static final ConcurrentHashMap<String, AtomicInteger> counts = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        long start = System.currentTimeMillis();
        Pages pages = new Pages(maxPages, fileName);

        int processedPages = 0;
        List<Future<?>> futures = new ArrayList<>();
        List<Page> batch = new ArrayList<>(BATCH_SIZE);

        for (Page page : pages) {
            if (page == null) break;

            batch.add(page);
            processedPages++;

            if (batch.size() == BATCH_SIZE) {
                submitBatch(new ArrayList<>(batch), pool, futures);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            submitBatch(batch, pool, futures);
        }

        for (Future<?> f : futures) {
            f.get();
        }

        pool.shutdown();

        long end = System.currentTimeMillis();
        System.out.println("Processed pages: " + processedPages);
        System.out.println("Elapsed time: " + (end - start) + "ms");

        counts.entrySet().stream()
                .sorted(Map.Entry.<String, AtomicInteger>comparingByValue(Comparator.comparingInt(AtomicInteger::get)).reversed())
                .limit(10)
                .forEach(e -> System.out.println("Word: '" + e.getKey() + "' with total " + e.getValue() + " occurrences!"));
    }

    private static void submitBatch(List<Page> pages, ExecutorService pool, List<Future<?>> futures) {
        Future<?> f = pool.submit(() -> {
            for (Page page : pages) {
                for (String word : new Words(page.getText())) {
                    word = word.trim().toLowerCase();
                    if (word.matches("[a-zA-Z]+")) {
                        counts.computeIfAbsent(word, k -> new AtomicInteger(0)).incrementAndGet();
                    }
                }
            }
        });
        futures.add(f);
    }
}
