public class Main {
    public static void main(String[] args) {
        // Creating warehouse
        Warehouse warehouse = new Warehouse("Atlanta", 5000, "Grid Layout");

        // Creating managers
        LogisticsManager logManager = new LogisticsManager("Alice", 0.9);
        InventoryManager invManager = new InventoryManager("Bob", 0.8);
        ECommerceManager eCommManager = new ECommerceManager("Charlie", 0.85);

        // Creating employees
        ForkliftOperator forkliftOperator = new ForkliftOperator("Dave", 0.75);
        PalletSpecialist palletSpecialist = new PalletSpecialist("Eve", 0.7);

        // Creating equipment
        Forklift forklift = new Forklift("High Quality", 10);

        // Simulating actions
        warehouse.powerOutage();
        logManager.placeOrder();
        forkliftOperator.grabItem();
        forklift.breakDown();
    }
}

