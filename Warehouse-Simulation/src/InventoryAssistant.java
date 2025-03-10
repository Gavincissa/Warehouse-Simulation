public class InventoryAssistant extends Employee {
    public InventoryAssistant(String name, double efficiency) {
        super(name, efficiency);
    }

    public void putItemAway() {
        System.out.println(name + " is putting an item back in stock");
    }

    public void sendOrder() {
        System.out.println(name + " is sending an inventory order.");
    }
}
