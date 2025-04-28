import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.Random;
import java.util.Scanner;

class Simulation {
    private final ExecutorService executor = Executors.newFixedThreadPool(7);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private long startTime;

    private Warehouse warehouse;
    private ECommerceManager ecommerceManager;
    private InventoryManager inventoryManager;
    private LogisticsManager logisticsManager;

    private int ORDER_PICK_QUOTA;
    private int ORDER_PACK_QUOTA;
    private int STOCK_UPDATE_QUOTA;
    private int ITEM_RETURN_QUOTA;
    private int PALLET_MOVE_QUOTA;
    private int RECEIVE_SHIPMENT_QUOTA;
    private int SEND_SHIPMENT_QUOTA;

    private int pickOrdersCompleted = 0;
    private int packOrdersCompleted = 0;
    private int stockUpdatesCompleted = 0;
    private int itemReturnsCompleted = 0;
    private int palletMovesCompleted = 0;
    private int receiveShipmentsCompleted = 0;
    private int sendShipmentsCompleted = 0;

    private int powerOutageCount = 0;


    private final Map<String, Double> taskCompletionTimes = new HashMap<>();

    // Add a Map to track progress of each task type
    private Map<String, Integer> taskProgressMap = new HashMap<>();

    // Initialize the task progress map with all tasks and initial counts (defaults to 0)
    private void initializeTaskProgress() {
        taskProgressMap.put("Pick Order", 0);
        taskProgressMap.put("Pack Order", 0);
        taskProgressMap.put("Update Stock", 0);
        taskProgressMap.put("Put Items Back", 0);
        taskProgressMap.put("Move Pallet", 0);
        taskProgressMap.put("Receive Shipment", 0);
        taskProgressMap.put("Send Shipment", 0);
    }

    private double getSizeMultiplier() {
        // Let's assume a "normal" size is 50000 sq ft.
        double baseSize = 50000.0;
        return warehouse.getSize() / baseSize;
    }

    // Method to get the progress of a specific task
    private int getTaskProgress(String task) {
        return taskProgressMap.getOrDefault(task, 0); // Default to 0 if the task is not found
    }

    // Method to update the completion progress of a specific task
    private void updateTaskCompletion(String task) {
        int currentProgress = taskProgressMap.getOrDefault(task, 0);
        taskProgressMap.put(task, currentProgress + 1);

        // Increment the correct counter
        switch (task) {
            case "Pick Order" -> pickOrdersCompleted++;
            case "Pack Order" -> packOrdersCompleted++;
            case "Update Stock" -> stockUpdatesCompleted++;
            case "Put Item Back" -> itemReturnsCompleted++;
            case "Move Pallets" -> palletMovesCompleted++;
            case "Receive Shipment" -> receiveShipmentsCompleted++;
            case "Send Shipment" -> sendShipmentsCompleted++;
        }

        boolean quotaMet = switch (task) {
            case "Pick Order" -> currentProgress + 1 == ORDER_PICK_QUOTA;
            case "Pack Order" -> currentProgress + 1 == ORDER_PACK_QUOTA;
            case "Update Stock" -> currentProgress + 1 == STOCK_UPDATE_QUOTA;
            case "Put Item Back" -> currentProgress + 1 == ITEM_RETURN_QUOTA;
            case "Move Pallets" -> currentProgress + 1 == PALLET_MOVE_QUOTA;
            case "Receive Shipment" -> currentProgress + 1 == RECEIVE_SHIPMENT_QUOTA;
            case "Send Shipment" -> currentProgress + 1 == SEND_SHIPMENT_QUOTA;
            default -> false;
        };

        if (quotaMet && !taskCompletionTimes.containsKey(task)) {
            double seconds = (System.currentTimeMillis() - startTime) / 1000.0;
            taskCompletionTimes.put(task, seconds);
        }
    }




    private final BlockingQueue<Integer> pickedOrdersQueue = new LinkedBlockingQueue<>();


    // Interactive User Setup Method
    private void getUserInput() {
        Scanner scanner = new Scanner(System.in);

        // Get warehouse details
        System.out.println("Enter warehouse location:");
        String location = scanner.nextLine();

        System.out.println("Enter warehouse size (in square feet):");
        int size = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        warehouse = new Warehouse(location, size);

        // Get task quotas from user
        System.out.println("Enter quota for Orders:");
        ORDER_PICK_QUOTA = scanner.nextInt();
        ORDER_PACK_QUOTA = ORDER_PICK_QUOTA;

        System.out.println("Enter quota for Item Returns:");
        ITEM_RETURN_QUOTA = scanner.nextInt();
        STOCK_UPDATE_QUOTA = ITEM_RETURN_QUOTA;

        System.out.println("Enter quota for Receiving Shipments:");
        RECEIVE_SHIPMENT_QUOTA = scanner.nextInt();
        PALLET_MOVE_QUOTA = RECEIVE_SHIPMENT_QUOTA;

        System.out.println("Enter quota for Sending Shipments:");
        SEND_SHIPMENT_QUOTA = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        // Get manager details
        System.out.println("\nEnter name for Inventory Manager:");
        String inventoryManagerName = scanner.nextLine();
        System.out.println("Enter efficiency for " + inventoryManagerName + ":");
        double inventoryManagerEfficiency = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        inventoryManager = new InventoryManager(inventoryManagerName, inventoryManagerEfficiency / 100);

        System.out.println("\nEnter name for Logistics Manager:");
        String logisticsManagerName = scanner.nextLine();
        System.out.println("Enter efficiency for " + logisticsManagerName + ":");
        double logisticsManagerEfficiency = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        logisticsManager = new LogisticsManager(logisticsManagerName, logisticsManagerEfficiency / 100);

        System.out.println("\nEnter name for E-Commerce Manager:");
        String ecommerceManagerName = scanner.nextLine();
        System.out.println("Enter efficiency for " + ecommerceManagerName + ":");
        double ecommerceManagerEfficiency = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        ecommerceManager = new ECommerceManager(ecommerceManagerName, ecommerceManagerEfficiency / 100);

        // Get worker details
        System.out.println("\nEnter worker name for Order Picker:");
        String pickerName = scanner.nextLine();
        System.out.println("Enter efficiency for " + pickerName + ":");
        double pickerEfficiency = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("\nEnter worker name for Order Packer:");
        String packerName = scanner.nextLine();
        System.out.println("Enter efficiency for " + packerName + ":");
        double packerEfficiency = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("\nEnter worker name for Forklift Operator:");
        String foName = scanner.nextLine();
        System.out.println("Enter efficiency for " + foName + ":");
        double foEfficiency = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("\nEnter worker name for Pallet Specialist:");
        String psName = scanner.nextLine();
        System.out.println("Enter efficiency for " + psName + ":");
        double psEfficiency = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("\nEnter worker name for Inventory Assistant:");
        String IAName = scanner.nextLine();
        System.out.println("Enter efficiency for " + IAName + ":");
        double IAEfficiency = scanner.nextDouble();
        scanner.nextLine();

        // Start tasks with user-defined workers and managers
        scheduleTask(new ForkliftOperator(foName, foEfficiency), "Move Pallets", 6, PALLET_MOVE_QUOTA, logisticsManager);
        scheduleOrderPicking(new OrderPicker(pickerName, pickerEfficiency), ORDER_PICK_QUOTA, ecommerceManager);
        scheduleOrderPacking(new OrderPacker(packerName, packerEfficiency), ORDER_PACK_QUOTA, ecommerceManager);
        scheduleTask(new InventoryAssistant(IAName, IAEfficiency), "Update Stock", 3, STOCK_UPDATE_QUOTA, inventoryManager);
        scheduleTask(new InventoryAssistant(IAName, IAEfficiency), "Put Item Back", 3, ITEM_RETURN_QUOTA, inventoryManager);
        scheduleTask(new ForkliftOperator(foName, foEfficiency), "Receive Shipment", 12, RECEIVE_SHIPMENT_QUOTA, logisticsManager);
        scheduleTask(new PalletSpecialist(psName, psEfficiency), "Send Shipment", 15, SEND_SHIPMENT_QUOTA, logisticsManager);
    }

    public void getHardCodedData(){
        warehouse = new Warehouse("Georgia", 10000);

        ORDER_PICK_QUOTA = 75;
        ITEM_RETURN_QUOTA = 50;
        RECEIVE_SHIPMENT_QUOTA = 25;
        SEND_SHIPMENT_QUOTA = 25;

        PALLET_MOVE_QUOTA = RECEIVE_SHIPMENT_QUOTA;
        STOCK_UPDATE_QUOTA = ITEM_RETURN_QUOTA;
        ORDER_PACK_QUOTA = ORDER_PICK_QUOTA;

        inventoryManager = new InventoryManager("Xavier", 50.0 / 100);
        logisticsManager = new LogisticsManager("Reece", 50.0 / 100);
        ecommerceManager = new ECommerceManager("Olivia", 50.0 / 100);

        String foName = "Brett";
        double foEfficiency = 50.0;

        String pickerName = "Zach";
        double pickerEfficiency = 50.0;

        String packerName = "Tyler";
        double packerEfficiency = 50.0;

        String IAName = "Ashlyn";
        double IAEfficiency = 50.0;

        String psName = "Alec";
        double psEfficiency = 50.0;

        scheduleTask(new ForkliftOperator(foName, foEfficiency), "Move Pallets", 6, PALLET_MOVE_QUOTA, logisticsManager);
        scheduleOrderPicking(new OrderPicker(pickerName, pickerEfficiency), ORDER_PICK_QUOTA, ecommerceManager);
        scheduleOrderPacking(new OrderPacker(packerName, packerEfficiency), ORDER_PACK_QUOTA, ecommerceManager);
        scheduleTask(new InventoryAssistant(IAName, IAEfficiency), "Update Stock", 3, STOCK_UPDATE_QUOTA, inventoryManager);
        scheduleTask(new InventoryAssistant(IAName, IAEfficiency), "Put Item Back", 3, ITEM_RETURN_QUOTA, inventoryManager);
        scheduleTask(new ForkliftOperator(foName, foEfficiency), "Receive Shipment", 12, RECEIVE_SHIPMENT_QUOTA, logisticsManager);
        scheduleTask(new PalletSpecialist(psName, psEfficiency), "Send Shipment", 15, SEND_SHIPMENT_QUOTA, logisticsManager);

    }

    public void startSimulation() {
        startTime = System.currentTimeMillis();
        System.out.println("\n=========================================");
        System.out.println("🏭  Starting Warehouse Simulation (Task Quota Mode)");
        System.out.println("=========================================\n");

        scheduleRandomPowerOutage();
    }

    private void scheduleRandomPowerOutage() {
        int delay = random.nextInt(100) + 40;

        scheduler.schedule(() -> {
            warehouse.powerOutage();
            powerOutageCount++;
            System.out.println("UH OH! A power outage has occurred. Workers are pausing their tasks...");

            try {
                Thread.sleep(5000); // Simulating downtime
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            warehouse.restorePower();

            // Schedule next outage recursively
            scheduleRandomPowerOutage();
        }, delay, TimeUnit.SECONDS);
    }


    private void scheduleOrderPicking(Employee worker, int taskQuota, ECommerceManager manager) {
        executor.submit(() -> {
            while (pickOrdersCompleted < taskQuota) {
                if (!warehouse.isPowerOn()) {
                    waitForPower(worker);
                    continue;
                }

                manager.relayTask("Pick Order");
                double adjustedEfficiency = (manager.efficiency/10)*(worker.efficiency);
                double taskTime = 6 / adjustedEfficiency;
                double relativeStartTime = (System.currentTimeMillis() - startTime) / 1000.0;

                System.out.printf("[⏳ Time: %.2f min] %s starts task: Pick Order (Eff: %.2f)\n",
                        relativeStartTime, worker.name, adjustedEfficiency);
                try {
                    Thread.sleep((long) (taskTime * 1000));
                } catch (InterruptedException e) {
                    return;
                }

                pickedOrdersQueue.add(++pickOrdersCompleted);
                double relativeCompletionTime = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.printf("[✅ Time: %.2f min] %s completed task: Pick Order | Progress: %d/%d\n\n",
                        relativeCompletionTime, worker.name, pickOrdersCompleted, taskQuota);
            }
        });
    }

    private void waitForPower(Employee worker) {
        // Loop until the power is back on
        while (!warehouse.isPowerOn()) {
            // Print a message that the worker is waiting for the power to return
            System.out.printf("[⏳ Time: %.2f min] %s is waiting for power to be restored...\n",
                    (System.currentTimeMillis() - startTime) / 1000.0, worker.name);
            try {
                // Sleep for 1 second before checking again
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Properly handle the interruption
            }
        }

        // Once power is restored, notify the worker can resume their task
        System.out.printf("[✅ Time: %.2f min] Power restored. %s can resume work.\n",
                (System.currentTimeMillis() - startTime) / 1000.0, worker.name);
    }


    private void scheduleOrderPacking(Employee worker, int taskQuota, ECommerceManager manager) {
        executor.submit(() -> {
            while (packOrdersCompleted < taskQuota) {
                if (!warehouse.isPowerOn()) {
                    waitForPower(worker);
                    continue;
                }

                try {
                    int orderId = pickedOrdersQueue.take();
                    manager.relayTask("Pack Order");
                    double adjustedEfficiency = (manager.efficiency/10)*(worker.efficiency);
                    double taskTime = 4 / adjustedEfficiency;
                    double relativeStartTime = (System.currentTimeMillis() - startTime) / 1000.0;

                    System.out.printf("[⏳ Time: %.2f min] %s starts packing Order #%d (Eff: %.2f)\n",
                            relativeStartTime, worker.name, orderId, adjustedEfficiency);

                    Thread.sleep((long) (taskTime * 1000));

                    updateTaskCompletion("Pack Order");
                    double relativeCompletionTime = (System.currentTimeMillis() - startTime) / 1000.0;
                    System.out.printf("[✅ Time: %.2f min] %s completed packing Order #%d | Progress: %d/%d\n\n",
                            relativeCompletionTime, worker.name, orderId, packOrdersCompleted, taskQuota);

                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }

    private void scheduleTask(Employee worker, String task, double baseTime, int taskQuota, Manager manager) {
        executor.submit(() -> {
            while (getTaskProgress(task) < taskQuota) {
                if (!warehouse.isPowerOn()) {
                    waitForPower(worker);
                    continue;
                }

                manager.relayTask(task);
                double adjustedEfficiency = (manager.efficiency/10) * worker.efficiency;
                double sizeMultiplier = 1.0;

                if (task.equals("Pick Order") ||
                        task.equals("Put Items Back") ||
                        task.equals("Move Pallets") ||
                        task.equals("Receive Shipment") ||
                        task.equals("Send Shipment")) {
                    sizeMultiplier = getSizeMultiplier();
                }


                double taskTime = (baseTime * sizeMultiplier) / adjustedEfficiency;
                double relativeStartTime = (System.currentTimeMillis() - startTime) / 1000.0;

                System.out.printf("[⏳ Time: %.2f min] %s starts task: %s (Eff: %.2f, Size x%.2f)\n",
                        relativeStartTime, worker.name, task, adjustedEfficiency, sizeMultiplier);
                try {
                    Thread.sleep((long) (taskTime * 1000));
                } catch (InterruptedException e) {
                    return;
                }

                updateTaskCompletion(task);
                double relativeCompletionTime = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.printf("[✅ Time: %.2f min] %s completed task: %s | Progress: %d/%d\n\n",
                        relativeCompletionTime, worker.name, task, getTaskProgress(task), taskQuota);


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }


    private boolean isSimulationComplete() {
        return pickOrdersCompleted >= ORDER_PICK_QUOTA &&
                packOrdersCompleted >= ORDER_PACK_QUOTA &&
                stockUpdatesCompleted >= STOCK_UPDATE_QUOTA &&
                itemReturnsCompleted >= ITEM_RETURN_QUOTA &&
                palletMovesCompleted >= PALLET_MOVE_QUOTA &&
                receiveShipmentsCompleted >= RECEIVE_SHIPMENT_QUOTA &&
                sendShipmentsCompleted >= SEND_SHIPMENT_QUOTA;
    }

    private void endSimulation() {
        System.out.println("\n🏁  Warehouse Day Completed! All tasks are done.\n");
        System.out.println("\n📈 Final Metrics Summary:");
        System.out.println("=========================================");
        System.out.printf("📦 ORDERS SHIPPED:        %.2f min\n", taskCompletionTimes.getOrDefault("Pack Order", 0.0));
        System.out.printf("📤 SHIPMENTS SENT:        %.2f min\n", taskCompletionTimes.getOrDefault("Send Shipment", 0.0));
        System.out.printf("📊 INVENTORY COMPLETED:   %.2f min\n", taskCompletionTimes.getOrDefault("Update Stock", 0.0));
        System.out.printf("📥 RECEIVED SHIPMENTS:    %.2f min\n", taskCompletionTimes.getOrDefault("Receive Shipment", 0.0));
        System.out.println("=========================================");
        double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.printf("\nTotal Time:             %.2f min\n", totalTime);
        System.out.printf("\n⚡ Power Outages During Simulation: %d\n", powerOutageCount);

        executor.shutdown();
        scheduler.shutdown();
    }







    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.getUserInput();
        simulation.startSimulation();
        while (!simulation.isSimulationComplete()) {
            try {
                Thread.sleep(1000); // Check once per second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
            simulation.endSimulation();

    }
}

