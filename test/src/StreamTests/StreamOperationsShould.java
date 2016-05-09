package StreamTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import velostream.StreamAPI;
import velostream.event.Event;
import velostream.interfaces.IEvent;
import velostream.stream.Stream;
import velostream.util.EventBuilder;
import velostream.util.StreamDefinitionBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StreamOperationsShould {

  Stream quotestream;
  Event max_event;
  Event min_event;
  Event last_event;

  @Before
  public void doSetup() throws Exception {
    Event event;
    quotestream =
        StreamAPI.newStream(StreamDefinitionBuilder.streamDefinition("quote").setEventTTL(0).build());

    event =
        EventBuilder.eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 20.0D)
            .build();
    min_event=event;
    quotestream.put(event, false);

    event =
        EventBuilder.eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 22.0D)
            .build();

    quotestream.put(event, false);

    event =
        EventBuilder.eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 24.0D)
            .build();
    max_event=event;
    quotestream.put(event, false);

    event =
        EventBuilder.eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 23.0D)
            .build();
    last_event=event;
    quotestream.put(event, false);

    Thread.currentThread().sleep(1000);

  }

  @Test
  public void testGetGroupByField() throws Exception {
    Set<Map.Entry<Object, Optional<IEvent>>>  e = StreamAPI.stream("quote").query().getLastBy("symbol");
    Assert.assertThat(e.iterator().next().getValue().get(), is(last_event));
  }

  @Test
  public void testMax() throws Exception {
    IEvent event = StreamAPI.stream("quote").query().getMax("price");
    Assert.assertThat(event.getFieldValue("price"), is(24.0D));
  }

  @Test
  public void testMin() throws Exception {
    IEvent event = StreamAPI.stream("quote").query().getMin("price");
    Assert.assertThat(event.getFieldValue("price"), is(20.0D));
  }

  @Test
  public void testCount() throws Exception {
    long count = StreamAPI.stream("quote").query().getCount();
    Assert.assertThat(count, is(4L));
  }

  @Test
  public void testSum() throws Exception {
    double sum = StreamAPI.stream("quote").query().getSum("price");
    Assert.assertThat(sum, is(89.0D));
  }




}
