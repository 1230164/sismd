package forkjoin_framework;

import sequential.Page;
import sequential.Words;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class WordCounterTask extends RecursiveTask<Map<String, Integer>> {
    private static final int LIMIT = 500;
    private final List<Page> pages;

    public WordCounterTask(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    protected Map<String, Integer> compute() {
        if (pages.size() <= LIMIT) {
            // contar diretamente
            return countWords(pages);
        } else {
            // dividir em duas partes
            int mid = pages.size() / 2;
            WordCounterTask left = new WordCounterTask(pages.subList(0, mid));
            WordCounterTask right = new WordCounterTask(pages.subList(mid, pages.size()));

            left.fork();
            Map<String, Integer> rightResult = right.compute();
            Map<String, Integer> leftResult = left.join();

            return merge(leftResult, rightResult);
        }
    }

    private Map<String, Integer> countWords(List<Page> pages) {
        Map<String, Integer> map = new HashMap<>();

        for (Page page : pages) {
            if (page == null || page.getText() == null) continue;

            for (String word : new Words(page.getText())) {
                if (word.length() > 1 && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("I")) {
                    map.merge(word, 1, Integer::sum);
                }
            }
        }

        return map;
    }

    private Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> merged = new HashMap<>(map1);
        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return merged;
    }
}