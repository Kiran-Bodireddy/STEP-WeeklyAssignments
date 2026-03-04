package week1;

import java.util.*;
import java.util.concurrent.*;

public class RealTimeAnalyticsDashboard {

    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.merge(url, 1, Integer::sum);
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);
        trafficSources.merge(source, 1, Integer::sum);
    }

    public void getDashboard() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();
        while (!pq.isEmpty()) {
            topPages.add(pq.poll());
        }

        Collections.reverse(topPages);

        System.out.println("Top Pages:");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> source : trafficSources.entrySet()) {
            System.out.println(source.getKey() + " - " + source.getValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();

        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_789", "google");
        dashboard.processEvent("/sports/championship", "user_111", "direct");
        dashboard.processEvent("/sports/championship", "user_789", "google");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n===== DASHBOARD UPDATE =====");
            dashboard.getDashboard();
        }, 0, 5, TimeUnit.SECONDS);

        Thread.sleep(15000);
        scheduler.shutdown();
    }
}
