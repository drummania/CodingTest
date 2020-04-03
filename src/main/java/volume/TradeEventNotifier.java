package volume;

public interface TradeEventNotifier {

    void onTradeEvent(final TradeEvent trade);
}
