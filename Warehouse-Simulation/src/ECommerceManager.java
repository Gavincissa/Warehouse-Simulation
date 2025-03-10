public class ECommerceManager extends Manager {
    public ECommerceManager(String name, double efficiency) {
        super(name, efficiency);
    }


    public void pickOrder() {
        System.out.println(name + " picked an online order.");
    }


    public void packOrder() {
        System.out.println(name + " packed an online order.");
    }

    @Override
    public void relayTask(String task) {
        System.out.println(name + " assigned task: " + task);
    }
}
