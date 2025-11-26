public class MyTest {
    public static void main(String[] args) {
        System.out.println("=== MY CUSTOM KNAPSACK TEST ===\n");

        // Define your own items here
        KnapsackSolver.Item[] myItems = {
                new KnapsackSolver.Item("iPhone", 1, 1000),
                new KnapsackSolver.Item("MacBook", 2, 2000),
                new KnapsackSolver.Item("iPad", 1, 800),
                new KnapsackSolver.Item("AirPods", 1, 250),
                new KnapsackSolver.Item("Charger", 1, 50),
                new KnapsackSolver.Item("Jacket", 2, 150)
        };

        // Set your capacity (in kg)
        int myCapacity = 5;

        System.out.println("My Items:");
        for (int i = 0; i < myItems.length; i++) {
            System.out.println((i+1) + ". " + myItems[i]);
        }

        // Solve the problem
        System.out.println("\nSolving...\n");
        KnapsackSolver.KnapsackResult result =
                KnapsackSolver.solve(myItems, myCapacity);

        // Print solution
        KnapsackSolver.printSolution(result, myCapacity);

        // Print individual values
        System.out.println("\n=== DETAILED RESULTS ===");
        System.out.println("Maximum Value: $" + result.getMaxValue());
        System.out.println("Weight Used: " + result.getTotalWeight() + " kg");
        System.out.println("Space Left: " + (myCapacity - result.getTotalWeight()) + " kg");

        System.out.println("\nItems to pack:");
        for (KnapsackSolver.Item item : result.getSelectedItems()) {
            System.out.println("  ✓ " + item.getName());
        }
    }
}