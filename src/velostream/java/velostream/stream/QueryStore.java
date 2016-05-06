package velostream.stream;

import com.sun.istack.internal.Nullable;
import velostream.event.EventTimestampComparator;
import velostream.stream.workers.PassthroughEventWorker;
import velostream.event.WatermarkEvent;
import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;
import velostream.util.Execute;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

/**
 * The velostream.event eventQueryStore where the putAll events are persisted in memory
 */
public class QueryStore {

    protected ConcurrentSkipListSet<IEvent> workerresults;
    protected int eventTTL=0;
    private Stream stream = null;
    private boolean isEnd = false;
    private IEventWorker worker;

    private QueryOperations queryOperations =null;

    /**
     * New Event Worker Execution using the given checked Comparator</IEvent> to order the results
     * passing velostream.event time to live > 0 when using the EventTimestampComparator specifies how long events
     * in seconds will be retained before being automatically removed from the worker results
     * @param stream
     * @param eventTTL
     */
    public QueryStore(Stream stream, @Nullable IEventWorker worker, int eventTTL) {
        this.stream = stream;
        if (worker!=null)
                this.worker=worker;
        else this.worker=new PassthroughEventWorker();
        this.eventTTL=eventTTL;

        workerresults = new ConcurrentSkipListSet<IEvent>(new EventTimestampComparator());
        queryOperations = new QueryOperations(this);
        if (eventTTL>0)
            Execute.getInstance().submit(new ExpiredEventsCollector());

        Execute.getInstance().submit(new WorkerInput(this));
    }

    /**
     * Remove worker results that were stored before the given eventid
     * @param eventid
     */
    public void removeWorkerResultsBefore(long eventid) {
        workerresults.headSet(new WatermarkEvent(eventid, 0), false);
    }

    /**
     * Get the query stores query operations
     * @return
     */
    public QueryOperations getQueryOperations() {
        return queryOperations;
    }

    /**
     * Returns if the query store has ended
     * @return
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Gets all the input from the velostream.event stream and persists it to the query store
     */
    private class WorkerInput implements Runnable {

        QueryStore queryStore;

        public WorkerInput(QueryStore queryStore) {
            this.queryStore = queryStore;
        }

        @Override
        public void run() {
            try {
                while (!stream.isEnd())
                    this.queryStore.work(stream.getAll());
                this.queryStore.work(stream.getAll());
                isEnd=true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    /**
     * Do work
     * @param events
     */
    private void work(IEvent[] events) {
        Arrays.stream(events).forEach(event -> workerresults.addAll(callWorker(event, stream.getWorkerParams())));
    }

    /**
     * Call the workers
     * @param event
     * @return
     */
    private List<IEvent> callWorker(IEvent event, Map<String, Object> workerParams){
        return this.worker.work(event, workerParams);
    }


    /**
     * Removes dead events from the query store
     */
    private class ExpiredEventsCollector implements Runnable {

        @Override
        public void run() {
            while (!stream.isEnd()) {
                try {
                    removeExpiredEventsWhenLimitReached(10000);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }

        private IEvent createWatermarkTimestampEventForComparison() {
            return new WatermarkEvent(0, System.currentTimeMillis()- (eventTTL * 1000));
        }

        private void removeExpiredEventsWhenLimitReached(int thelimit) {
            IEvent watermarktimestampevent = createWatermarkTimestampEventForComparison();
            NavigableSet<IEvent> expiredevents = getEventsBelowWaterMarkTimestmapEvent(watermarktimestampevent);
            if (expiredevents.size() > thelimit)
                workerresults.removeAll(expiredevents);
        }

        private NavigableSet<IEvent> getEventsBelowWaterMarkTimestmapEvent(IEvent watermarkevent) {
            return workerresults.headSet(watermarkevent);
        }


    }

}
