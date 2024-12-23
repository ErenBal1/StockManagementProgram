import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

// Birim enum'u
enum Unit {
    ADET("Adet"),
    KG("Kg"),
    LITRE("Litre"),
    METRE("Metre");

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

// İşlem tipi enum'u
enum TransactionType {
    ADDITION,
    REMOVAL
}

// Stok sınıfı
class Stock {
    private String name;
    private int quantity;
    private double price;
    private Unit unit;
    private List<StockTransaction> transactions;

    public Stock(String name, int quantity, double price, Unit unit) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.unit = unit;
        this.transactions = new ArrayList<>();
        addTransaction(quantity, price, TransactionType.ADDITION);
    }

    public void addQuantity(int quantity, double price) {
        this.quantity += quantity;
        this.price = price;
        addTransaction(quantity, price, TransactionType.ADDITION);
    }

    public void removeQuantity(int quantity, double price) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            addTransaction(quantity, price, TransactionType.REMOVAL);
        }
    }

    private void addTransaction(int quantity, double price, TransactionType type) {
        transactions.add(new StockTransaction(quantity, price, type));
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Unit getUnit() { return unit; }
    public List<StockTransaction> getTransactions() { return transactions; }
}

// İşlem sınıfı
class StockTransaction {
    private LocalDateTime dateTime;
    private int quantity;
    private double price;
    private TransactionType type;

    public StockTransaction(int quantity, double price, TransactionType type) {
        this.dateTime = LocalDateTime.now();
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public TransactionType getType() { return type; }
}

// Gradient Panel
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 245),
                0, getHeight(), new Color(220, 220, 225));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

// StockManager sınıfı
class StockManager {
    private Map<String, Stock> stocks;

    public StockManager() {
        stocks = new HashMap<>();
    }

    public void addStock(String name, int quantity, double price, Unit unit) {
        if (stocks.containsKey(name)) {
            Stock stock = stocks.get(name);
            stock.addQuantity(quantity, price);
        } else {
            stocks.put(name, new Stock(name, quantity, price, unit));
        }
    }

    public void removeStock(String name, int quantity, double price) {
        if (stocks.containsKey(name)) {
            Stock stock = stocks.get(name);
            stock.removeQuantity(quantity, price);
        }
    }

    public Stock getStock(String name) {
        return stocks.get(name);
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }
}

// Ana program sınıfı
class StockManagementSystem extends JFrame {
    private StockManager stockManager;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final Color THEME_COLOR = new Color(60, 141, 188);
    private static final Color BUTTON_COLOR = new Color(60, 141, 188);
    private static final Color HOVER_COLOR = new Color(45, 125, 170);
    private DecimalFormat priceFormat = new DecimalFormat("#,##0.00 TL");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public StockManagementSystem() {
        stockManager = new StockManager();
        setTitle("Stok Yönetim Sistemi");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupTheme();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createMainPanel();

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void setupTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        UIManager.put("Button.arc", 15);
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
    }

    private void createLoginPanel() {
        GradientPanel loginPanel = new GradientPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Stok Yönetim Sistemi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(THEME_COLOR);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(THEME_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Giriş Yap");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        loginPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> checkLogin());

        mainPanel.add(loginPanel, "login");
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(THEME_COLOR, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(THEME_COLOR, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 35));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private void createMainPanel() {
        JPanel mainMenuPanel = new GradientPanel();
        mainMenuPanel.setLayout(new BorderLayout(10, 10));

        // Üst menü
        JPanel topMenu = new JPanel();
        topMenu.setOpaque(false);
        topMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addStockBtn = createMenuButton("Stok Ekle", null);
        JButton removeStockBtn = createMenuButton("Stok Çıkar", null);
        JButton viewStockBtn = createMenuButton("Stok Görüntüle", null);
        JButton recentTransactionsBtn = createMenuButton("Son İşlemler", null);
        JButton reportBtn = createMenuButton("Raporlar", null);
        JButton logoutBtn = createMenuButton("Çıkış", null);

        topMenu.add(addStockBtn);
        topMenu.add(removeStockBtn);
        topMenu.add(viewStockBtn);
        topMenu.add(recentTransactionsBtn);
        topMenu.add(reportBtn);
        topMenu.add(logoutBtn);

        // İçerik paneli
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        contentPanel.add(createStyledPanel(createAddStockPanel()), "addStock");
        contentPanel.add(createStyledPanel(createRemoveStockPanel()), "removeStock");
        contentPanel.add(createStyledPanel(createViewStockPanel()), "viewStock");
        contentPanel.add(createStyledPanel(createRecentTransactionsPanel()), "recentTransactions");
        contentPanel.add(createStyledPanel(createReportPanel()), "reports");

        mainMenuPanel.add(topMenu, BorderLayout.NORTH);
        mainMenuPanel.add(contentPanel, BorderLayout.CENTER);

        addStockBtn.addActionListener(e -> showPanel(contentPanel, "addStock"));
        removeStockBtn.addActionListener(e -> showPanel(contentPanel, "removeStock"));
        viewStockBtn.addActionListener(e -> showPanel(contentPanel, "viewStock"));
        recentTransactionsBtn.addActionListener(e -> showPanel(contentPanel, "recentTransactions"));
        reportBtn.addActionListener(e -> showPanel(contentPanel, "reports"));
        logoutBtn.addActionListener(e -> logout());

        mainPanel.add(mainMenuPanel, "main");
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 40));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private JPanel createAddStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Arama alanları
        JTextField searchField = createStyledTextField();
        JButton searchButton = createStyledButton("Ara");
        JComboBox<String> searchResults = new JComboBox<>();

        // Stok bilgi alanları
        JTextField nameField = createStyledTextField();
        JTextField quantityField = createStyledTextField();
        JComboBox<Unit> unitComboBox = new JComboBox<>(Unit.values());
        JTextField priceField = createStyledTextField();
        JButton addButton = createStyledButton("Ekle");

        nameField.setEditable(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Ürün Ara:"), gbc);
        gbc.gridx = 1;
        panel.add(searchField, gbc);
        gbc.gridx = 2;
        panel.add(searchButton, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(searchResults, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ürün Adı:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Miktar:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);
        gbc.gridx = 2;
        panel.add(unitComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(addButton, gbc);

        // Arama işlevi
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase().trim();
            searchResults.removeAllItems();
            searchResults.addItem("Yeni Ürün Ekle");

            for (Stock stock : stockManager.getAllStocks()) {
                if (stock.getName().toLowerCase().contains(searchTerm)) {
                    searchResults.addItem(stock.getName());
                }
            }
        });

        // ComboBox seçim işlevi
        searchResults.addActionListener(e -> {
            String selected = (String) searchResults.getSelectedItem();
            if (selected != null) {
                if (selected.equals("Yeni Ürün Ekle")) {
                    nameField.setText("");
                    nameField.setEditable(true);
                    unitComboBox.setEnabled(true);
                } else {
                    nameField.setText(selected);
                    nameField.setEditable(false);
                    Stock stock = stockManager.getStock(selected);
                    if (stock != null) {
                        priceField.setText(String.valueOf(stock.getPrice()));
                        unitComboBox.setSelectedItem(stock.getUnit());
                        unitComboBox.setEnabled(false);
                    }
                }
            }
        });

        // Ekleme butonu işlevi
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen bir ürün adı girin!");
                    return;
                }

                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                Unit selectedUnit = (Unit) unitComboBox.getSelectedItem();

                if (nameField.isEditable()) {
                    if (stockManager.getStock(name) != null) {
                        int response = JOptionPane.showConfirmDialog(this,
                                "\"" + name + "\" isimli ürün zaten mevcut. Bu ürüne ekleme yapmak ister misiniz?",
                                "Ürün Mevcut",
                                JOptionPane.YES_NO_OPTION);

                        if (response == JOptionPane.YES_OPTION) {
                            stockManager.addStock(name, quantity, price, selectedUnit);
                            JOptionPane.showMessageDialog(this, "Stok başarıyla güncellendi!");
                        }
                    } else {
                        stockManager.addStock(name, quantity, price, selectedUnit);
                        JOptionPane.showMessageDialog(this, "Yeni stok başarıyla eklendi!");
                    }
                } else {
                    stockManager.addStock(name, quantity, price, selectedUnit);
                    JOptionPane.showMessageDialog(this, "Stok başarıyla güncellendi!");
                }

                // Alanları temizle
                searchField.setText("");
                nameField.setText("");
                quantityField.setText("");
                priceField.setText("");
                searchResults.removeAllItems();
                nameField.setEditable(false);
                unitComboBox.setEnabled(true);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz miktar veya fiyat!");
            }
        });

        return panel;
    }

    private JPanel createRemoveStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField searchField = createStyledTextField();
        JButton searchButton = createStyledButton("Ara");
        JComboBox<String> searchResults = new JComboBox<>();

        JTextField nameField = createStyledTextField();
        JTextField quantityField = createStyledTextField();
        JTextField priceField = createStyledTextField();
        JLabel currentQuantityLabel = new JLabel("");
        JButton removeButton = createStyledButton("Çıkar");

        nameField.setEditable(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Ürün Ara:"), gbc);
        gbc.gridx = 1;
        panel.add(searchField, gbc);
        gbc.gridx = 2;
        panel.add(searchButton, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(searchResults, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ürün Adı:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Çıkarılacak Miktar:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Satış Fiyatı:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(currentQuantityLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(removeButton, gbc);

        // Search button action
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().toLowerCase().trim();
            searchResults.removeAllItems();

            for (Stock stock : stockManager.getAllStocks()) {
                if (stock.getName().toLowerCase().contains(searchTerm)) {
                    searchResults.addItem(stock.getName());
                }
            }
        });

        // Search results selection action
        searchResults.addActionListener(e -> {
            String selected = (String) searchResults.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected);
                Stock stock = stockManager.getStock(selected);
                if (stock != null) {
                    currentQuantityLabel.setText("Mevcut Miktar: " + stock.getQuantity() + " " + stock.getUnit());
                    priceField.setText(String.valueOf(stock.getPrice()));
                }
            }
        });

        // Remove button action
        removeButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçin!");
                    return;
                }

                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                Stock stock = stockManager.getStock(name);

                if (stock != null) {
                    if (stock.getQuantity() >= quantity) {
                        stockManager.removeStock(name, quantity, price);
                        JOptionPane.showMessageDialog(this, "Stok başarıyla çıkarıldı!");

                        // Clear all fields
                        searchField.setText("");
                        nameField.setText("");
                        quantityField.setText("");
                        priceField.setText("");
                        currentQuantityLabel.setText("");
                        searchResults.removeAllItems();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Yetersiz stok miktarı! Mevcut miktar: " + stock.getQuantity() + " " + stock.getUnit());
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz miktar veya fiyat!");
            }
        });

        return panel;
    }

    private JPanel createViewStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"Ürün Adı", "Miktar", "Birim", "Birim Fiyat", "Toplam Değer"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = createStyledButton("Yenile");

        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            for (Stock stock : stockManager.getAllStocks()) {
                double totalValue = stock.getQuantity() * stock.getPrice();
                model.addRow(new Object[]{
                        stock.getName(),
                        stock.getQuantity(),
                        stock.getUnit(),
                        priceFormat.format(stock.getPrice()),
                        priceFormat.format(totalValue)
                });
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"Tarih", "Ürün", "İşlem", "Miktar", "Birim", "Fiyat", "Toplam"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                String transactionType = (String) table.getModel().getValueAt(row, 2);
                if ("EKLEME".equals(transactionType)) {
                    c.setForeground(new Color(0, 150, 0));
                } else if ("ÇIKARMA".equals(transactionType)) {
                    c.setForeground(new Color(200, 0, 0));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = createStyledButton("Yenile");

        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            List<Object[]> transactions = new ArrayList<>();

            for (Stock stock : stockManager.getAllStocks()) {
                for (StockTransaction trans : stock.getTransactions()) {
                    double total = trans.getQuantity() * trans.getPrice();
                    transactions.add(new Object[]{
                            trans.getDateTime().format(dateFormatter),
                            stock.getName(),
                            trans.getType() == TransactionType.ADDITION ? "EKLEME" : "ÇIKARMA",
                            trans.getQuantity(),
                            stock.getUnit(),
                            priceFormat.format(trans.getPrice()),
                            priceFormat.format(total)
                    });
                }
            }

            transactions.sort((a, b) -> String.valueOf(b[0]).compareTo(String.valueOf(a[0])));
            transactions.forEach(model::addRow);
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createStockValueReportPanel());
        mainPanel.add(createMonthlySalesReportPanel());
        mainPanel.add(createLowStockReportPanel());
        mainPanel.add(createProfitReportPanel());

        return mainPanel;
    }

    private JPanel createStockValueReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Stok Değer Raporu"));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ürün", "Miktar", "Birim", "Birim Fiyat", "Toplam"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateButton = createStyledButton("Rapor Oluştur");
        JLabel lastGeneratedLabel = new JLabel("");

        generateButton.addActionListener(e -> {
            model.setRowCount(0);
            double totalValue = 0;

            for (Stock stock : stockManager.getAllStocks()) {
                double stockValue = stock.getQuantity() * stock.getPrice();
                totalValue += stockValue;
                model.addRow(new Object[]{
                        stock.getName(),
                        stock.getQuantity(),
                        stock.getUnit(),
                        priceFormat.format(stock.getPrice()),
                        priceFormat.format(stockValue)
                });
            }

            model.addRow(new Object[]{"TOPLAM", "", "", "", priceFormat.format(totalValue)});
            lastGeneratedLabel.setText("Son güncelleme: " +
                    LocalDateTime.now().format(dateFormatter));
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(generateButton, BorderLayout.WEST);
        bottomPanel.add(lastGeneratedLabel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMonthlySalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Aylık Satış Raporu"));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Tarih", "Ürün", "Miktar", "Birim", "Satış Fiyatı", "Toplam"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateButton = createStyledButton("Rapor Oluştur");
        JLabel lastGeneratedLabel = new JLabel("");

        generateButton.addActionListener(e -> {
            model.setRowCount(0);
            double totalSales = 0;
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

            for (Stock stock : stockManager.getAllStocks()) {
                for (StockTransaction trans : stock.getTransactions()) {
                    if (trans.getDateTime().isAfter(startOfMonth) &&
                            trans.getType() == TransactionType.REMOVAL) {
                        double amount = trans.getQuantity() * trans.getPrice();
                        totalSales += amount;
                        model.addRow(new Object[]{
                                trans.getDateTime().format(dateFormatter),
                                stock.getName(),
                                trans.getQuantity(),
                                stock.getUnit(),
                                priceFormat.format(trans.getPrice()),
                                priceFormat.format(amount)
                        });
                    }
                }
            }

            model.addRow(new Object[]{"TOPLAM", "", "", "", "", priceFormat.format(totalSales)});
            lastGeneratedLabel.setText("Son güncelleme: " +
                    LocalDateTime.now().format(dateFormatter));
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(generateButton, BorderLayout.WEST);
        bottomPanel.add(lastGeneratedLabel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLowStockReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Düşük Stok Raporu"));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ürün", "Mevcut Miktar", "Birim", "Durum"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateButton = createStyledButton("Rapor Oluştur");
        JLabel lastGeneratedLabel = new JLabel("");

        generateButton.addActionListener(e -> {
            model.setRowCount(0);
            for (Stock stock : stockManager.getAllStocks()) {
                if (stock.getQuantity() < 10) {
                    model.addRow(new Object[]{
                            stock.getName(),
                            stock.getQuantity(),
                            stock.getUnit(),
                            "KRİTİK SEVİYE"
                    });
                }
            }
            lastGeneratedLabel.setText("Son güncelleme: " +
                    LocalDateTime.now().format(dateFormatter));
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(generateButton, BorderLayout.WEST);
        bottomPanel.add(lastGeneratedLabel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfitReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Kâr/Zarar Raporu"));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ürün", "Satış Miktarı", "Birim", "Gelir", "Maliyet", "Kâr"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateButton = createStyledButton("Rapor Oluştur");
        JLabel lastGeneratedLabel = new JLabel("");

        generateButton.addActionListener(e -> {
            model.setRowCount(0);
            double totalProfit = 0;

            for (Stock stock : stockManager.getAllStocks()) {
                int totalSoldQuantity = 0;
                double totalRevenue = 0;
                double totalCost = 0;

                for (StockTransaction trans : stock.getTransactions()) {
                    if (trans.getType() == TransactionType.REMOVAL) {
                        totalSoldQuantity += trans.getQuantity();
                        totalRevenue += trans.getQuantity() * trans.getPrice();
                        totalCost += trans.getQuantity() * stock.getPrice();
                    }
                }

                if (totalSoldQuantity > 0) {
                    double profit = totalRevenue - totalCost;
                    totalProfit += profit;
                    model.addRow(new Object[]{
                            stock.getName(),
                            totalSoldQuantity,
                            stock.getUnit(),
                            priceFormat.format(totalRevenue),
                            priceFormat.format(totalCost),
                            priceFormat.format(profit)
                    });
                }
            }

            model.addRow(new Object[]{"TOPLAM", "", "", "", "", priceFormat.format(totalProfit)});
            lastGeneratedLabel.setText("Son güncelleme: " +
                    LocalDateTime.now().format(dateFormatter));
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(generateButton, BorderLayout.WEST);
        bottomPanel.add(lastGeneratedLabel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStyledPanel(JPanel content) {
        JPanel styledPanel = new GradientPanel();
        styledPanel.setLayout(new BorderLayout());
        styledPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        styledPanel.add(content, BorderLayout.CENTER);
        return styledPanel;
    }

    private void checkLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("123456")) {
            cardLayout.show(mainPanel, "main");
            usernameField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!");
        }
    }

    private void logout() {
        cardLayout.show(mainPanel, "login");
        usernameField.setText("");
        passwordField.setText("");
    }

    private void showPanel(JPanel contentPanel, String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StockManagementSystem system = new StockManagementSystem();
            system.setVisible(true);
        });
    }
}
