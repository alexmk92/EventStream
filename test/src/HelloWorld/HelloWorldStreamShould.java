package HelloWorld;

import org.junit.Assert;
import org.junit.Test;
import velostream.StreamAPI;
import static org.hamcrest.CoreMatchers.*;
import static velostream.util.StreamDefinitionBuilder.streamBuilder;
import static velostream.util.EventBuilder.eventBuilder;

public class HelloWorldStreamShould {

  String HELLO_WORD = "Hello World";

  @Test
  public void shouldReturnHelloWorldEvent() throws Exception {
    StreamAPI.newStream(streamBuilder(HELLO_WORD).build())
        .put(eventBuilder("MyHelloWorld").addFieldValue("message", "hello world").build(), false);
    StreamAPI.getStream(HELLO_WORD).end();
    while (!StreamAPI.getStream(HELLO_WORD).isEnd())
      Thread.currentThread().sleep(1);
    Assert.assertThat(
        StreamAPI.getStream(HELLO_WORD).getEventQueryStore().getQueryOperations().getLast()
            .getFieldValue("message"), is("hello world"));
  }

}
