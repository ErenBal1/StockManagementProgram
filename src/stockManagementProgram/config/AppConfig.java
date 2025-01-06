package stockManagementProgram.config;

import java.awt.*;

/**
 * Configuration class that holds system-wide constants and settings.
 * Contains critical stock levels, UI colors, and default credentials.
 */
public class AppConfig {
    // Critical stock threshold for inventory alerts
    public static final int CRITICAL_STOCK_LEVEL = 10;
    public static final String APP_TITLE = "Stock Management System";

    // Default login credentials loaded from database
    public static String DEFAULT_USERNAME = "";
    public static String DEFAULT_PASSWORD = "";

    // UI Theme Colors
    public static final Color THEME_COLOR = new Color(60, 141, 188);
    public static final Color BUTTON_COLOR = new Color(60, 141, 188);
    public static final Color HOVER_COLOR = new Color(45, 125, 170);
}