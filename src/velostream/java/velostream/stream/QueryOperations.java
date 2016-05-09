package velostream.stream;

import velostream.event.EventTimestampComparator;
import velostream.interfaces.IEvent;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import static velostream.event.EmptyEvent.EMPTY_EVENT;

/**
 * QueryStoreQueryOperations
 *
 * @Author Richard Durley
 * @Copyright Richard Durley
 */
public class QueryOperations {

  private ConcurrentSkipListSet<IEvent> querystorecontents = null;
  private int eventTTL;


  private class EventKeyIDEntry {
    private String key;
    private Long timestamp;
    private Long event_id;

    public EventKeyIDEntry(String key, Long event_id) {
      this.key = key;
      this.event_id = event_id;
    }
  }

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
    return querystorecontents.parallelStream().filter(u -> u.isAlive(eventTTL)).count();
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
    IEvent last = this.querystorecontents.last();
    if (last != null && last.isAlive(eventTTL))
      return last;
    else
      return EMPTY_EVENT;
  }

  public IEvent getLastBy(String fieldname, Object value) {
    List<IEvent> events = querystorecontents.parallelStream().filter(u -> u.isAlive(eventTTL))
        .filter(i -> i.getFieldValue(fieldname).equals(value)).collect(Collectors.toList());
    if (events.size() > 0)
      return events.get(events.size() - 1);
    else
      return EMPTY_EVENT;
  }

  public IEvent getFirst() {
    return this.querystorecontents.stream().findFirst().filter(u -> u.isAlive(eventTTL)).get();
  }

  public double getAverage(String fieldname) {
    try {
      return this.querystorecontents.stream().parallel()
          .filter(u -> u.isAlive(eventTTL) && u.hasFieldValue(fieldname))
          .mapToDouble(e -> (double) e.getFieldValue(fieldname)).average().getAsDouble();
    } catch (NoSuchElementException e) {
      return 0.0d;
    }
  }

  public IEvent getMax(String fieldname) {
    try {

      IEvent event;
      event = querystorecontents.stream().parallel()
          .filter(u -> u.isAlive(eventTTL) && u.hasFieldValue(fieldname))
          .max(Comparator.comparing(i -> (double) i.getFieldValue(fieldname))).get();
      return event;
    } catch (NoSuchElementException e) {
      return EMPTY_EVENT;
    }
  }

  public IEvent getMin(String fieldname) {
    try {
      IEvent event;
      event = querystorecontents.stream().parallel()
          .filter(u -> u.isAlive(eventTTL) && u.hasFieldValue(fieldname))
          .min(Comparator.comparing(i -> (double) i.getFieldValue(fieldname))).get();
      return event;
    } catch (NoSuchElementException e) {
      return EMPTY_EVENT;
    }
  }

  public double getSum(String fieldname) {
    try {
      return this.querystorecontents.stream().parallel()
          .filter(u -> u.isAlive(eventTTL) && u.hasFieldValue(fieldname))
          .mapToDouble(e -> (double) e.getFieldValue(fieldname)).sum();
    } catch (NoSuchElementException e) {
      return 0.0d;
    }
  }

  public Set<Map.Entry<Object, Optional<IEvent>>> getLastBy(String fieldname) {
    return querystorecontents.stream()
        .filter(u -> u.isAlive(eventTTL) && u.hasFieldValue(fieldname)).collect(Collectors
            .groupingBy(foo -> foo.getFieldValue(fieldname),
                Collectors.maxBy(new EventTimestampComparator()))).entrySet();

  }

}
