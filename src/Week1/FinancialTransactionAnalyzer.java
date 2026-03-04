package Week1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FinancialTransactionAnalyzer {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        LocalDateTime time;

        public Transaction(int id, int amount, String merchant, String account, LocalDateTime time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<String> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                Transaction match = map.get(complement);
                result.add("(" + match.id + ", " + t.id + ")");
            }
            map.put(t.amount, t);
        }

        return result;
    }

    public List<String> findTwoSumWithinOneHour(int target) {
        Map<Integer, List<Transaction>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction prev : map.get(complement)) {
                    if (Math.abs(Duration.between(prev.time, t.time).toMinutes()) <= 60) {
                        result.add("(" + prev.id + ", " + t.id + ")");
                    }
                }
            }
            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int k, int target, List<Integer> current, List<List<Integer>> result) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || target < 0) return;

        for (int i = start; i < transactions.size(); i++) {
            current.add(transactions.get(i).id);
            backtrack(i + 1, k - 1, target - transactions.get(i).amount, current, result);
            current.remove(current.size() - 1);
        }
    }

    public List<String> detectDuplicates() {
        Map<String, Map<Integer, Set<String>>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            map
                    .computeIfAbsent(t.merchant, k -> new HashMap<>())
                    .computeIfAbsent(t.amount, k -> new HashSet<>())
                    .add(t.account);
        }

        for (String merchant : map.keySet()) {
            for (int amount : map.get(merchant).keySet()) {
                Set<String> accounts = map.get(merchant).get(amount);
                if (accounts.size() > 1) {
                    result.add("{amount:" + amount + ", merchant:" + merchant +
                            ", accounts:" + accounts + "}");
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {

        FinancialTransactionAnalyzer analyzer = new FinancialTransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA", "acc1",
                LocalDateTime.now().minusMinutes(50)));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB", "acc2",
                LocalDateTime.now().minusMinutes(40)));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC", "acc3",
                LocalDateTime.now().minusMinutes(30)));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA", "acc2",
                LocalDateTime.now().minusMinutes(20)));

        System.out.println("Two Sum (500): " + analyzer.findTwoSum(500));
        System.out.println("Two Sum Within 1 Hour (500): " + analyzer.findTwoSumWithinOneHour(500));
        System.out.println("K-Sum (k=3, target=1000): " + analyzer.findKSum(3, 1000));
        System.out.println("Duplicates: " + analyzer.detectDuplicates());
    }
}