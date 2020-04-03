package model;

import market.OrderBookQuery;
import order.Order;
import order.Parameter;
import order.Urgency;
import org.junit.Test;
import slice.Slice;
import slice.SliceManager;
import util.Formatter;
import util.Logger;
import util.MathUtil;
import util.TimerService;
import volume.LinearProfile;
import volume.TradeEvent;
import volume.VolumeProfile;
import volume.VolumeTracker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VwapTest {

    public static final int SIMULATION_INTERVAL_SEC = 1;
    private Instant mockTime = Instant.EPOCH;
    private double mockBid = 10.0;
    private double mockAsk = 10.1;

    // fake/mock dependency
    private Order order;
    private VolumeProfile volumeProfile;
    private VolumeTracker volumeTracker;
    private OrderBookQuery orderBook;
    private TimerService timerMock;
    private Parameter parameter;
    private Logger logger;
    private SliceManager sliceManager;

    boolean isTriggeredMarketMove = false;

    @Test
    public void testVwap() {

        // precondition
        final Instant orderStartTime = LocalDate.now().atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant();
        final Instant orderEndTime = LocalDate.now().atTime(16, 0).atZone(ZoneId.systemDefault()).toInstant();

        order = new Order(orderStartTime, orderEndTime, 100000.0, 10.0, true);
        volumeProfile = new LinearProfile(orderStartTime, orderEndTime);
        volumeTracker = new VolumeTracker();
        orderBook = new OrderBookQuery() {

            @Override
            public double getBid() {
                return mockBid;
            }

            @Override
            public double getAsk() {
                return mockAsk;
            }
        };

        timerMock = new TimerService() {
            @Override
            public Instant now() {
                return mockTime;
            }
        };

        parameter = new Parameter() {

            @Override
            public boolean getDone() {
                return true;
            }

            @Override
            public Urgency getUrgency() {
                return Urgency.HIGH;
            }

            @Override
            public double getMaxPct() {
                return 100.0;
            }
        };

        logger = new Logger(timerMock);
        sliceManager = new SliceManager(logger);

        // construct vwap
        final Vwap vwap = new Vwap(volumeProfile, order, timerMock, volumeTracker
                , sliceManager, orderBook, parameter, logger);

        final Instant simulationStartTime = orderStartTime.minusSeconds(60);
        final Instant simulationEndTime = orderEndTime.plusSeconds(60);
        mockTime = simulationStartTime;

        System.out.println("====================================================================");
        System.out.println("Start Simulation");
        System.out.println("Simulate start from: " + Formatter.formatInstant(simulationStartTime));
        System.out.println("Simulate end at: " + Formatter.formatInstant(simulationEndTime));
        order.printInfo();
        System.out.println("====================================================================");


        while (!mockTime.isAfter(simulationEndTime)) {

            // Precondition
            updateOrderBook(orderStartTime);

            // Run core logic
            vwap.run();

            // Simulate Market activities
            fillCrossingSlice();

            // move to next evaluation
            mockTime = mockTime.plusSeconds(SIMULATION_INTERVAL_SEC);
        }

        sliceManager.printInfo();
        final double avgFilledPrice = sliceManager.getAvgFilledPrice();
        final double benchmarkPrice = volumeTracker.getBenchmarkPrice();
        final double slippage = order.isBuy() ? benchmarkPrice - avgFilledPrice :
                avgFilledPrice - benchmarkPrice;


        System.out.println("====================================================================");
        //todo: simulate market trade in order to generate a reasonable analysis
        logger.debug("Avg filled price : " + avgFilledPrice);
        logger.debug("Benchmark price (VSOT) = " + benchmarkPrice);
        logger.debug("Slippage = " + (avgFilledPrice - benchmarkPrice));
        logger.debug("TODO: need to simulate market trade in order to generate a reasonable analysis");
        System.out.println("====================================================================");

        // Assertions
        assertEquals(sliceManager.getFilledQty(), order.getQuantity(), 0.00001);
        assertTrue(slippage < 0.5);
    }

    private void updateOrderBook( final Instant refTime) {

        // simulate move market at 6000s after order start time
        if (mockTime.isAfter(refTime.plusSeconds(1000)) && !isTriggeredMarketMove) {
            mockBid = 9.9;
            mockAsk = 10.0;
            isTriggeredMarketMove = true;
        }

        //todo: add more movement on order book
    }

    private void fillCrossingSlice() {
        // fill open slice straight away
        for (Slice slice : sliceManager.getOpenSlice()) {

            if (MathUtil.isAggressiveOrEqual(order.isBuy(), slice.getSlicePrice(), getFarTouch())) {

                sliceManager.fill(slice, slice.getOpenQuantity(), slice.getSlicePrice());
                logger.debug(slice.getSliceRef() + " is filled");

                // create trade event
                volumeTracker.onTradeEvent(new TradeEvent() {
                    @Override
                    public double getTradePrice() {
                        return slice.getSlicePrice();
                    }

                    @Override
                    public double getTradeSize() {
                        return slice.getOpenQuantity();
                    }
                });
            }
        }
    }

    private double getFarTouch() {
        return order.isBuy() ? orderBook.getAsk() : orderBook.getBid();
    }
}