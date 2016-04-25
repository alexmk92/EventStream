/*
 * EventImpl.java
 *
 * Created on 01 July 2007, 11:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package velostream.event;

import org.boon.json.annotations.JsonIgnore;
import velostream.interfaces.IEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract Event to be extended by user defined Events
 *
 * @author Richard Durley
 */
public abstract class Event implements IEvent {

  private long id;
  private long timestamp;

  public Event() {
    this.timestamp = System.currentTimeMillis();
    this.id = Counter.INSTANCE.getNext();
  }

  public Event(long eventid, long timestamp) {
    this.id = eventid;
    this.timestamp = timestamp;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
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
