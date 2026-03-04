package Week1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLotOpenAddressing {

    private enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    private static class ParkingSpot {
        String licensePlate;
        LocalDateTime entryTime;
        Status status;

        ParkingSpot() {
            this.status = Status.EMPTY;
        }
    }

    private final ParkingSpot[] table;
    private final int capacity;
    private int size = 0;
    private int totalProbes = 0;
    private int totalOperations = 0;
    private final Map<Integer, Integer> hourlyEntries = new HashMap<>();

    public ParkingLotOpenAddressing(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    public void parkVehicle(String licensePlate) {
        if (size >= capacity) {
            System.out.println("Parking Full");
            return;
        }

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = LocalDateTime.now();
        table[index].status = Status.OCCUPIED;

        size++;
        totalProbes += probes;
        totalOperations++;

        int hour = LocalDateTime.now().getHour();
        hourlyEntries.put(hour, hourlyEntries.getOrDefault(hour, 0) + 1);

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status != Status.EMPTY) {
            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(licensePlate)) {

                Duration duration = Duration.between(
                        table[index].entryTime,
                        LocalDateTime.now()
                );

                long minutes = duration.toMinutes();
                double fee = (minutes / 60.0) * 5.0;

                table[index].status = Status.DELETED;
                table[index].licensePlate = null;
                table[index].entryTime = null;

                size--;

                System.out.println("Spot #" + index +
                        " freed, Duration: " + minutes +
                        " minutes, Fee: $" + String.format("%.2f", fee));
                return;
            }

            index = (index + 1) % capacity;
            probes++;
        }

        System.out.println("Vehicle not found");
    }

    public void getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        double avgProbes = totalOperations == 0 ? 0 :
                (double) totalProbes / totalOperations;

        int peakHour = -1;
        int maxEntries = 0;

        for (Map.Entry<Integer, Integer> entry : hourlyEntries.entrySet()) {
            if (entry.getValue() > maxEntries) {
                maxEntries = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Avg Probes: " + String.format("%.2f", avgProbes));
        if (peakHour != -1) {
            System.out.println("Peak Hour: " + peakHour + ":00 - " + (peakHour + 1) + ":00");
        }
    }

    public static void main(String[] args) {

        ParkingLotOpenAddressing parking = new ParkingLotOpenAddressing(500);

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}