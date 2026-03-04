package week1;

import java.util.*;

public class DNSCacheWithTTL {

    private static class DNSEntry {
        String ipAddress;
        long expiryTime;

        DNSEntry(String ipAddress, long ttlSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int MAX_CACHE_SIZE = 5;

    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };

    private int hits = 0;
    private int misses = 0;

    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null) {
            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT");
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        misses++;
        System.out.println("Cache MISS - Querying upstream");

        String newIp = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(newIp, 5));

        return newIp;
    }

    private String queryUpstreamDNS(String domain) {
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCacheWithTTL dnsCache = new DNSCacheWithTTL();

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));

        Thread.sleep(6000);

        System.out.println(dnsCache.resolve("google.com"));

        dnsCache.getCacheStats();
    }
}
