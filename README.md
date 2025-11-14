# Capstone Team 16 – Project Documentation

## 1. Overview

This project simulates an automated pharmaceutical warehouse management system, integrating order processing, task management, agv charging, storage management, logging, and error handling. It follows a modular Java architecture using Maven. The system models a realistic environment where medicines are stored, retrieved, and managed through well-defined components.

---
## 2. System Architecture

The system is organized into several cohesive modules, each responsible for a specific domain:

### **2.1 HMI (Human–Machine Interface)**

* Class: HMI
* Acts as the primary interface between the user and the system.
* Responsible for taking user inputs (e.g., placing an order) and presenting system outputs.

### **2.2 Order Management**

* Classes: OrderManager, Order, Medicine
* Handles creation and management of customer orders.
* Defines medicines, order structure, and workflow for processing orders.

### **2.3 Storage Management**

* Classes: StorageManager, Inventory, StorageLocation, StockHandler, RoboticArm
* Simulates a warehouse environment.
* Manages:

  * Storage locations
  * Inventory levels
  * Stock adjustments
  
### **2.4 Task Management**

* Class: Task, TaskAssignable, TaskQueue, TaskManager
* Represents operational tasks such as retrieving or storing medication.

### **2.5 Logging System**

* Classes: Logger, SystemLogger, OrderLogger, LogEntry
* Maintains detailed logs of all system activities, including:

 * System event
 * Logs are stored in the "logs" directory.

### **2.6 Exception Handling**

* Too many classes that handles exceptions
* Ensures stability through controlled error responses.

### **2.7 Main Entry Point**

* Class: Main
* Initializes the system and launches the HMI.

---

## 3. Core Workflow

This section explains how the system operates internally based on the Java class logic.

### **Step 1: Application Startup**

* `Main` executes.
* The system initializes critical components:

  * Inventory
  * Storage Manager
  * Order Manager
  * Task Manager
  * Charging AGVs
  * Loggers
  * HMI interface

### **Step 2: User Interaction**

* The user interacts via the HMI class.
* The HMI prompts the user to:

  * Create orders
  * Check logs
  * View storage or inventory status
  * View orders
  * View charging AGVs

### **Step 3: Order Creation**

* Order details are passed to OrderManager.
* The system:

  * Takes name, medicine type and quantity as an input
  * Assings an order with an order-ID

### **Step 4: Logging**

Each major step is logged:

* Order received
* Task creation
* Stock access
* Errors

Logs are stored with timestamps for traceability.

---
Video link: https://www.canva.com/design/DAG4rpzq4HU/3pb0wg6FGF6ZyaFMDy5Czw/watch?utm_content=DAG4rpzq4HU&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hebcc81f531
