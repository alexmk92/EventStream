

import velostream.event.Event;
import velostream.util.ID;

/**
 * Created by Admin on 20/09/2014.
 */
public class QuoteEvent extends Event {

    public String symbol;
    public double quote;

    public QuoteEvent(String symbol, double quote)
    {
        super(ID.next(),System.currentTimeMillis());
        this.symbol=symbol;
        this.quote=quote;
    }

}
