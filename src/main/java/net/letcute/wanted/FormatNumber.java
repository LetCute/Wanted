package net.letcute.wanted;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatNumber {
    
    public static String formatWithCommas(int number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        return formatter.format(number);
    }
}
