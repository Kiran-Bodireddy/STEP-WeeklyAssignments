package Week1;

import java.util.*;

public class MultiLevelCacheSystem {

    private static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private final LRUCache<String, String> L1 = new LRUCache<>(10000);
    private final LRUCache<String, String> L2 = new LRUCache<>(100000);
    private final Map<String, String> L3 = new HashMap<>();
    private final Map<String, Integer> accessCount = new HashMap<>();

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;
    private int totalRequests = 0;

    public MultiLevelCacheSystem() {
        for (int i = 1; i <= 200000; i++) {
            L3.put("video_" + i, "VideoData_" + i);
        }
    }

    public String getVideo(String videoId) {
        totalRequests++;

        if (L1.containsKey(videoId)) {
            l1Hits++;
            return L1.get(videoId);
        }

        if (L2.containsKey(videoId)) {
            l2Hits++;
            String data = L2.get(videoId);
            promoteToL1(videoId, data);
            return data;
        }

        if (L3.containsKey(videoId)) {
            l3Hits++;
            String data = L3.get(videoId);
            L2.put(videoId, data);
            accessCount.put(videoId, 1);
            return data;
        }

        return null;
    }

    private void promoteToL1(String videoId, String data) {
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
        if (accessCount.get(videoId) >= 2) {
            L1.put(videoId, data);
        }
    }

    public void invalidate(String videoId) {
        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);
    }

    public void getStatistics() {
        double l1Rate = totalRequests == 0 ? 0 : (l1Hits * 100.0) / totalRequests;
        double l2Rate = totalRequests == 0 ? 0 : (l2Hits * 100.0) / totalRequests;
        double l3Rate = totalRequests == 0 ? 0 : (l3Hits * 100.0) / totalRequests;
        double overall = l1Rate + l2Rate + l3Rate;

        System.out.println("L1 Hit Rate: " + String.format("%.2f", l1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", l2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", l3Rate) + "%");
        System.out.println("Overall Hit Rate: " + String.format("%.2f", overall) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        System.out.println(cache.getVideo("video_123"));
        System.out.println(cache.getVideo("video_123"));
        System.out.println(cache.getVideo("video_999"));
        System.out.println(cache.getVideo("video_999"));
        System.out.println(cache.getVideo("video_999"));

        cache.getStatistics();
    }
}