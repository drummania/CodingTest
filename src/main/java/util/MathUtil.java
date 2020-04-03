package util;

public class MathUtil {

    public static boolean isAggressive(final boolean isBuy, final double price1, final double price2) {
        return isBuy
                ? Double.compare(price1, price2) > 0
                : Double.compare(price2, price1) > 0;
    }

    public static boolean isAggressiveOrEqual(final boolean isBuy, final double price1, final double price2) {
        return isBuy
                ? Double.compare(price1, price2) >= 0
                : Double.compare(price2, price1) >= 0;
    }

    public static double getRandomIntegerBetweenRange(double min, double max) {
        double x = (int) (Math.random() * ((max - min) + 1)) + min;
        return x;
    }

    public static double getRandomDoubleBetweenRange(double min, double max) {
        double x = (Math.random() * ((max - min) + 1)) + min;
        return x;
    }

}
