package threads_manual;


import sequential.Page;
import sequential.Pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WordCountThreads {

    static final int maxPages = 100000;
    static final String fileName = "inputs/enwiki-20250201-pages-articles-multistream1.xml";
    static final int numThreads = 10;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        List<Page> pageList = new ArrayList<>();
        Iterator<Page> it = new Pages(maxPages, fileName).iterator();
        while (it.hasNext()) {
            Page p = it.next();
            if (p != null) {
                pageList.add(p);
            }
        }

        int chunkSize = pageList.size() / numThreads;
        List<Worker> workers = new ArrayList<>();
        Map<String, Integer> sharedMap = new ConcurrentHashMap<>();

        for (int i = 0; i < numThreads; i++) {
            int startIdx = i * chunkSize;
            int endIdx = (i == numThreads - 1) ? pageList.size() : (i + 1) * chunkSize;
            List<Page> sublist = pageList.subList(startIdx, endIdx);
            Worker w = new Worker(sublist, sharedMap);
            workers.add(w);
            new Thread(w).start();
        }

        for (Worker w : workers) {
            while (!w.isDone()) {
                Thread.sleep(10);
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Processed pages: " + pageList.size());
        System.out.println("Elapsed time: " + (end - start) + "ms");

        sharedMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> System.out.println("Word: '" + e.getKey() + "' with total " + e.getValue() + " occurrences!"));
    }

}
