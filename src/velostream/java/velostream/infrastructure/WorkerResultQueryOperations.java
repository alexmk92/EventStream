package velostream.infrastructure;

import velostream.interfaces.IEvent;

import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * QueryStoreQueryOperations
 * @Author Richard Durley
 * @Copyright Richard Durley
 */
public class WorkerResultQueryOperations {

    private ConcurrentSkipListSet<IEvent> querystorecontents=null;
    private int eventTTL;

    /**
     * Query Store Constructor
     * @param eventWorkerExecution
     */
    public WorkerResultQueryOperations(EventWorkerExecution eventWorkerExecution)
    {
        this.querystorecontents= eventWorkerExecution.workerresults;
        this.eventTTL= eventWorkerExecution.eventTTL;
    }

    /**
     * Returns the events with the id eventid
     * @param id the eventid of the velostream.event to return
     * @return
     */
    public IEvent getEvent(long id) {
       List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.getId() == id).collect(Collectors.toList());
        if (events.size()==1)
            return events.get(0);
         else
            return null;
    }

    /**
     * Get the contents of events that are alive in velostream.event query store
     *
     * @return
     */
    public IEvent[] getAll() {
        List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.isAlive(eventTTL)).collect(Collectors.toList());
        return events.toArray(new IEvent[events.size()]);
    }

    /**
     * Get the contents of events before the given velostream.event in the query store
     *
     * @return
     */
    public IEvent[] getAllBefore(long eventid) {
        IEvent watermarkevent = getEvent(eventid);
        NavigableSet<IEvent> beforeset = querystorecontents.headSet(watermarkevent, false);
        return beforeset.toArray(new IEvent[beforeset.size()]);
    }

    /**
     * Get the contents of events after the given velostream.event in the query store
     *
     * @return IEvents[]
     */
    public IEvent[] getAllAfter(long eventid) {
        IEvent watermarkevent = getEvent(eventid);
        NavigableSet<IEvent> afterset = querystorecontents.tailSet(watermarkevent, false);
        return afterset.toArray(new IEvent[afterset.size()]);
    }


    /**
     * Get the last velostream.event in the stream's Query store
     * @return
     */
    public IEvent getLast() {
        return this.querystorecontents.last();
    }

    /**
     * Get the first velostream.event in the stream's Query store
     * @return
     */
    public IEvent getFirst() {
        return this.querystorecontents.stream().findFirst().filter(u-> u.isAlive(eventTTL)).get();
    }

    /**
     * Compute the average for a given field name in the Query store
     * @param fieldname
     * @return
     */
    public double getAverage(String fieldname) {
        return this.querystorecontents.stream().parallel().filter(u-> u.isAlive(eventTTL)).mapToDouble(e -> (double) e.getFieldValue(fieldname)).average().getAsDouble();
    }


}
