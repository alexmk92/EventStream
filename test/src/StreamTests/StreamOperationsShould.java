package StreamTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import velostream.event.Event;
import velostream.exceptions.StreamNotFoundException;
import velostream.interfaces.IEvent;
import velostream.stream.Stream;
import velostream.util.StreamDefinitionBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static velostream.StreamAPI.newStream;
import static velostream.StreamAPI.stream;
import static velostream.event.EmptyEvent.EMPTY_EVENT;
import static velostream.util.EventBuilder.eventBuilder;

public class StreamOperationsShould {

  Stream quotestream;
  Event max_event;
  Event min_event;
  Event last_event;

  @Before
  public void doSetup() throws Exception {
    Event event;
    quotestream =
        newStream(StreamDefinitionBuilder.streamDefinition("quote").setEventTTL(0).build());

    event =
        eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 20.0D).build();
    min_event = event;
    quotestream.put(event, false);

    event =
        eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 22.0D).build();

    quotestream.put(event, false);

    event =
        eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 24.0D).build();
    max_event = event;
    quotestream.put(event, false);

    event =
        eventBuilder("quote").addFieldValue("symbol", "JRD").addFieldValue("price", 23.0D).build();
    last_event = event;
    quotestream.put(event, false);

    Thread.currentThread().sleep(1000);

  }

  @Test
  public void testGetGroupByField() throws Exception {
    Set<Map.Entry<Object, Optional<IEvent>>> e =
        stream("quote").query().getLastBy("symbol");
    Assert.assertThat(e.iterator().next().getValue().get(), is(last_event));
  }

  @Test
  public void testMax() throws Exception {
    IEvent event = stream("quote").query().getMax("price");
    Assert.assertThat(event.getFieldValue("price"), is(24.0D));
  }

  @Test
  public void testMin() throws Exception {
    IEvent event = stream("quote").query().getMin("price");
    Assert.assertThat(event.getFieldValue("price"), is(20.0D));
  }

  @Test
  public void testCount() throws Exception {
    long count = stream("quote").query().getCount();
    Assert.assertThat(count, is(4L));
  }

  @Test
  public void testSum() throws Exception {
    double sum = stream("quote").query().getSum("price");
    Assert.assertThat(sum, is(89.0D));
  }


  @Test
  public void testEmptyStream() throws StreamNotFoundException, InterruptedException {
    Stream emptystream = newStream(StreamDefinitionBuilder.streamDefinition("empty").setEventTTL(1).build());
    emptystream.put(eventBuilder("test").addFieldValue("test", true).build(), false);
    Thread.currentThread().sleep(1000);
    double sum = stream("empty").query().getSum("price");
    long count = stream("empty").query().getCount();
    IEvent event_min = stream("empty").query().getMin("price");
    IEvent event_max = stream("empty").query().getMax("price");
    Set<Map.Entry<Object, Optional<IEvent>>> map =
        stream("empty").query().getLastBy("symbol");
    Assert.assertThat(sum, is(0.0D));
    Assert.assertThat(count, is(0L));
    Assert.assertThat(event_min, is(EMPTY_EVENT));
    Assert.assertThat(event_max, is(EMPTY_EVENT));
    Assert.assertThat(map.size(), is(0));
  }


}
