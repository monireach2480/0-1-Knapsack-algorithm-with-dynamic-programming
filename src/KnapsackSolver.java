/**
 * 0-1 Knapsack Problem Solver using Dynamic Programming
 *
 * Purpose: Solves the luggage packing optimization problem for airline travelers
 * who need to maximize the value of items while staying within weight limits.
 *
 * Algorithm: Dynamic Programming approach with backtracking to find optimal items
 * Time Complexity: O(n * W) where n is number of items and W is capacity
 * Space Complexity: O(n * W) for the DP table
 *
 * @author Group 5 - COSC251
 */

public class KnapsackSolver {

    /**
     * Inner class to represent an item that can be packed
     */
    public static class Item {
        private String name;
        private int weight;
        private int value;

        public Item(String name, int weight, int value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }

        public String getName() { return name; }
        public int getWeight() { return weight; }
        public int getValue() { return value; }

        @Override
        public String toString() {
            return String.format("%-20s | Weight: %3dkg | Value: $%4d",
                    name, weight, value);
        }
    }

    /**
     * Result class to store the solution
     */
    public static class KnapsackResult {
        private int maxValue;
        private int totalWeight;
        private Item[] selectedItems;
        private int[][] dpTable;

        public KnapsackResult(int maxValue, int totalWeight,
                              Item[] selectedItems, int[][] dpTable) {
            this.maxValue = maxValue;
            this.totalWeight = totalWeight;
            this.selectedItems = selectedItems;
            this.dpTable = dpTable;
        }

        public int getMaxValue() { return maxValue; }
        public int getTotalWeight() { return totalWeight; }
        public Item[] getSelectedItems() { return selectedItems; }
        public int[][] getDpTable() { return dpTable; }
    }

    /**
     * Solves the 0-1 Knapsack problem using Dynamic Programming
     *
     * @param items Array of items to choose from
     * @param capacity Maximum weight capacity (luggage limit)
     * @return KnapsackResult containing optimal solution
     */
    public static KnapsackResult solve(Item[] items, int capacity) {
        int n = items.length;

        // Create DP table: dp[i][w] = max value using first i items with weight limit w
        int[][] dp = new int[n + 1][capacity + 1];

        // Fill the DP table
        // Bottom-up approach: build solution from smaller subproblems
        for (int i = 1; i <= n; i++) {
            Item currentItem = items[i - 1];

            for (int w = 0; w <= capacity; w++) {
                // Option 1: Don't include current item
                dp[i][w] = dp[i - 1][w];

                // Option 2: Include current item (if it fits)
                if (currentItem.getWeight() <= w) {
                    int valueWithItem = currentItem.getValue() +
                            dp[i - 1][w - currentItem.getWeight()];
                    dp[i][w] = Math.max(dp[i][w], valueWithItem);
                }
            }
        }

        // Backtrack to find which items were selected
        Item[] selectedItems = backtrack(items, dp, capacity);

        // Calculate total weight of selected items
        int totalWeight = 0;
        for (Item item : selectedItems) {
            totalWeight += item.getWeight();
        }

        return new KnapsackResult(dp[n][capacity], totalWeight, selectedItems, dp);
    }

    /**
     * Backtrack through DP table to find which items were selected
     *
     * @param items Original array of items
     * @param dp Completed DP table
     * @param capacity Maximum capacity
     * @return Array of selected items
     */
    private static Item[] backtrack(Item[] items, int[][] dp, int capacity) {
        int n = items.length;
        int w = capacity;

        // Use a temporary array to collect selected items
        Item[] temp = new Item[n];
        int count = 0;

        // Trace back from dp[n][capacity] to dp[0][0]
        for (int i = n; i > 0 && w > 0; i--) {
            // If value changed from previous row, item i-1 was included
            if (dp[i][w] != dp[i - 1][w]) {
                temp[count++] = items[i - 1];
                w -= items[i - 1].getWeight();
            }
        }

        // Copy to exact-sized array and reverse order
        Item[] selected = new Item[count];
        for (int i = 0; i < count; i++) {
            selected[i] = temp[count - 1 - i];
        }

        return selected;
    }

    /**
     * Prints a formatted solution report
     */
    public static void printSolution(KnapsackResult result, int capacity) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("          0-1 KNAPSACK OPTIMAL PACKING SOLUTION");
        System.out.println("=".repeat(70));

        System.out.println("\nLuggage Capacity: " + capacity + " kg");
        System.out.println("Maximum Value Achieved: $" + result.getMaxValue());
        System.out.println("Total Weight Used: " + result.getTotalWeight() + " kg");
        System.out.println("Remaining Capacity: " + (capacity - result.getTotalWeight()) + " kg");

        System.out.println("\n" + "-".repeat(70));
        System.out.println("SELECTED ITEMS TO PACK:");
        System.out.println("-".repeat(70));

        if (result.getSelectedItems().length == 0) {
            System.out.println("No items can be packed within the weight limit.");
        } else {
            for (int i = 0; i < result.getSelectedItems().length; i++) {
                System.out.printf("%2d. %s%n", (i + 1), result.getSelectedItems()[i]);
            }
        }

        System.out.println("=".repeat(70) + "\n");
    }

    /**
     * Prints the DP table for educational purposes
     */
    public static void printDPTable(int[][] dp, Item[] items, int capacity) {
        System.out.println("\nDYNAMIC PROGRAMMING TABLE:");
        System.out.println("(Rows = Items, Columns = Weight Capacity)");
        System.out.println("-".repeat(70));

        // Print header
        System.out.print("Item\\Weight |");
        for (int w = 0; w <= Math.min(capacity, 20); w++) {
            System.out.printf("%4d", w);
        }
        if (capacity > 20) System.out.print(" ...");
        System.out.println();
        System.out.println("-".repeat(70));

        // Print rows
        for (int i = 0; i <= Math.min(items.length, 10); i++) {
            if (i == 0) {
                System.out.printf("%-12s |", "None");
            } else {
                System.out.printf("%-12s |", items[i-1].getName().substring(0,
                        Math.min(12, items[i-1].getName().length())));
            }

            for (int w = 0; w <= Math.min(capacity, 20); w++) {
                System.out.printf("%4d", dp[i][w]);
            }
            if (capacity > 20) System.out.print(" ...");
            System.out.println();
        }

        if (items.length > 10) {
            System.out.println("... (showing first 10 items only)");
        }
        System.out.println();
    }

    /**
     * Main method with demonstration examples
     */
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  0-1 KNAPSACK ALGORITHM - AIRLINE LUGGAGE OPTIMIZATION       ║");
        System.out.println("║  Using Dynamic Programming                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        // ========== SMALL EXAMPLE ==========
        runSmallExample();

        // ========== LARGE EXAMPLE ==========
        runLargeExample();

        // ========== CUSTOM INPUT GUIDE ==========
        printUsageGuide();
    }

    /**
     * Small example demonstrating the algorithm
     */
    private static void runSmallExample() {
        System.out.println("\n\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  EXAMPLE 1: SMALL INSTANCE (5 items, 15kg limit)           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");

        // Create items for a traveler
        Item[] smallItems = {
                new Item("Laptop", 3, 1000),
                new Item("Camera", 2, 800),
                new Item("Clothes", 5, 300),
                new Item("Gifts", 4, 600),
                new Item("Books", 6, 400)
        };

        int smallCapacity = 15;

        System.out.println("\nAvailable Items:");
        System.out.println("-".repeat(70));
        for (int i = 0; i < smallItems.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), smallItems[i]);
        }

        // Solve the problem
        long startTime = System.nanoTime();
        KnapsackResult result = solve(smallItems, smallCapacity);
        long endTime = System.nanoTime();

        // Print solution
        printSolution(result, smallCapacity);
        printDPTable(result.getDpTable(), smallItems, smallCapacity);

        System.out.printf("Computation Time: %.4f ms%n", (endTime - startTime) / 1_000_000.0);
    }

    /**
     * Large example demonstrating scalability
     */
    private static void runLargeExample() {
        System.out.println("\n\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  EXAMPLE 2: LARGE INSTANCE (20 items, 50kg limit)          │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");

        // Create a larger set of items
        Item[] largeItems = {
                new Item("Designer Handbag", 2, 1500),
                new Item("Laptop Computer", 3, 1200),
                new Item("DSLR Camera", 2, 1000),
                new Item("Portable Monitor", 1, 400),
                new Item("Wireless Headphones", 1, 300),
                new Item("Traditional Clothes", 4, 800),
                new Item("Jewelry Box", 1, 2000),
                new Item("Perfume Set", 2, 500),
                new Item("Chocolate Gift Box", 3, 350),
                new Item("Coffee Beans", 2, 250),
                new Item("Books Collection", 5, 400),
                new Item("Tablet", 1, 600),
                new Item("Smart Watch", 1, 800),
                new Item("Winter Jacket", 3, 450),
                new Item("Shoes", 2, 300),
                new Item("Toiletries Kit", 2, 150),
                new Item("Power Bank", 1, 200),
                new Item("Travel Pillow", 1, 100),
                new Item("Medicines", 1, 500),
                new Item("Documents Folder", 1, 1000)
        };

        int largeCapacity = 50;

        System.out.println("\nAvailable Items (20 items):");
        System.out.println("-".repeat(70));
        for (int i = 0; i < largeItems.length; i++) {
            System.out.printf("%2d. %s%n", (i + 1), largeItems[i]);
        }

        // Solve the problem
        long startTime = System.nanoTime();
        KnapsackResult result = solve(largeItems, largeCapacity);
        long endTime = System.nanoTime();

        // Print solution
        printSolution(result, largeCapacity);

        System.out.printf("Computation Time: %.4f ms%n", (endTime - startTime) / 1_000_000.0);
        System.out.println("\n(DP Table omitted for large example - too large to display)");
    }

    /**
     * Prints usage guide for custom input
     */
    private static void printUsageGuide() {
        System.out.println("\n\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  HOW TO USE THIS CLASS WITH YOUR OWN INPUT                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        System.out.println("\nTo use this solver with your own data:");
        System.out.println("\n1. Create an array of Item objects:");
        System.out.println("   Item[] myItems = {");
        System.out.println("       new Item(\"Item Name\", weight, value),");
        System.out.println("       new Item(\"Another Item\", weight, value),");
        System.out.println("       ...");
        System.out.println("   };");

        System.out.println("\n2. Set your capacity:");
        System.out.println("   int capacity = 30; // Your luggage limit in kg");

        System.out.println("\n3. Solve the problem:");
        System.out.println("   KnapsackResult result = KnapsackSolver.solve(myItems, capacity);");

        System.out.println("\n4. Display the solution:");
        System.out.println("   KnapsackSolver.printSolution(result, capacity);");

        System.out.println("\n5. Access individual result components:");
        System.out.println("   int maxValue = result.getMaxValue();");
        System.out.println("   int totalWeight = result.getTotalWeight();");
        System.out.println("   Item[] selected = result.getSelectedItems();");

        System.out.println("\n" + "=".repeat(70));
    }
}