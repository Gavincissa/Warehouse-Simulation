public class Forklift extends Equipment {
    private int speed;

    public Forklift(String quality, int speed) {
        super(quality);
        this.speed = speed;
    }

    public void breakDown() {
        System.out.println("The forklift has broken down!");
    }
}

