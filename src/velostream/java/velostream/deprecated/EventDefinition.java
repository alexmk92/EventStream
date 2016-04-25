package velostream.deprecated;

import velostream.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Event Definition that is used to define the meta-data of an Event
 *
 * @author Richard Durley
 *         Date: 10/12/13
 *         Time: 21:00
 */
public class EventDefinition {

  private final String name;
  Map<String, Class> event_fields;
  Map<String, Object> event_values;

  public EventDefinition(String name, Map<String, Class> fields) {
    if (fields == null)
      throw new IllegalArgumentException("fields are mandatory");

    this.name = name;
    this.event_fields = fields;
  }


  public String getName() {
    return this.name;
  }

  public Map<String, Class> getFields() {
    return this.event_fields;
  }

  @Override
  public String toString() {
    return name;
  }

}
