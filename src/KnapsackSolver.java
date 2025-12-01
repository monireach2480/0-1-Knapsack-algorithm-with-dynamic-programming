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
 * NOTE ON SPACE OPTIMIZATION:
 * This implementation uses O(n × W) space to store the full DP table.
 * This allows us to backtrack and find which items were selected.
 *
 * ALTERNATIVE: We could reduce space to O(W) by using only one row,
 * updating it in place. However, this prevents backtracking.
 *
 * Trade-off decision: We prioritize showing selected items over
 * memory savings, since typical luggage problems don't have
 * memory constraints.
 *
 * See solveOptimized() method for the O(W) space version.
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

        // EDGE CASE 1: Handle empty items or zero capacity
        if (n == 0 || capacity == 0) {
            System.out.println("⚠️  Edge case detected: No items or zero capacity");
            return new KnapsackResult(0, 0, new Item[]{}, new int[1][1]);
        }

        // EDGE CASE 2: Handle single item (optimization - no need for full DP table)
        if (n == 1) {
            System.out.println("⚡ Optimization: Single item detected - using O(1) solution");
            Item item = items[0];

            if (item.getWeight() <= capacity) {
                // Item fits, take it!
                int[][] simpleTable = new int[2][capacity + 1];
                for (int w = item.getWeight(); w <= capacity; w++) {
                    simpleTable[1][w] = item.getValue();
                }

                return new KnapsackResult(
                        item.getValue(),
                        item.getWeight(),
                        new Item[]{item},
                        simpleTable
                );
            } else {
                // Item doesn't fit
                System.out.println("   → Item too heavy, cannot pack");
                return new KnapsackResult(
                        0,
                        0,
                        new Item[]{},
                        new int[2][capacity + 1]
                );
            }
        }

        // NORMAL CASE: Multiple items - build full DP table
        System.out.println("📊 Building DP table for " + n + " items with " + capacity + "kg capacity");

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
     * SPACE-OPTIMIZED version using only ONE ROW (O(W) space)
     *
     * Trade-off: Uses less memory but cannot backtrack to find items.
     * Only returns the maximum value, not which items to select.
     *
     * @param items Array of items to choose from
     * @param capacity Maximum weight capacity
     * @return Maximum value achievable (items not included)
     */
    public static int solveOptimized(Item[] items, int capacity) {
        int n = items.length;

        // Edge cases
        if (n == 0 || capacity == 0) {
            return 0;
        }

        // Single item case
        if (n == 1) {
            return (items[0].getWeight() <= capacity) ? items[0].getValue() : 0;
        }

        // Only ONE row needed! (Space: O(W) instead of O(n × W))
        int[] dp = new int[capacity + 1];

        System.out.println("⚡ Using space-optimized version: O(W) space instead of O(n × W)");

        // Process each item
        for (int i = 0; i < n; i++) {
            Item currentItem = items[i];

            // IMPORTANT: Go backwards to avoid using updated values
            for (int w = capacity; w >= currentItem.getWeight(); w--) {
                dp[w] = Math.max(
                        dp[w],  // Don't take item
                        dp[w - currentItem.getWeight()] + currentItem.getValue()  // Take item
                );
            }
        }

        return dp[capacity];  // Maximum value
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

        // ========== EDGE CASE EXAMPLES ==========
        runEdgeCaseExamples();

        // ========== SMALL EXAMPLE ==========
        runSmallExample();

        // ========== LARGE EXAMPLE ==========
        runLargeExample();

        // ========== OPTIMIZED VERSION DEMO ==========
        runOptimizedDemo();

        // ========== CUSTOM INPUT GUIDE ==========
        printUsageGuide();
    }

    /**
     * Demonstrates edge case handling
     */
    private static void runEdgeCaseExamples() {
        System.out.println("\n\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  EDGE CASES DEMONSTRATION                                   │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");

        // Edge Case 1: Empty items
        System.out.println("\n1️⃣  Test: Empty items array");
        Item[] emptyItems = {};
        KnapsackResult result1 = solve(emptyItems, 10);
        System.out.println("   Result: Max Value = $" + result1.getMaxValue());

        // Edge Case 2: Zero capacity
        System.out.println("\n2️⃣  Test: Zero capacity");
        Item[] someItems = {new Item("Laptop", 3, 1000)};
        KnapsackResult result2 = solve(someItems, 0);
        System.out.println("   Result: Max Value = $" + result2.getMaxValue());

        // Edge Case 3: Single item that fits
        System.out.println("\n3️⃣  Test: Single item that FITS");
        Item[] oneItem = {new Item("Camera", 2, 800)};
        KnapsackResult result3 = solve(oneItem, 5);
        printSolution(result3, 5);

        // Edge Case 4: Single item that doesn't fit
        System.out.println("\n4️⃣  Test: Single item that DOESN'T FIT");
        Item[] heavyItem = {new Item("Piano", 100, 5000)};
        KnapsackResult result4 = solve(heavyItem, 10);
        printSolution(result4, 10);

        // Edge Case 5: All items too heavy
        System.out.println("\n5️⃣  Test: All items too heavy");
        Item[] heavyItems = {
                new Item("Elephant", 1000, 10000),
                new Item("Car", 1500, 20000)
        };
        KnapsackResult result5 = solve(heavyItems, 5);
        System.out.println("   Result: Max Value = $" + result5.getMaxValue());
        System.out.println("   (No items selected - all too heavy)");
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
     * Demonstrates the space-optimized O(W) version
     */
    private static void runOptimizedDemo() {
        System.out.println("\n\n┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  SPACE OPTIMIZATION DEMO - O(W) vs O(n×W)                   │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");

        Item[] items = {
                new Item("Laptop", 3, 1000),
                new Item("Camera", 2, 800),
                new Item("Gifts", 4, 600)
        };
        int capacity = 8;

        System.out.println("\nComparing both approaches with 3 items, 8kg capacity:");

        // Standard approach
        long start1 = System.nanoTime();
        KnapsackResult fullResult = solve(items, capacity);
        long end1 = System.nanoTime();

        System.out.println("\n📊 Standard DP (with backtracking):");
        System.out.println("   Space: O(n × W) = O(3 × 8) = 24 cells");
        System.out.println("   Max Value: $" + fullResult.getMaxValue());
        System.out.println("   Selected Items: " + fullResult.getSelectedItems().length + " items");
        System.out.printf("   Time: %.4f ms%n", (end1 - start1) / 1_000_000.0);

        // Optimized approach
        long start2 = System.nanoTime();
        int optimizedValue = solveOptimized(items, capacity);
        long end2 = System.nanoTime();

        System.out.println("\n⚡ Optimized DP (one row only):");
        System.out.println("   Space: O(W) = O(8) = 8 cells only!");
        System.out.println("   Max Value: $" + optimizedValue);
        System.out.println("   Selected Items: Cannot determine (trade-off)");
        System.out.printf("   Time: %.4f ms%n", (end2 - start2) / 1_000_000.0);

        System.out.println("\n💡 Trade-off Summary:");
        System.out.println("   ✅ Optimized version uses 67% less memory (8 vs 24 cells)");
        System.out.println("   ❌ But cannot show which items to pack");
        System.out.println("   → We use standard version for better user experience");
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

        System.out.println("\n6. For space-optimized version (value only, no items):");
        System.out.println("   int maxValue = KnapsackSolver.solveOptimized(myItems, capacity);");

        System.out.println("\n" + "=".repeat(70));
    }
}