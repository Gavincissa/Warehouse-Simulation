public class PalletSpecialist extends Employee {
    public PalletSpecialist(String name, double efficiency) {
        super(name, efficiency);
    }

    public void palletize() {
        System.out.println(name + " is palletizing an item");
    }
}

