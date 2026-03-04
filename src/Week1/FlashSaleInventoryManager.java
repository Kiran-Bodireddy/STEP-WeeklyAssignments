package week1;

import java.util.*;

public class FlashSaleInventoryManager {

    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, LinkedList<Integer>> waitingList = new HashMap<>();

    public FlashSaleInventoryManager() {
        inventory.put("IPHONE15_256GB", 100);
        waitingList.put("IPHONE15_256GB", new LinkedList<>());
    }

    public synchronized int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    public synchronized String purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {
            inventory.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        } else {
            waitingList.get(productId).add(userId);
            int position = waitingList.get(productId).size();
            return "Added to waiting list, position #" + position;
        }
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager =
                new FlashSaleInventoryManager();

        System.out.println("Stock: " +
                manager.checkStock("IPHONE15_256GB"));

        for (int i = 1; i <= 102; i++) {
            System.out.println(manager.purchaseItem("IPHONE15_256GB", i));
        }
    }
}
