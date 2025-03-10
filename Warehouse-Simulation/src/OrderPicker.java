public class OrderPicker extends Employee {
    public OrderPicker(String name, double efficiency) {
        super(name, efficiency);
    }

    public void pick() {
        System.out.println(name + " is grabbing an item for an online order.");
    }
}
