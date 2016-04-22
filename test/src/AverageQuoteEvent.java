

import velostream.event.Event;

/**
 * Created by Admin on 20/09/2014.
 */
public class AverageQuoteEvent extends Event {

    public double getAvg_quote() {
        return avg_quote;
    }

    public double avg_quote;

    public String getSymbol() {
        return symbol;
    }

    public String symbol;

    public AverageQuoteEvent(String symbol, double avg_quote)
    {
        super();
        this.avg_quote=avg_quote;
        this.symbol=symbol;
    }
}
