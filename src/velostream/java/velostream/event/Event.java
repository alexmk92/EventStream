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

/**
 * Abstract Event to be extended by user defined Events
 *
 * @author Richard Durley
 */
public abstract class Event implements IEvent {

    private long $id;
    private long $timestamp;

    public Event(long eventid, long timestamp) {
        this.$id = eventid;
        this.$timestamp = timestamp;
    }

    @Override
    public long getTimestamp() {
        return this.$timestamp;
    }

    @Override
    public boolean isAlive(int ttl) {
        return this.getTimestamp() + (ttl * 1000) > System.currentTimeMillis();
    }

    @Override
    public long getId() {
        return this.$id;
    }

    @Override
    /**
     * Override to return customer user defined ID for sorting results
     */
    public String getUserDefinedId() {
        return Long.toString(this.$id);
    }

    @Override
    public boolean equals(Object event) {
        if (this == event) return true;
        else
            return this.getId() == (((IEvent) event).getId());
    }


}
