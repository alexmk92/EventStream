import events.QuoteEvent;
import org.junit.Test;
import org.junit.Assert;
import velostream.infrastructure.Stream;
import velostream.StreamAPI;
import velostream.exceptions.StreamNotFoundException;
import velostream.interfaces.IEventWorker;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 07/09/2014.
 */


public class TestBenchmarkQuoteStream {


  @Test
  public void test() {
    long benchmarkeventspersecond = 0;
    double avg = 200d;
    int count = 0;
    int numrecs = 2000000;
    Random r = new Random();
    try {

      while (count < 10) {
        count++;
        Stream quotestream = StreamAPI
            .newStream("IBM_AVG", new IEventWorker[] {new QuoteToAverageQuoteWorker("IBM")},
                StreamAPI.WORKER_RESULTS_UNORDERED, 0);
        double lastquoteprice = 200d;
        long starttime = System.currentTimeMillis();
        for (int i = 1; i < numrecs; i++) {
          QuoteEvent quote = new QuoteEvent("IBM", lastquoteprice);
          avg = (double) ((lastquoteprice + (avg * (i - 1))) / (double) (i));
          quotestream.put(quote, true);
          if (r.nextBoolean())
            lastquoteprice = lastquoteprice + 0.1;
          else
            lastquoteprice = lastquoteprice - 0.1;

        }
        quotestream.end();
        while (!quotestream.isEnd() || !quotestream.getEventEventWorkerExecution().isEnd())
          TimeUnit.MILLISECONDS.sleep(1);
        double totest =
            ((AverageQuoteEvent) StreamAPI.getStream("IBM_AVG").getEventEventWorkerExecution()
                .getWorkerResultQueryOperations().getLast()).avg_quote;
        benchmarkeventspersecond = (benchmarkeventspersecond == 0) ?
            ((numrecs / (System.currentTimeMillis() - starttime)) * 1000) :
            ((((numrecs / (System.currentTimeMillis() - starttime)) * 1000)
                + (benchmarkeventspersecond)) / 2);

        Assert.assertTrue(totest == avg);

      }
      System.out.println("average benchmark per second :" + benchmarkeventspersecond);
    } catch (InterruptedException e) {

    } catch (StreamNotFoundException e2) {

    }

  }

}
