/**
 * MyTest.java - Custom Test File for KnapsackSolver
 *
 * Purpose: Demonstrates how to use the KnapsackSolver with custom input
 *
 * Usage:
 * 1. Compile: javac MyTest.java KnapsackSolver.java
 * 2. Run: java MyTest
 *
 * @author Group 5 - COSC251
 * @version 1.0
 */

public class MyTest {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   MY CUSTOM KNAPSACK TEST                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // ==============================================
        // SECTION 1: DEFINE YOUR ITEMS
        // ==============================================

        // Define your own items here
        // Format: new KnapsackSolver.Item("Name", weight, value)
        KnapsackSolver.Item[] myItems = {
                new KnapsackSolver.Item("iPhone", 1, 1000),
                new KnapsackSolver.Item("MacBook", 2, 2000),
                new KnapsackSolver.Item("iPad", 1, 800),
                new KnapsackSolver.Item("AirPods", 1, 250),
                new KnapsackSolver.Item("Charger", 1, 50),
                new KnapsackSolver.Item("Jacket", 2, 150),
                new KnapsackSolver.Item("Passport", 1, 5000),  // High value, low weight
                new KnapsackSolver.Item("Water Bottle", 2, 100),
                new KnapsackSolver.Item("Snacks", 1, 80),
                new KnapsackSolver.Item("Medications", 1, 300)
        };

        // ==============================================
        // SECTION 2: SET YOUR CAPACITY
        // ==============================================

        // Set your luggage capacity (in kg)
        int myCapacity = 7;  // You can change this value

        // ==============================================
        // SECTION 3: DISPLAY INPUT ITEMS
        // ==============================================

        System.out.println("📦 MY ITEMS (Total: " + myItems.length + " items)");
        System.out.println("─".repeat(70));

        int totalAvailableWeight = 0;
        int totalAvailableValue = 0;

        for (int i = 0; i < myItems.length; i++) {
            System.out.printf("%2d. %s%n", (i+1), myItems[i]);
            totalAvailableWeight += myItems[i].getWeight();
            totalAvailableValue += myItems[i].getValue();
        }

        System.out.println("\n📊 INPUT SUMMARY:");
        System.out.println("  • Available Items: " + myItems.length);
        System.out.println("  • Total Available Weight: " + totalAvailableWeight + " kg");
        System.out.println("  • Total Available Value: $" + totalAvailableValue);
        System.out.println("  • Luggage Capacity: " + myCapacity + " kg");
        System.out.println("  • Weight Constraint: " + (totalAvailableWeight > myCapacity ?
                "Exceeds capacity!" : "Within capacity"));

        // ==============================================
        // SECTION 4: SOLVE USING STANDARD DP
        // ==============================================

        System.out.println("\n" + "🔄".repeat(35));
        System.out.println("  SOLVING WITH DYNAMIC PROGRAMMING...");
        System.out.println("🔁".repeat(35) + "\n");

        long startTime = System.nanoTime();
        KnapsackSolver.KnapsackResult result =
                KnapsackSolver.solve(myItems, myCapacity);
        long endTime = System.nanoTime();

        // ==============================================
        // SECTION 5: PRINT COMPLETE SOLUTION
        // ==============================================

        KnapsackSolver.printSolution(result, myCapacity);

        // ==============================================
        // SECTION 6: DETAILED ANALYSIS
        // ==============================================

        System.out.println("\n" + "📈".repeat(35));
        System.out.println("  DETAILED ANALYSIS");
        System.out.println("📊".repeat(35));

        // Performance metrics
        System.out.println("\n⏱️  PERFORMANCE METRICS:");
        System.out.printf("  • Computation Time: %.4f ms%n",
                (endTime - startTime) / 1_000_000.0);
        System.out.println("  • DP Table Size: " + (result.getDpTable().length) +
                " rows × " + (result.getDpTable()[0].length) + " columns");
        System.out.println("  • Items Processed: " + myItems.length);

        // Value analysis
        System.out.println("\n💰 VALUE ANALYSIS:");
        System.out.printf("  • Maximum Value Achieved: $%,d%n", result.getMaxValue());
        System.out.printf("  • Value Utilization: %.1f%%%n",
                ((double)result.getMaxValue() / totalAvailableValue) * 100);

        // Weight analysis
        System.out.println("\n⚖️  WEIGHT ANALYSIS:");
        System.out.println("  • Weight Used: " + result.getTotalWeight() + " kg");
        System.out.println("  • Space Left: " + (myCapacity - result.getTotalWeight()) + " kg");
        System.out.printf("  • Weight Utilization: %.1f%%%n",
                ((double)result.getTotalWeight() / myCapacity) * 100);

        // Item breakdown
        System.out.println("\n📦 SELECTED ITEMS BREAKDOWN:");
        if (result.getSelectedItems().length == 0) {
            System.out.println("  ✗ No items selected (all items too heavy)");
        } else {
            for (int i = 0; i < result.getSelectedItems().length; i++) {
                KnapsackSolver.Item item = result.getSelectedItems()[i];
                System.out.printf("  %2d. %-15s %3d kg  $%5d  (Value/Weight: $%d per kg)%n",
                        (i+1),
                        item.getName(),
                        item.getWeight(),
                        item.getValue(),
                        item.getValue() / item.getWeight());
            }
        }

        // Display excluded items
        System.out.println("\n🚫 EXCLUDED ITEMS:");
        boolean foundExcluded = false;
        for (KnapsackSolver.Item item : myItems) {
            boolean included = false;
            for (KnapsackSolver.Item selected : result.getSelectedItems()) {
                if (item.getName().equals(selected.getName())) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                System.out.printf("  ✗ %-15s %3d kg  $%5d%n",
                        item.getName(), item.getWeight(), item.getValue());
                foundExcluded = true;
            }
        }
        if (!foundExcluded) {
            System.out.println("  ✓ All items included!");
        }

        // ==============================================
        // SECTION 7: SPACE-OPTIMIZED COMPARISON
        // ==============================================

        System.out.println("\n" + "⚡".repeat(35));
        System.out.println("  SPACE OPTIMIZATION COMPARISON");
        System.out.println("💾".repeat(35));

        long startOptTime = System.nanoTime();
        int optimizedValue = KnapsackSolver.solveOptimized(myItems, myCapacity);
        long endOptTime = System.nanoTime();

        System.out.println("\n📊 STANDARD DP vs OPTIMIZED DP:");
        System.out.println("  ┌─────────────────┬──────────────┬──────────────┬──────────────┐");
        System.out.println("  │     Method      │   Max Value  │   Time (ms)  │    Space     │");
        System.out.println("  ├─────────────────┼──────────────┼──────────────┼──────────────┤");
        System.out.printf("  │ Standard DP     │ $%,10d  │    %8.4f  │   O(n×W)     │%n",
                result.getMaxValue(), (endTime - startTime) / 1_000_000.0);
        System.out.printf("  │ Optimized DP    │ $%,10d  │    %8.4f  │    O(W)      │%n",
                optimizedValue, (endOptTime - startOptTime) / 1_000_000.0);
        System.out.println("  └─────────────────┴──────────────┴──────────────┴──────────────┘");

        // Verify both methods give same value
        if (result.getMaxValue() == optimizedValue) {
            System.out.println("\n  ✅ VERIFICATION: Both methods return the same maximum value!");
        } else {
            System.out.println("\n  ⚠️  WARNING: Methods return different values!");
        }

        // ==============================================
        // SECTION 8: DP TABLE VISUALIZATION (OPTIONAL)
        // ==============================================

        System.out.println("\n" + "📋".repeat(35));
        System.out.println("  DYNAMIC PROGRAMMING TABLE");
        System.out.println("🗃️".repeat(35));

        // Only show DP table for small instances
        if (myItems.length <= 10 && myCapacity <= 20) {
            KnapsackSolver.printDPTable(result.getDpTable(), myItems, myCapacity);
        } else {
            System.out.println("\n  ℹ️  DP table omitted (too large to display)");
            System.out.println("  • Items: " + myItems.length + " (showing when ≤ 10)");
            System.out.println("  • Capacity: " + myCapacity + " (showing when ≤ 20)");
            System.out.println("\n  Tip: Reduce items/capacity to see the DP table.");
        }

        // ==============================================
        // SECTION 9: WHAT-IF SCENARIOS
        // ==============================================

        System.out.println("\n" + "🔮".repeat(35));
        System.out.println("  WHAT-IF SCENARIOS");
        System.out.println("💡".repeat(35));

        // Test with different capacities
        System.out.println("\n📐 TESTING DIFFERENT CAPACITIES:");
        int[] testCapacities = {myCapacity/2, myCapacity, myCapacity*2};
        for (int capacity : testCapacities) {
            KnapsackSolver.KnapsackResult testResult =
                    KnapsackSolver.solve(myItems, capacity);
            System.out.printf("  • Capacity %2d kg → Value: $%,5d, Items: %d%n",
                    capacity, testResult.getMaxValue(), testResult.getSelectedItems().length);
        }

        // Value density analysis
        System.out.println("\n📊 VALUE DENSITY (Value per kg):");
        System.out.println("  ┌─────────────────┬──────────┬──────────┬─────────────────┐");
        System.out.println("  │     Item        │  Weight  │  Value   │  Value/kg       │");
        System.out.println("  ├─────────────────┼──────────┼──────────┼─────────────────┤");
        for (KnapsackSolver.Item item : myItems) {
            double density = (double) item.getValue() / item.getWeight();
            System.out.printf("  │ %-15s │ %4d kg  │ $%6d │ $%6.0f per kg │%n",
                    item.getName(), item.getWeight(), item.getValue(), density);
        }
        System.out.println("  └─────────────────┴──────────┴──────────┴─────────────────┘");

        // ==============================================
        // SECTION 10: CONCLUSION
        // ==============================================

        System.out.println("\n" + "✅".repeat(35));
        System.out.println("  TEST COMPLETED SUCCESSFULLY!");
        System.out.println("🎯".repeat(35));

        System.out.println("\n💡 INSIGHTS FROM THIS TEST:");
        System.out.println("  1. Items with highest value/weight ratio are prioritized");
        System.out.println("  2. Even small items can be excluded if better combinations exist");
        System.out.println("  3. Space-optimized version is faster but doesn't show items");
        System.out.println("  4. DP table grows with both items and capacity");

        System.out.println("\n🔧 TO MODIFY THIS TEST:");
        System.out.println("  1. Change 'myItems' array with your own items");
        System.out.println("  2. Adjust 'myCapacity' to your luggage limit");
        System.out.println("  3. Add/remove items to see how the solution changes");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  END OF CUSTOM TEST - HAPPY PACKING! ✈️");
        System.out.println("=".repeat(70));
    }

    /**
     * Helper method to create random test items
     * Useful for stress testing or random scenarios
     */
    private static KnapsackSolver.Item[] createRandomItems(int count, int maxWeight, int maxValue) {
        KnapsackSolver.Item[] items = new KnapsackSolver.Item[count];
        String[] itemNames = {
                "Laptop", "Camera", "Phone", "Tablet", "Headphones",
                "Books", "Clothes", "Shoes", "Toiletries", "Snacks",
                "Gifts", "Documents", "Medicines", "Charger", "Cables"
        };

        for (int i = 0; i < count; i++) {
            String name = itemNames[i % itemNames.length] + " " + (i/itemNames.length + 1);
            int weight = (int)(Math.random() * maxWeight) + 1;
            int value = (int)(Math.random() * maxValue) + 1;
            items[i] = new KnapsackSolver.Item(name, weight, value);
        }
        return items;
    }
}