public abstract class Manager {
    protected String name;
    protected double efficiency;

    public Manager(String name, double efficiency) {
        this.name = name;
        this.efficiency = efficiency;
    }

    public abstract void relayTask(String task);
}

