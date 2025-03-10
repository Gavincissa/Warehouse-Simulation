public class OrderPacker extends Employee {
    public OrderPacker(String name, double efficiency) {
        super(name, efficiency);
    }

    public void pack() {
        System.out.println(name + " is packing an online order.");
    }
}
