public class ForkliftOperator extends Employee {
    public ForkliftOperator(String name, double efficiency) {
        super(name, efficiency);
    }

    public void grabItem() {
        System.out.println(name + " is grabbing an item using the forklift.");
    }

    public void putAwayItem() {
        System.out.println(name + " is putting away an item.");
    }
}