package completablefuture;


import sequential.Page;
import sequential.Words;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordProcessor {

    public static Map<String, Integer> countWords(List<Page> pages) {
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

    public static Map<String, Integer> merge(Map<String, Integer> m1, Map<String, Integer> m2) {
        Map<String, Integer> merged = new HashMap<>(m1);
        for (Map.Entry<String, Integer> entry : m2.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return merged;
    }
}
