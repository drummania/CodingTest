package util;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Formatter {

    public static String formatQuantity(final double decimal) {
        final String pattern = "###.##";
        final DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(decimal);
    }

    public static String formatPrice(final double price) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(price);
    }

    public static String formatInstant(final Instant instant) {
        return ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()).toString();
    }
}
