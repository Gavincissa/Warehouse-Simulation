# Warehouse Simulation

## Overview
This project is a **Java-based warehouse simulation** designed to model the operations of a warehouse, including order processing, inventory management, and equipment handling. The simulation is structured using **object-oriented programming (OOP)** principles, with different classes representing managers, employees, equipment, and the warehouse itself.

## Features
- **Facility Simulation**: Represents a warehouse with key attributes like location, size, and layout.
- **Role-Based Management**: Three types of managers (Logistics, Inventory, E-Commerce) delegate tasks to employees.
- **Employee Actions**: Workers such as Forklift Operators, Order Pickers, and Pallet Specialists perform specific duties.
- **Equipment Functionality**: Forklifts and pallets interact with employees to facilitate warehouse operations.
- **Inventory System**: Manages stock changes and transfers between different locations.

## Future Plans
The initial implementation will focus on setting up the warehouse structure and basic interactions. Future enhancements include:
1. **Task Scheduling System**: Implement a **task queue** to simulate real-time warehouse operations.
2. **Multithreading**: Assign tasks to employees and execute them concurrently using Java's **ExecutorService**.
3. **Data Persistence**: Integrate **Firebase** or an SQL database to store warehouse inventory and transaction records.
4. **Graphical User Interface (GUI)**: Develop a **JavaFX or Swing** interface to visualize warehouse activities.
5. **Machine Learning Optimization**: Use AI to improve task efficiency and predict order fulfillment times.
6. **Networking Support**: Implement a **client-server model** for multiple users to interact with the simulation.

## Tools & Technologies
- **Programming Language**: Java
- **IDE**: Eclipse / IntelliJ IDEA / VS Code
- **Version Control**: Git & GitHub
- **Build System**: Maven or Gradle
- **Testing Framework**: JUnit 4
- **Database** (Future): Firebase / MySQL
- **Multithreading**: Java Executors & ThreadPool
- **GUI Development** (Future): JavaFX / Swing

## Updates to Current Documentation
- Project is undergoing more in-depth literary research and the overall literature review document has been cleaned up to fit specific requirements
- UML diagram has underwent recent changed to accommodate more complex concepts and the usage of efficiency and quality attributes in key functions

## Setup Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/Warehouse-Simulation.git
   ```
2. Open the project in your preferred IDE.
3. Compile and run the `Main.java` file.
4. Modify classes under `models/` to customize behavior.

Note: This project is a work-in-progress and it is currently only designed to demonstrate basic functions at a low level
