package HMI;

import ChargingStation.AGV;
import ChargingStation.ChargingManager;
import ChargingStation.ChargingQueue;
import StorageManagement.StorageLocation;
import StorageManagement.StorageManager;
import TaskManagement.TaskManager;
import Logging.SystemLogger;
import Database.DbReader;
import OrderManagement.Medicine;
import Exceptions.StorageException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HMI extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    // Backend components (may be re-created on restart)
    public StorageManager storageManager;
    private TaskManager taskManager;
    private ChargingManager chargingManager;
    private SystemLogger logger = new SystemLogger();

    // System running flag
    private final AtomicBoolean running = new AtomicBoolean(true);

    // Order page components
    private JTextField nameField;
    private JComboBox<String> medicineCombo;               // user-facing medicine names
    private JSpinner qtySpinner;
    private JLabel orderResultLabel;                       // small result below buttons
    private JLabel bigOrderIdLabel;                        // large centered order id

    // Monitor page components
    private DefaultTableModel agvTableModel;
    private DefaultTableModel chargingModel;
    private DefaultTableModel stockTableModel;
    private DefaultTableModel orderTableModel; // NEW: separate order table
    private JTextArea logArea;

    // Mapping medicineName -> storageLocationId (one-to-one for simplicity)
    private final Map<String, String> medicineToLocation = new HashMap<>();

    // Track orders: orderId -> status
    private final Map<String, String> orderStatus = Collections.synchronizedMap(new LinkedHashMap<>());

    // Timer for UI refresh
    private javax.swing.Timer refreshTimer;
    private javax.swing.Timer clockTimer;

    // Simulation state for AGVs (id -> battery% and charging status)
    private final Map<String, Integer> agvBattery = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, String> agvChargingStatus = Collections.synchronizedMap(new HashMap<>()); // "IDLE","CHARGING","ERROR"

    private final Random random = new Random();
    private Thread agvSimThread;

    public HMI() throws java.io.IOException {
        super("Pharmacy Simulation HMI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 740);
        setLocationRelativeTo(null);

        // Build UI first so logArea and components exist
        initUI();

        // Now create backend instances and seed data
        startSystemComponents();
        seedSampleData();

        // Start background processes
        startBackgroundRefresh();
        startClock();
        startAgvSimulationThread();
    }

    // (re)create backend components
    private void startSystemComponents() {
        try {
            if (storageManager != null) storageManager.shutdown();
        } catch (Exception ignored) {}
        try { if (taskManager != null) taskManager.shutdown(); } catch (Exception ignored) {}
        try { if (chargingManager != null) chargingManager.shutdown(); } catch (Exception ignored) {}

        storageManager = new StorageManager("robotic-arm-1");
        taskManager = new TaskManager(2);
        chargingManager = new ChargingManager(Arrays.asList("CS-1", "CS-2"), 60L); // 2 charging stations
        running.set(true);
        safeAppendLog("System components started.");
    }

    private void stopSystemComponents() {
        running.set(false);
        if (refreshTimer != null) refreshTimer.stop();
        if (clockTimer != null) clockTimer.stop();
        try { taskManager.shutdown(); } catch (Exception ignored) {}
        try { storageManager.shutdown(); } catch (Exception ignored) {}
        try { chargingManager.shutdown(); } catch (Exception ignored) {}
        safeAppendLog("System components stopped.");
        // stop AGV sim thread
        if (agvSimThread != null) {
            agvSimThread.interrupt();
            agvSimThread = null;
        }
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Date and clock
        JLabel dateLabel = new JLabel();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateLabel.setText(LocalDate.now().format(df));
        JLabel clockLabel = new JLabel();
        JLabel dayLabel = new JLabel(LocalDate.now().getDayOfWeek().toString());
        headerLeft.add(dateLabel);
        headerLeft.add(new JLabel(" | "));
        headerLeft.add(dayLabel);

        headerRight.add(clockLabel);

        // Start/Stop on header
        JButton startBtn = new JButton("Start System");
        JButton stopBtn = new JButton("Stop System");
        startBtn.addActionListener(e -> {
            if (!running.get()) {
                startSystemComponents();
                seedSampleData();
                startBackgroundRefresh();
                startClock();
                startAgvSimulationThread();
            }
        });
        stopBtn.addActionListener(e -> stopSystemComponents());
        headerRight.add(startBtn); headerRight.add(stopBtn);

        header.add(headerLeft, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        cards.add(createMainPage(), "MAIN");
        cards.add(createOrderPage(), "ORDER");
        cards.add(createMonitorPage(), "MONITOR");

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        // clock timer updates clockLabel and date/day every second
        clockTimer = new javax.swing.Timer(1000, e -> {
            clockLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            dateLabel.setText(LocalDate.now().format(df));
            dayLabel.setText(LocalDate.now().getDayOfWeek().toString());
        });
    }

    private JPanel createMainPage() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel center = new JPanel();
        center.setBorder(new EmptyBorder(40, 40, 40, 40));
        center.setLayout(new GridLayout(2, 2, 20, 20));

        JButton orderBtn = new JButton("Place an Order");
        JButton monitorBtn = new JButton("Monitor System");

        orderBtn.addActionListener(e -> {
            populateMedicineCombo();
            cardLayout.show(cards, "ORDER");
        });
        monitorBtn.addActionListener(e -> cardLayout.show(cards, "MONITOR"));

        center.add(orderBtn);
        center.add(monitorBtn);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel createOrderPage() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Customer name:"), c);
        c.gridx = 1; nameField = new JTextField(20); form.add(nameField, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Medicine:"), c);
        c.gridx = 1; medicineCombo = new JComboBox<>(); medicineCombo.setPrototypeDisplayValue("XXXXXXXXXXXX"); form.add(medicineCombo, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Quantity:"), c);
        c.gridx = 1; qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1)); form.add(qtySpinner, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton place = new JButton("Place Order");
        JButton back = new JButton("Back");
        orderResultLabel = new JLabel(" ");

        place.addActionListener(this::onPlaceOrderEnqueueOnly);
        back.addActionListener(e -> {
            // clear small result, keep big order id visible
            orderResultLabel.setText(" ");
            cardLayout.show(cards, "MAIN");
        });

        buttons.add(place); buttons.add(back); buttons.add(orderResultLabel);

        // big order id in center-top
        bigOrderIdLabel = new JLabel("", SwingConstants.CENTER);
        bigOrderIdLabel.setFont(bigOrderIdLabel.getFont().deriveFont(28f));
        bigOrderIdLabel.setBorder(new EmptyBorder(10,10,10,10));

        p.add(bigOrderIdLabel, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createMonitorPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridLayout(1, 3, 8, 8));

        // AGV block
        agvTableModel = new DefaultTableModel(new Object[]{"AGV ID","State","Charge (%)"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable agvTable = new JTable(agvTableModel);
        JScrollPane agvScroll = new JScrollPane(agvTable);
        JPanel agvPanel = new JPanel(new BorderLayout());
        agvPanel.add(new JLabel("AGV Status"), BorderLayout.NORTH);
        agvPanel.add(agvScroll, BorderLayout.CENTER);

        // Charging station block
        chargingModel = new DefaultTableModel(new Object[]{"Station ID","AGV ID","Charging Status","%"},0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable chargingTable = new JTable(chargingModel);
        JScrollPane chargingScroll = new JScrollPane(chargingTable);
        JPanel chargingPanel = new JPanel(new BorderLayout());
        chargingPanel.add(new JLabel("Charging Stations"), BorderLayout.NORTH);
        chargingPanel.add(chargingScroll, BorderLayout.CENTER);

        // Stock block with medicine name and quantity (sourced from medicines.csv)
        // NOTE: removed Order Status column from Inventory as requested
        stockTableModel = new DefaultTableModel(new Object[]{"Medicine","Location","Quantity"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable stockTable = new JTable(stockTableModel);
        JScrollPane stockScroll = new JScrollPane(stockTable);
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.add(new JLabel("Inventory"), BorderLayout.NORTH);
        inventoryPanel.add(stockScroll, BorderLayout.CENTER);

        // NEW: Orders panel (separate block stacked below inventory)
        orderTableModel = new DefaultTableModel(new Object[]{"Order ID","Order Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable orderTable = new JTable(orderTableModel);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.add(new JLabel("Orders (real-time)"), BorderLayout.NORTH);
        ordersPanel.add(orderScroll, BorderLayout.CENTER);

        // Stack inventory and orders vertically using GridLayout
        JPanel stockPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        stockPanel.add(inventoryPanel);
        stockPanel.add(ordersPanel);

        top.add(agvPanel);
        top.add(chargingPanel);
        top.add(stockPanel);

        // Logs
        logArea = new JTextArea(10, 20);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(new JLabel("System Logs (real-time)"), BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton restartBtn = new JButton("Restart System");
        JButton stopBtn = new JButton("Stop System");
        JButton backBtn = new JButton("Back");

        restartBtn.addActionListener(e -> {
            appendLog("Restart requested.", Color.BLUE);
            restartSystem();
        });
        stopBtn.addActionListener(e -> {
            appendLog("Stop requested.", Color.RED);
            stopSystemComponents();
        });
        backBtn.addActionListener(e -> cardLayout.show(cards, "MAIN"));

        controlPanel.add(restartBtn); controlPanel.add(stopBtn); controlPanel.add(backBtn);

        p.add(top, BorderLayout.CENTER);
        p.add(controlPanel, BorderLayout.NORTH);
        p.add(logPanel, BorderLayout.SOUTH);

        return p;
    }

    // Enqueue-only order: creates task and does NOT remove stock immediately.
    private void onPlaceOrderEnqueueOnly(ActionEvent ev) {
        if (!running.get()) {
            JOptionPane.showMessageDialog(this, "System is stopped. Start the system first.", "System Stopped", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String med = (String) medicineCombo.getSelectedItem();
        Integer qty = (Integer) qtySpinner.getValue();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (med == null || med.equals("NONE")) {
            JOptionPane.showMessageDialog(this, "No medicine selected.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = "ORD-" + System.currentTimeMillis();
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Map medicine to storage location for AGV tasks
        String loc = medicineToLocation.get(med);
        if (loc == null) loc = "LOC-1";

        try {
            // register order in UI tracking
            orderStatus.put(orderId, "CREATED");
            appendLog("Order created: " + orderId + " (" + med + " x" + qty + ")", Color.GREEN.darker());
            bigOrderIdLabel.setText(orderId);
            orderResultLabel.setText("Order enqueued. Order ID: " + orderId);

            // clear user inputs AFTER order placed
            nameField.setText("");
            qtySpinner.setValue(1);
            medicineCombo.setSelectedIndex(-1);

            // create backend task
            taskManager.createTask("PICK", loc, "DISPATCH", "PENDING", createdAt);

            // simulate asynchronous progress for this order
            simulateOrderProgress(orderId, med, loc, qty);

        } catch (Exception ex) {
            appendLog("Error enqueuing order: " + ex.getMessage(), Color.RED);
            orderResultLabel.setText("Enqueue failed: " + ex.getMessage());
        }
    }

    // Simulate order progress in background (updates orderStatus map and UI/logs)
    private void simulateOrderProgress(String orderId, String med, String loc, int qty) {
        // run in background thread so UI remains responsive
        new Thread(() -> {
            try {
                orderStatus.put(orderId, "PLACED");
                appendLog("Order " + orderId + " status -> PLACED", Color.BLUE);
                Thread.sleep(1500);

                orderStatus.put(orderId, "IN PROCESS");
                appendLog("Order " + orderId + " status -> IN PROCESS", Color.BLUE);

                // Simulate time to pick (wait) and then deduct stock
                Thread.sleep(2000);
                try {
                    StorageLocation s = storageManager.getStorageLocations().stream().filter(x -> x.getId().equals(loc)).findFirst().orElse(null);
                    if (s != null) {
                        storageManager.removeStockSync(s, qty);
                        appendLog("Stock removed: " + qty + " from " + loc, Color.DARK_GRAY);
                    } else {
                        appendLog("Storage location not found for order " + orderId, Color.RED);
                        orderStatus.put(orderId, "CANCELLED");
                        return;
                    }
                } catch (Exception se) {
                    appendLog("Failed to remove stock for " + orderId + ": " + se.getMessage(), Color.RED);
                    orderStatus.put(orderId, "CANCELLED");
                    return;
                }

                Thread.sleep(1500);
                orderStatus.put(orderId, "COMPLETED");
                appendLog("Order " + orderId + " status -> COMPLETED", Color.GREEN.darker());

            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void populateMedicineCombo() {
        medicineCombo.removeAllItems();
        medicineToLocation.clear();

        // Provide fixed medicine list for the user
        String[] meds = getMedicineList();

        List<StorageLocation> locs = storageManager.getStorageLocations();
        for (int i = 0; i < meds.length; i++) {
            String med = meds[i];
            medicineCombo.addItem(med);
            // map medicines to storage locations round-robin
            if (!locs.isEmpty()) {
                StorageLocation sl = locs.get(i % locs.size());
                medicineToLocation.put(med, sl.getId());
            }
        }
        medicineCombo.setSelectedIndex(-1);
    }

    // Single source of medicine list — load from CSV (resources/database/medicine.csv)
    private String[] getMedicineList() {
        try {
            List<Medicine> meds = DbReader.loadMedicines();
            if (meds == null || meds.isEmpty()) return new String[0];
            String[] names = new String[meds.size()];
            for (int i = 0; i < meds.size(); i++) names[i] = meds.get(i).getName();
            return names;
        } catch (Exception e) {
            safeAppendLog("Failed to load medicines from CSV: " + e.getMessage());
            return new String[0];
        }
    }

    private void startBackgroundRefresh() {
        if (refreshTimer != null) refreshTimer.stop();
        refreshTimer = new javax.swing.Timer(1000, e -> refreshStatus());
        refreshTimer.start();
    }

    private void startClock() {
        if (clockTimer != null) clockTimer.start();
        else clockTimer = new javax.swing.Timer(1000, ev -> {});
        clockTimer.start();
    }

    private void refreshStatus() {
        if (!running.get()) return;
        SwingUtilities.invokeLater(() -> {
            try {
                // AGV table
                agvTableModel.setRowCount(0);
                for (Map.Entry<String,Integer> entry : agvBattery.entrySet()) {
                    String id = entry.getKey();
                    int bat = entry.getValue();
                    String charging = agvChargingStatus.getOrDefault(id, "IDLE");
                    agvTableModel.addRow(new Object[]{id, charging, bat});
                }
                if (agvTableModel.getRowCount() == 0) agvTableModel.addRow(new Object[]{"-","-","-"});

                // Charging stations
                chargingModel.setRowCount(0);
                List<String> chargingAgg = new ArrayList<>();
                for (Map.Entry<String,String> e : agvChargingStatus.entrySet()) {
                    if ("CHARGING".equals(e.getValue())) chargingAgg.add(e.getKey());
                }
                for (int i = 0; i < 2; i++) {
                    String station = "CS-" + (i+1);
                    String agvId = i < chargingAgg.size() ? chargingAgg.get(i) : "-";
                    String status = agvId.equals("-") ? "IDLE" : "CHARGING";
                    int pct = agvId.equals("-") ? 0 : agvBattery.getOrDefault(agvId, 0);
                    chargingModel.addRow(new Object[]{station, agvId, status, pct});
                }

                // Stock table: load medicines from CSV so ordering and inventory use same source
                stockTableModel.setRowCount(0);
                for (Map.Entry<String,String> e : medicineToLocation.entrySet()) {
                String med = e.getKey();
                String loc = e.getValue();
                StorageLocation s = storageManager.getStorageLocations().stream().filter(x -> x.getId().equals(loc)).findFirst().orElse(null);
                int qty = s == null ? 0 : storageManager.getInventory().getStock(s);
                stockTableModel.addRow(new Object[]{med, loc, qty});
                }

                // Orders table (NEW): reflect current orderStatus map in real-time
                orderTableModel.setRowCount(0);
                synchronized (orderStatus) {
                    for (Map.Entry<String,String> os : orderStatus.entrySet()) {
                        orderTableModel.addRow(new Object[]{os.getKey(), os.getValue()});
                    }
                }

                // scroll logs to bottom
                if (logArea != null) logArea.setCaretPosition(logArea.getDocument().getLength());

            } catch (Exception ex) {
                safeAppendLog("Error refreshing status: " + ex.getMessage());
            }
        });
    }

    private void startAgvSimulationThread() {
        // stop existing thread if any
        if (agvSimThread != null && agvSimThread.isAlive()) return;

        // initialize batteries map from charging queue
        ChargingQueue q = chargingManager.getQueue();
        for (Object o : q.getQueue().toArray()) {
            if (o instanceof AGV) {
                AGV a = (AGV)o;
                agvBattery.put(a.getId(), a.getBatteryLevel());
                agvChargingStatus.put(a.getId(), "IDLE");
            }
        }

        agvSimThread = new Thread(() -> {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // drain/charge AGVs
                    for (String id : new ArrayList<>(agvBattery.keySet())) {
                        int bat = agvBattery.getOrDefault(id, 50);
                        String state = agvChargingStatus.getOrDefault(id, "IDLE");
                        if ("CHARGING".equals(state)) {
                            bat = Math.min(100, bat + 10);
                            if (bat >= 100) {
                                agvChargingStatus.put(id, "READY");
                                safeAppendLog("AGV " + id + " finished charging.");
                            }
                        } else {
                            // working: drain small amount
                            bat = Math.max(0, bat - (1 + random.nextInt(3)));
                            if (bat <= 20 && !"CHARGING".equals(agvChargingStatus.get(id))) {
                                // send to charge
                                agvChargingStatus.put(id, "CHARGING");
                                safeAppendLog("AGV " + id + " battery low (" + bat + "%); sending to charge.");
                            }
                        }
                        agvBattery.put(id, bat);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ignored) {}
            }
        }, "AGV-SIM");
        agvSimThread.setDaemon(true);
        agvSimThread.start();
    }

    private void appendLog(String msg, Color color) {
        String ts = new SimpleDateFormat("HH:mm:ss").format(new Date());
        if (logArea != null) logArea.append("[" + ts + "] " + msg + "\n"); // NEW: write each log on its own line
        logger.logInfo("HMI", msg);
    }

    // used before logArea is available
    private void safeAppendLog(String msg) {
        try { appendLog(msg, Color.BLACK); } catch (Exception ignored) {}
    }

    private void seedSampleData() {
        try {
            if (storageManager.getStorageLocations().isEmpty()) {
                for (int i = 1; i <= 5; i++) {
                    try {
                        StorageLocation sl = new StorageLocation("LOC-" + i, 100);
                        storageManager.addStorageLocation(sl);
                        storageManager.addStock(sl, 50 + random.nextInt(50));
                    } catch (StorageException se) {
                        // ignore
                    }
                }
                safeAppendLog("Sample storage locations created.");
            }

            ChargingQueue q = chargingManager.getQueue();
            if (q.isEmpty()) {
                for (int i = 1; i <= 5; i++) {
                    AGV agv = new AGV("AGV-" + i, 20 + random.nextInt(80), false);
                    q.add(agv);
                    agvBattery.put(agv.getId(), agv.getBatteryLevel());
                    agvChargingStatus.put(agv.getId(), "IDLE");
                }
                safeAppendLog("Sample AGVs seeded into charging queue.");
            }

            // set up medicine mapping using the same medicine list as user-facing combo
            if (medicineToLocation.isEmpty()) {
                String[] meds = getMedicineList();
                List<StorageLocation> locs = storageManager.getStorageLocations();
                for (int i = 0; i < meds.length; i++) {
                    String medName = meds[i];
                    StorageLocation sl = locs.isEmpty() ? null : locs.get(i % locs.size());
                    medicineToLocation.put(medName, sl == null ? "LOC-1" : sl.getId());
                }
            }

        } catch (Exception e) {
            safeAppendLog("Error seeding sample data: " + e.getMessage());
        }
    }

    private void restartSystem() {
        appendLog("Restarting system...", Color.BLUE);
        stopSystemComponents();
        // small pause
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        startSystemComponents();
        seedSampleData();
        startBackgroundRefresh();
        startClock();
        startAgvSimulationThread();
        appendLog("System restarted.", Color.BLUE);
    }

    // allow the program to be closed gracefully
    @Override
    public void dispose() {
        try {
            if (refreshTimer != null) refreshTimer.stop();
            if (clockTimer != null) clockTimer.stop();
            if (taskManager != null) taskManager.shutdown();
            if (storageManager != null) storageManager.shutdown();
            if (chargingManager != null) chargingManager.shutdown();
            if (agvSimThread != null) agvSimThread.interrupt();
        } catch (Exception ignored) {}
        super.dispose();
    }

}
