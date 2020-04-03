package order;

import util.Formatter;

import java.time.Instant;

public class Order {

    private final Instant startTime;
    private final Instant endTime;
    private final double quantity;
    private final double limit;
    private final boolean isBuy;

    public Order(final Instant startTime, final Instant endTime, final double quantity, final double limit, final boolean isBuy) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.limit = limit;
        this.isBuy = isBuy;

    }

    public double getQuantity() {
        return quantity;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public double getLimit() {
        return limit;
    }

    public double getTickSize() {
        return 0.1;
    }  // hardcode

    public double getLotSize() {
        return 100.0;
    } // hardcode

    public boolean isBuy() {
        return isBuy;
    }

    public double roundDownToLotSize(double decimal) {
        return decimal - decimal % getLotSize();
    }

    public void printInfo() {

        System.out.println("=================");
        System.out.println("Order Information");
        System.out.println("=================");
        System.out.println("Start time:" + Formatter.formatInstant(getStartTime()));
        System.out.println("End time:" + Formatter.formatInstant(getEndTime()));
        System.out.println("Limit price:" + getLimit());
        System.out.println("Quantity" + getQuantity());
        System.out.println("Is Buy? " + isBuy());
    }
}
