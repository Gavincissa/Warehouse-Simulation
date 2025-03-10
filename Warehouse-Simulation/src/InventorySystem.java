public class InventorySystem {
    private double efficiency;

    public InventorySystem(double efficiency) {
        this.efficiency = efficiency;
    }

    public void changeStock() {
        System.out.println("Stock levels updated.");
    }

    public void transferStock() {
        System.out.println("Stock transferred between locations.");
    }
}
