public class Warehouse {
    private String location;
    private int size;
    private boolean isPowerOn;

    public Warehouse(String location, int size) {
        this.location = location;
        this.size = size;
        this.isPowerOn = true;
    }

    public void powerOutage() {
        System.out.println("Power outage in the warehouse!");
        isPowerOn = false;
    }

    public void restorePower() {
        System.out.println("Power restored in the warehouse!");
        isPowerOn = true;
    }

    public String getLocation() {
        return location;
    }

    public int getSize() {
        return size;
    }

    public boolean isPowerOn() {
        return isPowerOn;
    }
}
