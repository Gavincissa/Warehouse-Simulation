public class Forklift extends Equipment {
    private int speed;
    private boolean isFunctional = true;

    public Forklift(String quality, int speed) {
        super(quality);
        this.speed = speed;
    }

    public void breakDown() {
        isFunctional = false;
        System.out.println("The forklift has broken down!");
    }

    public boolean isFunctional() {
        return isFunctional;
    }
}

