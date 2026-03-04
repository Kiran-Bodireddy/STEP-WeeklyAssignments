package Week1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedRateLimiter {

    private static class TokenBucket {
        private final int maxTokens;
        private final double refillRatePerMillis;
        private double tokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens) {
            this.maxTokens = maxTokens;
            this.refillRatePerMillis = maxTokens / 3600000.0;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            double refillAmount = elapsed * refillRatePerMillis;
            tokens = Math.min(maxTokens, tokens + refillAmount);
            lastRefillTime = now;
        }

        public synchronized boolean allowRequest() {
            refill();
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        }

        public synchronized int getRemainingTokens() {
            refill();
            return (int) tokens;
        }

        public synchronized long getResetTimeSeconds() {
            refill();
            double needed = maxTokens - tokens;
            return (long) (needed / refillRatePerMillis / 1000);
        }
    }

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final int LIMIT = 1000;

    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(LIMIT));
        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            System.out.println("Denied (0 requests remaining, retry after "
                    + bucket.getResetTimeSeconds() + "s)");
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        if (bucket == null) {
            System.out.println("{used: 0, limit: 1000}");
            return;
        }

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT - remaining;

        System.out.println("{used: " + used +
                ", limit: " + LIMIT +
                ", reset: " + bucket.getResetTimeSeconds() + "s}");
    }

    public static void main(String[] args) {
        DistributedRateLimiter limiter = new DistributedRateLimiter();
        String client = "abc123";

        for (int i = 0; i < 1005; i++) {
            limiter.checkRateLimit(client);
        }

        limiter.getRateLimitStatus(client);
    }
}