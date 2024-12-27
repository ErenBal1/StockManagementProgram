package stockManagementProgram.util;

import java.text.DecimalFormat;

public class PriceFormatter {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00 TL");

    public static String format(double price) {
        return PRICE_FORMAT.format(price);
    }
}