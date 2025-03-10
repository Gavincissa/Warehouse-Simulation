public class LogisticsManager extends Manager {
    public LogisticsManager(String name, double efficiency) {
        super(name, efficiency);
    }


    public void placeOrder() {
        System.out.println(name + " placed a logistics order.");
    }


    public void sendOrder() {
        System.out.println(name + " sent a logistics order.");
    }

    @Override
    public void relayTask(String task) {
        System.out.println(name + " assigned task: " + task);
    }
}

