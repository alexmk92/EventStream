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
import java.util.Map;

/**
 * Abstract Event to be extended by user defined Events
 *
 * @author Richard Durley
 */
public class Event implements IEvent {

  private long eventID;
  private long timestamp;
  private Map<String, Object> eventKeyValueMAP;

  public Event() {
    this.timestamp = System.currentTimeMillis();
    this.eventID = Counter.INSTANCE.getNext();
  }

  public Event(long eventid, long timestamp) {
    this.eventID = eventid;
    this.timestamp = timestamp;
  }

  public Event(Map<String, Object> eventKeyValueMAP) {
    this.timestamp = System.currentTimeMillis();
    this.eventID = Counter.INSTANCE.getNext();
    this.eventKeyValueMAP = eventKeyValueMAP;
  }

  public Event(long eventid, long timestamp, Map<String, Object> eventKeyValueMap) {
    this.eventID = eventid;
    this.timestamp = timestamp;
    this.eventKeyValueMAP = eventKeyValueMap;
  }

  public void setEventID(long eventID) {
    this.eventID = eventID;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public Object getFieldValue(String name) {
    if (this.eventKeyValueMAP != null) {
      return eventKeyValueMAP.get(name);
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

  @Override
  public boolean isAlive(int ttl) {
    if (ttl > 0)
      return this.getTimestamp() + (ttl * 1000) > System.currentTimeMillis();
    else
      return true;
  }

  public long getEventID() {
    return this.eventID;
  }

  /**
   * Override to return customer user defined ID for sorting results
   */
  @Override
  public String getUserDefinedId() {
    return Long.toString(this.eventID);
  }


  @Override
  public boolean equals(Object event) {
    if (this == event)
      return true;
    else
      return this.getEventID() == (((IEvent) event).getEventID());
  }


}
