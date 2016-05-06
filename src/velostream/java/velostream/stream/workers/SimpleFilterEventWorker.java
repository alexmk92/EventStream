package velostream.stream.workers;

import velostream.event.EmptyEvent;
import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static velostream.event.EmptyEvent.EmptyEvent;

/**
 * Created by Admin on 06/09/2014.
 */
public class SimpleFilterEventWorker implements IEventWorker {


  private final List<Character> operator = Arrays.asList('?', '>', '<', '=');

  public List<IEvent> work(IEvent eventIn, Map<String, Object> filterParams) {
    if (filterParams == null)
      return Arrays.asList(eventIn);
    else {
      String field = (String) filterParams.get("field");
      Object value = filterParams.get("value");
      String operator = (String) filterParams.get("operator");

      switch (operator.charAt(0)) {
        case '?':
          return Arrays.asList(doContains(eventIn, field, value));
      }

    }

    return Arrays.asList(eventIn);
  }

  private IEvent doContains(IEvent event, String field, Object value) {
    if (event.getFieldValue(field)!=null && event.getFieldValue(field).toString().contains(value.toString()))
      return event;
    else
      return EmptyEvent;
  }
}
