package velostream.event;

public class EventBuilder {

  Event event;
  EventBuilder builder;

  public EventBuilder(String eventName) {
    this.event = new Event(eventName);
  }

  public static EventBuilder builder(String eventName) {
    return new EventBuilder(eventName);
  }

  public EventBuilder addFieldValue(String fieldname, Object value) {
    this.event.addFieldValue(fieldname, value);
    return this;
  }

  public EventBuilder setTimestamp(Long timestamp) {
    this.event.setTimestamp(timestamp);
    return this;
  }

  public EventBuilder setId(Long id) {
    this.event.setEventID(id);
    return this;
  }

  public Event build() {
    return this.event;
  }

}
