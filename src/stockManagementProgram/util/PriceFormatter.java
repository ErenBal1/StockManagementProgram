package stockManagementProgram.util;

import java.text.DecimalFormat;

/**
 * Utility class for formatting price values in the stock management system.
 */
public class PriceFormatter {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00 TL");

    public static String format(double price) {
        return PRICE_FORMAT.format(price);
    }
}