package model;

import market.OrderBookQuery;
import order.Order;
import order.Parameter;
import slice.Slice;
import slice.SliceManager;
import util.Formatter;
import util.Logger;
import util.TimerService;
import volume.VolumeProfile;
import volume.VolumeTracker;

import java.time.Duration;
import java.time.Instant;
import java.util.TimerTask;

/**
 *  The implementation of VWAP algorithm
 *  It supports:
 *  (1) divide trade horizon into predefined length interval
 *      and target to fill target quantity indicated on the volume profile
 *  (2) send passive slice in the beginning of each interval
 *  (3) Passive slice peg to passive touch
 *  (4) Cross passive slice when crossing time is reached
 *  (5) VWAP logic run between start time and end time only
 *  (6) Support three target curve ( low, desire, upper) use by different urgent setting
 *
 *  Please run VwapTest.testVwap() for quick simulation
 */
public class Vwap extends TimerTask {

    public static final Duration INTERVAL_LENGTH = Duration.ofSeconds(3600);
    public static final Duration LOWER_CURVE_DELAY = Duration.ofSeconds(600);
    public static final Duration UPPER_CURVE_AHEAD = Duration.ofSeconds(400);

    private final Order order;
    private final VolumeProfile volumeProfile;
    private final TimerService timerService;
    private final VolumeTracker volumeTracker;
    private final SliceManager sliceManager;
    private final OrderBookQuery orderBook;
    private final Parameter parameter;
    private final Logger logger;
    private Interval interval;

    public Vwap(final VolumeProfile volumeProfile,
                final Order order,
                final TimerService timerService,
                final VolumeTracker volumeTracker,
                final SliceManager sliceManager,
                final OrderBookQuery orderBook,
                final Parameter parameter,
                final Logger logger) {

        this.volumeProfile = volumeProfile;
        this.order = order;
        this.timerService = timerService;
        this.volumeTracker = volumeTracker;
        this.sliceManager = sliceManager;
        this.orderBook = orderBook;
        this.parameter = parameter;
        this.logger = logger;
    }

    private boolean isActive() {

        Instant now = timerService.now();
        return !now.isBefore(order.getStartTime()) && !now.isAfter(order.getEndTime());
    }

    class Interval {

        private final Instant dueTime;

        public Interval(final Instant dueTime) {
            this.dueTime = dueTime;
        }

        public double getLowerTargetQuantity() {
            return volumeProfile.getVolume(dueTime.minus(LOWER_CURVE_DELAY)) * order.getQuantity();
        }

        public double getDesireTargetQuantity() {
            return volumeProfile.getVolume(dueTime) * order.getQuantity();
        }

        public double getUpperTargetQuantity() {
            return volumeProfile.getVolume(dueTime.plus(UPPER_CURVE_AHEAD)) * order.getQuantity();
        }

        public Instant getDueTime() {
            return dueTime;
        }

        public void printInfo() {
            logger.debug("Interval Targets :[ "
                    + "low:" + Formatter.formatQuantity(getLowerTargetQuantity()) + " , "
                    + "desire" + Formatter.formatQuantity(getDesireTargetQuantity()) + " , "
                    + "up:" + Formatter.formatQuantity(getUpperTargetQuantity()) + " ] "
                    + "Interval due time : " + Formatter.formatInstant(getDueTime()));
        }
    }

    @Override
    public void run() {

        if (!isActive()) {
            return;
        }

        if (isOrderFullyFilled()) {
            return;
        }

        Instant now = timerService.now();

        // Divide order Horizon into interval
        // Aim for reduce market impact
        if (interval == null || now.isAfter(interval.getDueTime())) {
            interval = new Interval(now.plus(getIntervalLength()));
            logger.debug("Create a new interval");
            interval.printInfo();
        }

        final double filledQty = sliceManager.getFilledQty();
        final double openQty = sliceManager.getOpenQty();
        final double target = getTargetQuantity(interval);
        final double behindQty = target - filledQty - openQty;

        // LAYERING : put quantity in passive touch price
        // Aim for getting Price improvement
        if (Double.compare(behindQty, order.getLotSize()) > 0) {

            final double sliceQty = order.roundDownToLotSize(behindQty);
            final double slicePrice = getPassivePrice();
            sliceManager.entry(sliceQty, slicePrice, "Slice");
            printInfo();
            return;
        }

        // PEG to passive touch price
        // Aim to get price improvement
        for (Slice slice : sliceManager.getOpenSlice()) {
            final double passiveTouch = getPassivePrice();
            if (isAggressive(order.isBuy(), slice.getSlicePrice(), passiveTouch)) {
                sliceManager.amend(slice, passiveTouch);
                logger.debug(slice.getSliceRef() + " is pegged to " + passiveTouch);
            }
        }

        // Crossing passive slice
        // Aim to get Done
        boolean crossingTimeReached = now.isAfter(interval.dueTime.minus(getCrossingStartOffset()));
        if (crossingTimeReached){
            for (Slice slice : sliceManager.getOpenSlice()) {
                final double farTouch = getFarTouch();
                //todo: existing logic is one step crossing, enhance to multiple steps crossing to get price improvement
                sliceManager.amend(slice, farTouch);
                logger.debug(slice.getSliceRef() + " is amended to cross the spread");
            }
        }

        //todo: add IWould feature , aim for getting price improvment
        //todo: add end game logic, to reserve certain quantity close to the end time
    }

    private boolean isOrderFullyFilled() {
        return Double.compare(sliceManager.getFilledQty(), order.getQuantity()) == 0;
    }

    private Duration getIntervalLength() {
        return INTERVAL_LENGTH;
    }

    private boolean isAggressive(final boolean isBuy, final double price1, final double price2) {
        return isBuy
                ? Double.compare(price1, price2) > 0
                : Double.compare(price2, price1) > 0;
    }

    private void printInfo() {
        sliceManager.printInfo();
        logger.debug("Order book status "
                + "- Bid: " + Formatter.formatPrice(orderBook.getBid())
                + ", Ask: " + Formatter.formatPrice(orderBook.getAsk()));
    }

    // return offset used to determine the start of crossing time in each interval
    private Duration getCrossingStartOffset() {

        switch (parameter.getUrgency()) {

            case LOW:
                return INTERVAL_LENGTH.dividedBy(6);
            case MEDIUM:
                return INTERVAL_LENGTH.dividedBy(3);
            case HIGH:
            default:
                return INTERVAL_LENGTH.dividedBy(2);
        }
    }

    // Return target quantity based on Urgency and Get Done parameter
    public double getTargetQuantity(Interval interval) {

        switch (parameter.getUrgency()) {

            case LOW:
                return interval.getUpperTargetQuantity();
            case MEDIUM:
                return interval.getDesireTargetQuantity();
            case HIGH:
            default:
                return  parameter.getDone() ? interval.getUpperTargetQuantity() : interval.getLowerTargetQuantity();
        }
    }

    public double getPassivePrice() {
        return order.isBuy() ? orderBook.getBid() : orderBook.getAsk();
    }

    public double getFarTouch() {
        return order.isBuy() ? orderBook.getAsk() : orderBook.getBid();
    }
}


