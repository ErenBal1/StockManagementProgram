package stockManagementProgram.ui;

import stockManagementProgram.config.AppConfig;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.GradientPanel;
import stockManagementProgram.ui.panels.*;
import stockManagementProgram.ui.panels.reports.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final StockService stockService;
    private JPanel contentPanel;
    private static final Color BUTTON_COLOR = new Color(60, 141, 188);
    private static final Color HOVER_COLOR = new Color(45, 125, 170);

    public MainFrame(StockService stockService) {
        this.stockService = stockService;

        setupTheme();

        setTitle(AppConfig.APP_TITLE);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initializeComponents();
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

        // Global UI settings
        UIManager.put("Button.arc", 15);
        UIManager.put("Button.background", AppConfig.BUTTON_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
    }

    private void initializeComponents() {
        // Login panel
        LoginPanel loginPanel = new LoginPanel(new LoginPanel.LoginCallback() {
            @Override
            public void onLoginSuccess() {
                cardLayout.show(mainPanel, "main");
            }

            @Override
            public void onLoginFailed() {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Incorrect username or password!");
            }
        });

        // Main panel with menu
        JPanel mainMenuPanel = createMainMenuPanel();

        mainPanel.add(loginPanel, "login");
        mainPanel.add(mainMenuPanel, "main");

        add(mainPanel);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // Top menu
        JPanel topMenu = createTopMenu();

        // Content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        // Add all panels
        contentPanel.add(new StockAddPanel(stockService), "addStock");
        contentPanel.add(new StockRemovePanel(stockService), "removeStock");
        contentPanel.add(new StockViewPanel(stockService), "viewStock");
        contentPanel.add(new RecentTransactionsPanel(stockService), "recentTransactions");
        contentPanel.add(createReportsPanel(), "reports");

        panel.add(topMenu, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopMenu() {
        JPanel topMenu = new JPanel();
        topMenu.setOpaque(false);
        topMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addStockBtn = createMenuButton("Add Stock");
        JButton removeStockBtn = createMenuButton("Remove Stock");
        JButton viewStockBtn = createMenuButton("View Stock");
        JButton recentTransactionsBtn = createMenuButton("Recent Transactions");
        JButton reportBtn = createMenuButton("Reports");
        JButton logoutBtn = createMenuButton("Logout");

        topMenu.add(addStockBtn);
        topMenu.add(removeStockBtn);
        topMenu.add(viewStockBtn);
        topMenu.add(recentTransactionsBtn);
        topMenu.add(reportBtn);
        topMenu.add(logoutBtn);

        addStockBtn.addActionListener(e -> showPanel("addStock"));
        removeStockBtn.addActionListener(e -> showPanel("removeStock"));
        viewStockBtn.addActionListener(e -> showPanel("viewStock"));
        recentTransactionsBtn.addActionListener(e -> showPanel("recentTransactions"));
        reportBtn.addActionListener(e -> showPanel("reports"));
        logoutBtn.addActionListener(e -> logout());

        return topMenu;
    }

    private JPanel createReportsPanel() {
        JPanel reportsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        reportsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        reportsPanel.setOpaque(false);

        reportsPanel.add(new StockValueReportPanel(stockService));
        reportsPanel.add(new MonthlySalesReportPanel(stockService));
        reportsPanel.add(new LowStockReportPanel(stockService));
        reportsPanel.add(new ProfitReportPanel(stockService));

        return reportsPanel;
    }

    private JButton createMenuButton(String text) {
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

    private void showPanel(String name) {
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, name);
    }

    private void logout() {
        cardLayout.show(mainPanel, "login");
    }
}