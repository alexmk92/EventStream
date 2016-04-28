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

  private String eventName;
  private long eventID;
  private long timestamp;
  private Map<String, Object> eventFieldValues;

  public Event() {
    super();
  }

  public Event(String eventName) {
    this.eventName = eventName;
    this.timestamp = System.currentTimeMillis();
    this.eventID = Counter.INSTANCE.getNext();
    this.eventFieldValues = new HashMap<>();
  }

  public Event(long eventid, long timestamp) {
    this.eventID = eventid;
    this.timestamp = timestamp;
    this.eventFieldValues = new HashMap<>();
  }

  public Event(long eventid, long timestamp, Map<String, Object> eventKeyValueMap) {
    this.eventID = eventid;
    this.timestamp = timestamp;
    this.eventFieldValues = eventKeyValueMap;
  }

  public void setEventID(long eventID) {
    this.eventID = eventID;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void addFieldValue(String fieldname, Object value) {
    this.eventFieldValues.put(fieldname, value);
  }

  public Object getFieldValue(String name) {

    if (this.eventFieldValues != null) {
      return eventFieldValues.get(name);
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

  public long getTimestamp() {
    return this.timestamp;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public boolean isAlive(int ttl) {
    if (ttl > 0)
      return this.getTimestamp() + (ttl * 1000) > System.currentTimeMillis();
    else
      return true;
  }

  public long getEventID() {
    return this.eventID;
  }


  public Map<String, Object> getEventFieldValues() {
    return eventFieldValues;
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
    sb.append(", eventFieldValues=").append(eventFieldValues);
    sb.append('}');
    return sb.toString();
  }
}
