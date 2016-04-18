
import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Admin on 20/09/2014.
 */
public class QuoteToAverageQuoteWorker implements IEventWorker {

    volatile AverageQuoteEvent lastaveragequote =null;
    volatile int no_quotes = 0;

    String symbol;
    private final ReentrantLock lock = new ReentrantLock();

    public QuoteToAverageQuoteWorker(String symbol)
    {
        this.symbol=symbol;
    }

    @Override
    public IEvent work(IEvent toprocess) {
            if (toprocess instanceof QuoteEvent) {
                QuoteEvent q = (QuoteEvent) toprocess;
                if (q.symbol.equals(symbol)) {
                    lock.lock();
                    no_quotes++;
                    if (lastaveragequote !=null) {
                        lastaveragequote= new AverageQuoteEvent(symbol,(double) ( ((QuoteEvent) toprocess).quote + (lastaveragequote.avg_quote*(no_quotes-1))) / (no_quotes));
                    }
                    else {
                        lastaveragequote = new AverageQuoteEvent(symbol, (((QuoteEvent) toprocess).quote));
                    }
                    lock.unlock();
                }
            }
            return lastaveragequote;
    }

}
