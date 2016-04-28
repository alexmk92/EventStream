package velostream.stream;

import velostream.interfaces.IEvent;

import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * QueryStoreQueryOperations
 *
 * @Author Richard Durley
 * @Copyright Richard Durley
 */
public class QueryOperations {

  private ConcurrentSkipListSet<IEvent> querystorecontents = null;
  private int eventTTL;

  public QueryOperations(QueryStore queryStore) {
    this.querystorecontents = queryStore.workerresults;
    this.eventTTL = queryStore.eventTTL;
  }

  public IEvent getEvent(long id) {
    List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.getEventID() == id)
        .collect(Collectors.toList());
    if (events.size() == 1)
      return events.get(0);
    else
      return null;
  }

  public long getCount() {
    List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.isAlive(eventTTL))
        .collect(Collectors.toList());
    return events.size();
  }

  public IEvent[] getAll() {
    List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.isAlive(eventTTL))
        .collect(Collectors.toList());
    return events.toArray(new IEvent[events.size()]);
  }

  public IEvent[] getAllBefore(long eventid) {
    IEvent watermarkevent = getEvent(eventid);
    NavigableSet<IEvent> beforeset = querystorecontents.headSet(watermarkevent, false);
    return beforeset.toArray(new IEvent[beforeset.size()]);
  }

  public IEvent[] getAllAfter(long eventid) {
    IEvent watermarkevent = getEvent(eventid);
    NavigableSet<IEvent> afterset = querystorecontents.tailSet(watermarkevent, false);
    return afterset.toArray(new IEvent[afterset.size()]);
  }

  public IEvent getLast() {
    return this.querystorecontents.last();
  }

  public IEvent getFirst() {
    return this.querystorecontents.stream().findFirst().filter(u -> u.isAlive(eventTTL)).get();
  }

  public double getAverage(String fieldname) {
    return this.querystorecontents.stream().parallel().filter(u -> u.isAlive(eventTTL))
        .mapToDouble(e -> (double) e.getFieldValue(fieldname)).average().getAsDouble();
  }

}
