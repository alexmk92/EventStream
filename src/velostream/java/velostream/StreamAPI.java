package velostream;

import velostream.stream.Stream;
import velostream.event.EventIDComparator;
import velostream.event.EventTimestampComparator;
import velostream.event.EventUnorderedComparator;
import velostream.exceptions.StreamNotFoundException;
import velostream.interfaces.IEvent;
import velostream.stream.StreamDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * StreamAPI - API that allows streams to be created, queried, and removed
 *
 * @Author Rich Durley
 * @Copyright Rich Durley 2014
 */
public class StreamAPI {

  public static final int ORDERBY_UNORDERED = 2;
  public static final int ORDERBY_TIMESTAMP = 0;
  public static final int ORDERBY_EVENT_ID = 1;
  public static final int ORDERBY_EVENT_FIELD = 3;
  private static HashMap<String, Stream> streams = new HashMap<String, Stream>();

  /**
   * Create a new stream with given name, array of workers to work events,
   * with worker result ordered by the given order by value (0-timestamp, 1-eventID)
   * optional time to live of events can be specified when timestamp ordering is selected
   *
   * @return StreamAPIResource
   */
  public static Stream newStream(StreamDefinition streamDefinition) {
    Stream stream;
    switch (streamDefinition.getOrderBy()) {
      case 0: {
        stream = new Stream(streamDefinition.getName(), streamDefinition.getEventWorker(), streamDefinition.getWorkerParams(),
            new EventTimestampComparator(), streamDefinition.getEventTTLSeconds());
        break;
      }
      case 1: {
        stream = new Stream(streamDefinition.getName(), streamDefinition.getEventWorker(), streamDefinition.getWorkerParams(),
            new EventIDComparator(), 0);
        break;
      }
      case 2: {
        stream = new Stream(streamDefinition.getName(), streamDefinition.getEventWorker(),streamDefinition.getWorkerParams(),
            new EventUnorderedComparator(), 0);
        break;
      }

      default: {
        stream = new Stream(streamDefinition.getName(), streamDefinition.getEventWorker(),streamDefinition.getWorkerParams(),
            new EventIDComparator(), 0);
      }
    }
    streams.put(streamDefinition.getName(), stream);
    return stream;
  }

  /**
   * Get the stream given the streams name
   *
   * @param streamname
   * @return
   * @throws StreamNotFoundException
   */
  public static Stream getStream(String streamname) throws StreamNotFoundException {
    Stream toreturn = streams.get(streamname);
    if (toreturn != null)
      return toreturn;
    else
      throw new StreamNotFoundException("StreamAPIResource " + streamname + " not found");
  }

  /**
   * Puts an velostream.event into a velostream.stream   with name streamname
   * Pass true if want willing to block until velostream.event is put successfully into the velostream.stream
   * Returns true if velostream.event was put succesfully
   *
   * @param streamname
   * @param event
   * @param block
   * @return
   * @throws StreamNotFoundException
   */
  public static boolean put(String streamname, IEvent event, boolean block)
      throws StreamNotFoundException {
    if (streams.containsKey(streamname)) {
      return streams.get(streamname).put(event, block);
    } else
      throw new StreamNotFoundException("StreamAPIResource " + streamname + " not found");
  }

  /**
   * Puts an velostream.event into a velostream.stream   with name streamname
   * Blocks until all events are put successfully into the velostream.stream
   *
   * @param streamname
   * @param events
   * @return
   * @throws StreamNotFoundException
   */
  public static Stream putAll(String streamname, IEvent events[]) throws StreamNotFoundException {
    if (streams.containsKey(streamname)) {
      streams.get(streamname).putAll(events);
      return streams.get(streamname);
    } else
      throw new StreamNotFoundException("StreamAPIResource " + streamname + " not found");
  }

  /**
   * Remove an velostream.event from the stream
   *
   * @param es
   * @param eventId
   * @return
   */
  public static Stream remove(Stream es, long eventId) {
    throw new UnsupportedOperationException("remove operation not yet implemented");
  }

  /**
   * Remove a list of events from the stream
   *
   * @param es
   * @param eventIds
   * @return
   */
  public static Stream removeAll(Stream es, long[] eventIds) {
    throw new UnsupportedOperationException("remove all operation not yet implemented");
  }


  /**
   * Types of querys that are supported
   */
  static final String[] queryTypes =
      new String[] {"All", "Last", "First", "AllAfter", "AllBefore", "Average", "Max", "Min",
          "Count", "Filter"};

  /**
   * Executes the query of type query type on the stream with the given query parameters
   *
   * @param streamname
   * @param queryType
   * @param queryParams
   * @return
   */
  public static Object doQuery(String streamname, String queryType, Object... queryParams) {

    Object toreturn = null;

    try {

      int queryToExecute = Arrays.binarySearch(queryTypes, queryType);

      if (streams.containsKey(streamname)) {
        Stream stream = streams.get(streamname);

        switch (queryToExecute) {

          //ALL
          case 0: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, null);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          //LAST
          case 1: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, null);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          //FIRST
          case 2: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, null);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          //ALLAFTER
          case 3: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, long.class);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          //ALLBEFORE
          case 4: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, long.class);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          //AVERAGE
          case 5: {
            Method queryMethod =
                stream.getEventWorkerExecution().getWorkerResultQueryOperations().getClass()
                    .getDeclaredMethod("get" + queryType, String.class);
            toreturn = queryMethod
                .invoke(stream.getEventWorkerExecution().getWorkerResultQueryOperations(),
                    queryParams);
            break;
          }
          default: {
            throw new UnsupportedOperationException(queryType);
          }
        }
      }
    } catch (NoSuchMethodException e) {

    } catch (IllegalAccessException e2) {

    } catch (InvocationTargetException e3) {

    }
    return toreturn;
  }

}
