package events;

import velostream.event.Event;

/**
 * Created by Admin on 20/09/2014.
 */
public class QuoteEvent extends Event {

  public String symbol;
  public double quote;

  public QuoteEvent() {
    super();
  }

  public QuoteEvent(String symbol, double quote) {
    super();
    this.symbol = symbol;
    this.quote = quote;
  }

}
