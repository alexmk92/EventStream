/*
 * EventImpl.java
 *
 * Created on 01 July 2007, 11:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package velostream.event;

import velostream.interfaces.IEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Event to be extended by user defined Events
 *
 * @author Richard Durley
 */
public class Event implements IEvent {

  private String eventName = "";
  private long eventID = -1;
  private long timestamp = -1;
  private Map<String, Object> eventValues = null;
  private static final Map<String, Object> EMPTY_FIELD_VALUES = new HashMap<>();


  public Event() {
    super();
    this.timestamp = System.currentTimeMillis();
    this.eventID = Counter.INSTANCE.getNext();
    this.eventValues = EMPTY_FIELD_VALUES;
  }

  public Event(String eventName) {
    this.eventName = eventName;
    this.timestamp = System.currentTimeMillis();
    this.eventID = Counter.INSTANCE.getNext();
    this.eventValues = EMPTY_FIELD_VALUES;
  }

  public Event(long eventid, long timestamp) {
    this.eventID = eventid;
    this.timestamp = timestamp;
    this.eventValues = EMPTY_FIELD_VALUES;
  }

  public Event(long eventid, long timestamp, Map<String, Object> eventValues) {
    this.eventID = eventid;
    this.timestamp = timestamp;
    this.eventValues = eventValues;
  }

  @Override
  public Object getFieldValue(String name) {

    if (this.eventValues != null) {
      return eventValues.get(name);
    } else
      return getFieldValueViaGetter(name);
  }

  private Object getFieldValueViaGetter(String fieldname) {
    Method f = null;
    Object toreturn = null;
    try {
      f = this.getClass().getMethod("get" + fieldname, null);
      toreturn = f.invoke(this, null);
    } catch (NoSuchMethodException e2) {
    } catch (IllegalAccessException e3) {
    } catch (InvocationTargetException e4) {
    }

    return toreturn;
  }

  @Override
  public long getTimestamp() {
    return this.timestamp;
  }

  @Override
  public boolean isAlive(int ttl) {
    if (ttl > 0)
      return this.getTimestamp() + (ttl * 1000) > System.currentTimeMillis();
    else
      return true;
  }

  @Override
  public boolean hasFieldValue(String fieldname) {
    if (this.eventValues != null && this.eventValues.containsKey(fieldname))
      return true;
    else if (this.getFieldValueViaGetter(fieldname) != null)
      return true;
    else
      return false;
  }

  @Override
  public long getEventID() {
    return this.eventID;
  }

  public void setEventValues(Map<String, Object> eventValues) {
    this.eventValues = eventValues;
  }

  public void setEventID(long eventID) {
    this.eventID = eventID;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void addFieldValue(String fieldname, Object value) {
    if (eventValues.equals(EMPTY_FIELD_VALUES))
      this.eventValues = new HashMap<>();
    this.eventValues.put(fieldname, value);
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public Map<String, Object> getEventValues() {
    return eventValues;
  }

  @Override
  public boolean equals(Object event) {
    if (this == event)
      return true;
    else
      return this.getEventID() == (((IEvent) event).getEventID());
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Event{");
    sb.append("eventName='").append(eventName).append('\'');
    sb.append(", eventID=").append(eventID);
    sb.append(", timestamp=").append(timestamp);
    sb.append(", eventValues=").append(eventValues);
    sb.append('}');
    return sb.toString();
  }
}
