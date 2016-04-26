/*
 * Event.java
 * Created on 14 November 2006, 17:38
 * Author Richard Durley
 */
package velostream.interfaces;

/**
 * Defines the interface that all events implement
 * @author Richard Durley
 */
public interface IEvent<T> {

    /**
     * Returns an events id
     * @return
     */
    public long getEventID();

     /**
     * Returns the velostream.event timestamp
     * @return timestamp
     */
    public long getTimestamp();

    /**
     * Maps events that are still alive
     * @return
     */
    public boolean isAlive(int ttl);

    /**
     * Override to return a user defined id that can be used for ordering results
     * @return
     */
    public String getUserDefinedId();

    /**
     * Return value of given event field name
     * @return
     */
    public Object getFieldValue(String name);



}

