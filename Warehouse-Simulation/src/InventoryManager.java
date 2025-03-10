public class InventoryManager extends Manager {
    public InventoryManager(String name, double efficiency) {
        super(name, efficiency);
    }


    public void placeOrder() {
        System.out.println(name + " placed an inventory order.");
    }


    public void sendOrder() {
        System.out.println(name + " sent an inventory order.");
    }

    @Override
    public void relayTask(String task) {
        System.out.println(name + " assigned task: " + task);
    }
}

