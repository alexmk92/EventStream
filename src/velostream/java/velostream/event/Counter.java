package velostream.event;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {

  public static Counter INSTANCE = new Counter();

  private AtomicLong counter = new AtomicLong(0);

  public long getNext() {
    return counter.incrementAndGet();
  }
}
