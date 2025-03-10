
public class Warehouse {
    private String location;
    private int size;
    private String layout;
    private Boolean isPowerOn;

    public Warehouse(String location, int size, String layout) {
        this.location = location;
        this.size = size;
        this.layout = layout;
        this.isPowerOn = true;
    }

    public void powerOutage() {
        System.out.println("Power outage in the warehouse!");
        isPowerOn = false;
    }
}
