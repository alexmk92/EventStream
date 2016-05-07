package HelloWorld;

import org.junit.Assert;
import org.junit.Test;
import velostream.StreamAPI;

import static org.hamcrest.CoreMatchers.*;
import static velostream.util.StreamDefinitionBuilder.streamDefinition;
import static velostream.util.EventBuilder.eventBuilder;
import static velostream.StreamAPI.*;

public class HelloWorldStreamShould {

  String HELLO_WORLD = "Hello World";

  @Test
  public void shouldReturnHelloWorldEvent() throws Exception {
    newStream(streamDefinition(HELLO_WORLD).build());
    stream(HELLO_WORLD)
        .put(eventBuilder("MyHelloWorld").addFieldValue("message", "hello world").build(), false);
    stream(HELLO_WORLD).end(true);
    Assert.assertThat(stream(HELLO_WORLD).query().getLast().getFieldValue("message"),
        is("hello world"));
  }

}
