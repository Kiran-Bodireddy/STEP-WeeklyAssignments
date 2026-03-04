package Week1;

import java.util.*;

public class AutocompleteSystem {

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
    }

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> frequencyMap = new HashMap<>();

    public void addQuery(String query) {
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        collect(prefix, node, results);

        PriorityQueue<String> minHeap = new PriorityQueue<>(
                (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
        );

        for (String s : results) {
            minHeap.offer(s);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<String> topResults = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            topResults.add(minHeap.poll());
        }

        Collections.reverse(topResults);
        return topResults;
    }

    private void collect(String prefix, TrieNode node, List<String> results) {
        if (node.isEnd) {
            results.add(prefix);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collect(prefix + entry.getKey(), entry.getValue(), results);
        }
    }

    public void updateFrequency(String query) {
        addQuery(query);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");

        List<String> suggestions = system.search("jav");

        System.out.println("Top Suggestions:");
        for (String s : suggestions) {
            System.out.println(s + " (" + system.frequencyMap.get(s) + " searches)");
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter Trending Update:");
        suggestions = system.search("jav");

        for (String s : suggestions) {
            System.out.println(s + " (" + system.frequencyMap.get(s) + " searches)");
        }
    }
}