package week1;

import java.util.*;

public class SocialMediaUsernameAvailabilityChecker {

    private Map<String, Integer> userDatabase = new HashMap<>();
    private Map<String, Integer> attemptFrequency = new HashMap<>();

    public SocialMediaUsernameAvailabilityChecker() {
        userDatabase.put("john_doe", 1);
        userDatabase.put("admin", 2);
        userDatabase.put("alex99", 3);
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !userDatabase.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        if (!userDatabase.containsKey(username)) {
            suggestions.add(username);
            return suggestions;
        }

        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!userDatabase.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        if (username.contains("_")) {
            String modified = username.replace("_", ".");
            if (!userDatabase.containsKey(modified)) {
                suggestions.add(modified);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {
        return Collections.max(attemptFrequency.entrySet(),
                Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) {
        SocialMediaUsernameAvailabilityChecker checker =
                new SocialMediaUsernameAvailabilityChecker();

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));
        System.out.println(checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println(checker.getMostAttempted());
    }
}