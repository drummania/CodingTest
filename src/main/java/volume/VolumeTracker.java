package volume;

public class VolumeTracker implements BenchmarkPrice, TradeEventNotifier {

    private double intervalVolume;
    private double benchmarkPrice;

    public VolumeTracker() {}

    // return VSOT price for benchmark use
    @Override
    public double getBenchmarkPrice() {
        return benchmarkPrice;
    }

    @Override
    public void onTradeEvent(TradeEvent trade) {

        // update benchmark price
        benchmarkPrice = ( benchmarkPrice * intervalVolume + trade.getTradePrice() * trade.getTradeSize() )  /
                (intervalVolume + trade.getTradeSize());

        intervalVolume += trade.getTradeSize();
    }
}
