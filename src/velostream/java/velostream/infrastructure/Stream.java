/*
 * stream.java
 * Created on 14 October 2006, 10:20
 * Copyright Richard Durley 
 */
package velostream.infrastructure;

import velostream.event.*;
import velostream.infrastructure.EventWorkerExecution;
import velostream.interfaces.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * A Stream - a stream of information
 *
 * A Stream is implemented with a Command Query Responsibility Segregation
 * External Events can be put into the stream and a segregated (in memory)
 * query store is used to provide a processed (i.e. filtered, aggregated, joined)
 * view of the events for the Stream that are pre-processed via the stream's query
 * store input processor.  All functions to query the stream should be implemented
 * here.
 *
 * @Author Rich Durley
 * @Copyright Rich Durley 2014
 */
public class Stream {

    //stream variables
    private final EventWorkerExecution eventEventWorkerExecution;
    //constants
    private final long tickstowait = 10;
    private final int queue_max_size = 1024*Runtime.getRuntime().availableProcessors();
    private int eventTTL;
    private IEventWorker eventProcessor;
    private String streamName;
    //stream state
    private BlockingQueue<IEvent> event_queue;
    private IEvent[] eventqueue_out;
    private int last_queue_size = 0;
    private volatile boolean isEnd = false;
    private Thread parked = null;
    private volatile boolean flush = false;
    //locks
    private Object lock = new Object();

    /**
     * Construct a new Stream with the given velostream.event comparator and velostream.event time to live
     *
     * Event comparator
     *      EventTimestampComparator orders Stream worker results in timestamp order
     *      EventIDComparator orders Stream worker results in velostream.event ID order
     *      EventUnorderedComparator Stream worker results are not put into order
     *
     * Event time to live is to be used with the timestamp comparator for auto removal
     * of events that have lived in the results set for longer than eventTTL seconds
     * a value <=0 will never remove results
     *
     * @param streamName
     * @param comparator
     * @param eventTTL
     */
    public Stream(String streamName,  IEventWorker[] workers, Comparator<IEvent> comparator, int eventTTL) {
        this.event_queue = new LinkedBlockingQueue<IEvent>(queue_max_size);
        this.eventqueue_out = new IEvent[this.queue_max_size];
        this.streamName=streamName;
        this.eventTTL = eventTTL;
        this.eventEventWorkerExecution = new EventWorkerExecution(this,workers, comparator, eventTTL);
    }

    /**
     * End the life of this stream :(
     */
    public void end() {
        this.isEnd = true;
    }

    /**
     * Returns if the stream has been ended
     * A stream ends once any remaining inputs events have been processed
     *
     * @return
     */
    public boolean isEnd() {
        return isEnd && size() == 0;
    }

    /**
     * Return the query store against which query operations can be made
     * @return
     */
    public EventWorkerExecution getEventEventWorkerExecution() {
        return eventEventWorkerExecution;
    }


    /**
     * Puts an array of events into the stream
     *
     * @param events
     */
    public void putAll(IEvent[] events) {
        if (events != null && !isEnd)
            Arrays.stream(events).parallel().forEach(e -> put(e,true));
    }

    /**
     * Put a single velostream.event into the stream
     * @param event to be put
     * @param block if block is true blocks until velostream.event put is successful
     * @return true if the put was successful
     */
    public boolean put(IEvent event, boolean block) {
        boolean put_ok = true;
        if (event != null && !isEnd)
            while (!(put_ok = doPut(event)) && block) LockSupport.parkNanos(1);
        return put_ok;
    }


    private boolean doPut(IEvent event) {
        boolean put_ok = true;
        try {
            if (event != null)
                put_ok = addToStream(event);
        } catch (InterruptedException i) {
            put_ok = false;
        }
        return put_ok;
    }

    private boolean addToStream(IEvent event) throws InterruptedException {
        boolean put_ok;
        if (this.event_queue.offer(event)) {
            this.flush = true;
            put_ok = true;
        } else {
            this.flush = true;
            LockSupport.unpark(this.parked);
            put_ok = this.event_queue.offer(event, 1, TimeUnit.NANOSECONDS);
        }
        return put_ok;
    }


    private int size() {
        return event_queue.size() - last_queue_size;
    }


    private void waitForInput() {
        int ticks = 0;
        while (this.event_queue.remainingCapacity() != 0 && !isEnd() && ticks <= tickstowait && !flush) {
            try {
                LockSupport.parkNanos(lock, 1);
                ticks++;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        flush = false;
    }

    public IEvent[] getAll() {
        IEvent[] result;

        waitForInput();
        int mmsize = this.event_queue.size();
        if ((getNumberOfNewEvents(mmsize, last_queue_size)) > 0) {
            result = Arrays.copyOfRange(this.event_queue.toArray(this.eventqueue_out), this.last_queue_size, mmsize);
            this.last_queue_size = mmsize;
            clearStreamWhenFull();
        } else {//no events
            result = EmptyEventArray.EMPTY_EVENT_ARRAY;
        }

        return result;
    }

    private int getNumberOfNewEvents(int mmsize, int lastsize) {

        return mmsize - lastsize;
    }

    private void clearStreamWhenFull() {
        if (this.last_queue_size == this.queue_max_size) {
            this.last_queue_size = 0;
            this.event_queue.clear();
        }
    }



}
