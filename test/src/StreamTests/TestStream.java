package StreamTests;

import org.junit.Before;
import org.junit.Test;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.stream.Stream;
import velostream.util.EventBuilder;
import velostream.util.StreamDefinitionBuilder;

public class TestStream {

  Stream quotestream;

  @Before
  public void doSetup() throws Exception {
    Event event;
    quotestream =
        StreamAPI.newStream(StreamDefinitionBuilder.builder("quote").setEventTTL(0).build());

    event =
        EventBuilder.builder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 20.0D)
            .build();
    quotestream.put(event, false);

    event =
        EventBuilder.builder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 22.0D)
            .build();

    quotestream.put(event, false);

    event =
        EventBuilder.builder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 24.0D)
            .build();

    quotestream.put(event, false);

    Thread.currentThread().sleep(1000);

  }

  @Test
  public void testGetGroupByField() throws Exception {
    StreamAPI.getStream("quote").getEventQueryStore().getQueryOperations().getEachLastBy("symbol");

  }
}
