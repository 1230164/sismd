package threads_manual;

import sequential.Page;
import sequential.Words;

import java.util.List;
import java.util.Map;

public class Worker implements Runnable {

    private final List<Page> pages;
    private final Map<String, Integer> sharedMap;
    private boolean done = false;

    public Worker(List<Page> pages, Map<String, Integer> sharedMap) {
        this.pages = pages;
        this.sharedMap = sharedMap;
    }

    @Override
    public void run() {
        for (Page page : pages) {
            if (page == null || page.getText() == null) continue;

            Iterable<String> words = new Words(page.getText());
            for (String word : words) {
                if (word.length() > 1 && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("I")) {
                    sharedMap.merge(word, 1, Integer::sum);
                }
            }
        }
        done = true;
    }

    public boolean isDone() {
        return done;
    }
}
