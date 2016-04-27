package velostream.event;

import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

import java.util.Map;

/**
 * Created by Admin on 06/09/2014.
 */
public class SimpleFilterEventWorker implements IEventWorker {

  public enum Operator {
    CONTAINS("?"), GT(">"), LT("<"), EQ("=");

    private final String operator;

    Operator(String operator) {
      this.operator = operator;
    }
  }

  public IEvent work(IEvent eventIn, Map<String, Object> filterParams) {
    if (filterParams == null)
      return eventIn;
    else {
      String field = (String) filterParams.get("field");
      Object value = filterParams.get("value");
      String operator = (String) filterParams.get("operator");

      switch (Operator.valueOf(operator)) {
        case CONTAINS:
          return doContains(eventIn, field, value);
      }

    }

    return eventIn;
  }

  private IEvent doContains(IEvent event, String field, Object value) {
    if (event.getFieldValue(field).toString().contains(value.toString()))
      return event;
    else
      return EmptyEvent.INSTANCE;
  }
}
