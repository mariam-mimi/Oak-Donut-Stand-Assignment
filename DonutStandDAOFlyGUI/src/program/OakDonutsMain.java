
package program;

import entity.DonutOrder;
import entity.DonutOrderDAO;
import entity.MenuItem;
import entity.MenuItemDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main GUI for Oak Donuts OD ordering system.
 *
 * - Left side: filters and options
 * - Middle: menu list
 * - Right: order table and totals
 */
public class OakDonutsMain extends JFrame {

    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final DonutOrderDAO orderDAO = new DonutOrderDAO();

    private JList<MenuItem> listMenu;
    private DefaultListModel<MenuItem> listModel;

    private JTable tableOrder;
    private DefaultTableModel orderModel;

    private JComboBox<String> comboCategory;
    private JTextField txtSearch;

    private JComboBox<String> comboIcing;
    private JComboBox<String> comboFilling;

    private JSpinner spinnerQty;

    private JLabel lblUnitPrice;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblTotal;

    public OakDonutsMain() {
        setTitle("Oak Donuts – Ordering Mockup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        UIManager.put("Panel.background", new Color(255, 248, 240));  // donut cream
        UIManager.put("Button.background", new Color(255, 223, 186)); // warm beige
        UIManager.put("Button.foreground", Color.DARK_GRAY);
        UIManager.put("Label.foreground", new Color(80, 50, 20));     // chocolate brown
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("List.background", Color.WHITE);
        UIManager.put("List.foreground", Color.BLACK);

        initComponents();
        loadMenuItems();
        updateTotals();
    }

    private void initComponents() {
        // --- Left panel: Filters and item options ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Oak Donuts");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(15));

        // Filters
        JLabel lblFilters = new JLabel("Filters");
        lblFilters.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilters.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblFilters);

        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(new JLabel("Category:"));
        comboCategory = new JComboBox<>(new String[]{"All", "Donut", "Drink", "Food"});
        comboCategory.addActionListener(e -> loadMenuItems());
        leftPanel.add(comboCategory);

        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField();
        txtSearch.addActionListener(e -> loadMenuItems());
        leftPanel.add(txtSearch);

        leftPanel.add(Box.createVerticalStrut(15));

        // Item options
        JLabel lblItemOptions = new JLabel("Item Options");
        lblItemOptions.setFont(new Font("SansSerif", Font.BOLD, 14));
        leftPanel.add(lblItemOptions);

        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(new JLabel("Icing:"));
        comboIcing = new JComboBox<>(new String[]{"None", "Chocolate", "Vanilla", "Strawberry"});
        leftPanel.add(comboIcing);

        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(new JLabel("Filling:"));
        comboFilling = new JComboBox<>(new String[]{"None", "Custard", "Cream", "Jelly"});
        leftPanel.add(comboFilling);

        leftPanel.add(Box.createVerticalGlue());

        // Qty + Add to order
        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeft.add(new JLabel("Qty:"));
        spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        bottomLeft.add(spinnerQty);
        bottomLeft.add(new JLabel("Unit:"));
        lblUnitPrice = new JLabel("$0.00");
        bottomLeft.add(lblUnitPrice);
        JButton btnAdd = new JButton("Add to Order");
        btnAdd.addActionListener(e -> addToOrder());
        bottomLeft.add(btnAdd);

        leftPanel.add(bottomLeft);

        add(leftPanel, BorderLayout.WEST);

        // --- Center panel: menu list ---
        listModel = new DefaultListModel<>();
        listMenu = new JList<>(listModel);
        listMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMenu.addListSelectionListener(e -> updateUnitPriceLabel());
        JScrollPane scrollMenu = new JScrollPane(listMenu);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Menu"));

        // --- Right panel: order table + totals ---
        String[] columns = {"Item", "Options", "Qty", "Price", "Total"};
        orderModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // read-only table
            }
        };
        tableOrder = new JTable(orderModel);
        JScrollPane scrollOrder = new JScrollPane(tableOrder);
        scrollOrder.setBorder(BorderFactory.createTitledBorder("Order"));

        JPanel centerRight = new JPanel(new BorderLayout(10, 0));
        centerRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerRight.add(scrollMenu, BorderLayout.WEST);
        centerRight.add(scrollOrder, BorderLayout.CENTER);

        add(centerRight, BorderLayout.CENTER);

        // --- Bottom totals + buttons ---
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new GridLayout(3, 2));
        totalsPanel.add(new JLabel("Subtotal:"));
        lblSubtotal = new JLabel("$0.00", SwingConstants.RIGHT);
        totalsPanel.add(lblSubtotal);
        totalsPanel.add(new JLabel("Tax (6%):"));
        lblTax = new JLabel("$0.00", SwingConstants.RIGHT);
        totalsPanel.add(lblTax);
        JLabel lblTotalTitle = new JLabel("Total:");
        lblTotalTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalsPanel.add(lblTotalTitle);
        lblTotal = new JLabel("$0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalsPanel.add(lblTotal);

        bottomPanel.add(totalsPanel, BorderLayout.EAST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearOrder());
        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.addActionListener(e -> checkout());
        buttonsPanel.add(btnClear);
        buttonsPanel.add(btnCheckout);

        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadMenuItems() {
        listModel.clear();
        List<MenuItem> all = menuItemDAO.getAll();
        String selectedCategory = (String) comboCategory.getSelectedItem();
        String search = txtSearch.getText().trim().toLowerCase();

        for (MenuItem item : all) {
            boolean matchesCategory = "All".equals(selectedCategory)
                    || item.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = search.isEmpty()
                    || item.getName().toLowerCase().contains(search);
            if (matchesCategory && matchesSearch) {
                listModel.addElement(item);
            }
        }

        if (!listModel.isEmpty()) {
            listMenu.setSelectedIndex(0);
        } else {
            lblUnitPrice.setText("$0.00");
        }
    }

    private void updateUnitPriceLabel() {
        MenuItem selected = listMenu.getSelectedValue();
        if (selected != null) {
            lblUnitPrice.setText(String.format("$%.2f", selected.getUnitPrice()));
        }
    }

    private void addToOrder() {
        MenuItem selected = listMenu.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an item from the menu.");
            return;
        }
        int qty = (Integer) spinnerQty.getValue();
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be at least 1.");
            return;
        }

        String icing = (String) comboIcing.getSelectedItem();
        String filling = (String) comboFilling.getSelectedItem();

        String options;
        if ("None".equals(icing) && "None".equals(filling)) {
            options = "-";
        } else {
            StringBuilder sb = new StringBuilder();
            if (!"None".equals(icing)) {
                sb.append("Icing: ").append(icing);
            }
            if (!"None".equals(filling)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("Filling: ").append(filling);
            }
            options = sb.toString();
        }

        double price = selected.getUnitPrice();
        double total = price * qty;

        orderModel.addRow(new Object[]{
                selected.getName(),
                options,
                qty,
                String.format("$%.2f", price),
                String.format("$%.2f", total)
        });

        updateTotals();
    }

    private void clearOrder() {
        orderModel.setRowCount(0);
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = 0.0;
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            String totalStr = (String) orderModel.getValueAt(i, 4); // "$x.yy"
            subtotal += parseMoney(totalStr);
        }
        double tax = subtotal * 0.06;
        double total = subtotal + tax;

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblTax.setText(String.format("$%.2f", tax));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private double parseMoney(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        return Double.parseDouble(s.replace("$", ""));
    }

    private void checkout() {
        if (orderModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in the order.");
            return;
        }

        double subtotal = parseMoney(lblSubtotal.getText());
        double tax = parseMoney(lblTax.getText());
        double total = parseMoney(lblTotal.getText());

        // Build itemsDetails string summarizing each line in the order.
        StringBuilder details = new StringBuilder();
        for (int i = 0; i < orderModel.getRowCount(); i++) {
            String itemName = (String) orderModel.getValueAt(i, 0);
            String options = (String) orderModel.getValueAt(i, 1);
            int qty = (Integer) orderModel.getValueAt(i, 2);
            if (i > 0) {
                details.append("; ");
            }
            details.append(qty).append("x ").append(itemName);
            if (!"-".equals(options)) {
                details.append(" (").append(options).append(")");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        DonutOrder order = new DonutOrder(timestamp, details.toString(), subtotal, tax, total);
        orderDAO.insert(order);

        JOptionPane.showMessageDialog(
                this,
                String.format("Thank you!\nTotal due: $%.2f\n(This is a mockup – no payment processed.)", total),
                "Checkout",
                JOptionPane.INFORMATION_MESSAGE
        );

        clearOrder();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OakDonutsMain().setVisible(true);
        });
    }
}

