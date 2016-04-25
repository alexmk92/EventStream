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

  private long id;
  private long timestamp;
  private Map<String, Object> eventValues;

  public Map<String, Object> getEventValues() {
    return eventValues;
  }

  public void setEventValues(Map<String, Object> eventValues) {
    this.eventValues = eventValues;
  }

  public Event() {
    this.timestamp = System.currentTimeMillis();
    this.id = Counter.INSTANCE.getNext();
  }

  public Event(long eventid, long timestamp) {
    this.id = eventid;
    this.timestamp = timestamp;
  }

  public Event(Map<String, Object> eventValues) {
    this.timestamp = System.currentTimeMillis();
    this.id = Counter.INSTANCE.getNext();
    this.eventValues = eventValues;
  }

  public Event(long eventid, long timestamp, Map<String, Object> event_values) {
    this.id = eventid;
    this.timestamp = timestamp;
    this.eventValues = event_values;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

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
  public long getId() {
    return this.id;
  }

  /**
   * Override to return customer user defined ID for sorting results
   */
  @Override
  public String getUserDefinedId() {
    return Long.toString(this.id);
  }


  @Override
  public boolean equals(Object event) {
    if (this == event)
      return true;
    else
      return this.getId() == (((IEvent) event).getId());
  }


}
