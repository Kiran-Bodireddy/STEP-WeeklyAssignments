package week1;

import java.util.*;

public class PlagiarismDetectionSystem {

    private static final int N_GRAM_SIZE = 5;


    private Map<String, Set<String>> nGramIndex = new HashMap<>();


    private Map<String, String> documents = new HashMap<>();

    public void addDocument(String documentId, String content) {
        documents.put(documentId, content);

        List<String> nGrams = generateNGrams(content);

        for (String nGram : nGrams) {
            nGramIndex
                    .computeIfAbsent(nGram, k -> new HashSet<>())
                    .add(documentId);
        }
    }
    public void analyzeDocument(String documentId, String content) {

        List<String> nGrams = generateNGrams(content);
        System.out.println("Extracted " + nGrams.size() + " n-grams");

        Map<String, Integer> matchCount = new HashMap<>();

        for (String nGram : nGrams) {
            if (nGramIndex.containsKey(nGram)) {
                for (String matchedDoc : nGramIndex.get(nGram)) {
                    matchCount.put(matchedDoc,
                            matchCount.getOrDefault(matchedDoc, 0) + 1);
                }
            }
        }

        for (String matchedDoc : matchCount.keySet()) {
            int matches = matchCount.get(matchedDoc);
            double similarity = (matches * 100.0) / nGrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + matchedDoc + "\"");
            System.out.printf("Similarity: %.2f%%\n", similarity);

            if (similarity > 60) {
                System.out.println("⚠ PLAGIARISM DETECTED\n");
            } else if (similarity > 15) {
                System.out.println("Suspicious\n");
            } else {
                System.out.println("Low similarity\n");
            }
        }
    }
    private List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
        List<String> nGrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N_GRAM_SIZE; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N_GRAM_SIZE; j++) {
                sb.append(words[i + j]).append(" ");
            }
            nGrams.add(sb.toString().trim());
        }

        return nGrams;
    }

    public static void main(String[] args) {

        PlagiarismDetectionSystem detector = new PlagiarismDetectionSystem();

        detector.addDocument("essay_089.txt",
                "Artificial intelligence is transforming the world with powerful technologies.");

        detector.addDocument("essay_092.txt",
                "Artificial intelligence is transforming the world with powerful technologies and automation systems.");

        String newEssay =
                "Artificial intelligence is transforming the world with powerful technologies and automation systems in education.";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}
