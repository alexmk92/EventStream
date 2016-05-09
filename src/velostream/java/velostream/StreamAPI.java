package velostream;

import static velostream.event.EmptyEvent.EMPTY_EVENT;
import velostream.stream.Stream;
import velostream.exceptions.StreamNotFoundException;
import velostream.stream.StreamDefinition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * StreamAPI - API that allows streams to be created, queried, and removed
 *
 * @Author Rich Durley
 * @Copyright Rich Durley 2014
 */
public class StreamAPI {

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
    stream = new Stream(streamDefinition.getName(), streamDefinition.getEventWorker(),
        streamDefinition.getWorkerParams(),
        streamDefinition.getEventTTLSeconds());
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
  public static Stream stream(String streamname) throws StreamNotFoundException {
    Stream toreturn = streams.get(streamname);
    if (toreturn != null)
      return toreturn;
    else
      throw new StreamNotFoundException("StreamAPIResource " + streamname + " not found");
  }


  /**
   * Types of querys that are supported
   */
  static final String[] queryTypes =
      new String[] {"All", "Last", "First", "AllAfter", "AllBefore", "Average", "Max", "Min",
          "Count", "Filter", "LastBy"};

  public enum QueryType {
    All, Last, First, AllAfter, AllBefore, Average, Max, Min, Count, Filter, LastBy, AverageBy, CountBy
  }

  /**
   * Executes the query of type query type on the stream with the given query parameters
   *
   * @param streamname
   * @param queryType
   * @param queryParams
   * @return
   */
  public static Object queryStream(String streamname, QueryType queryType, Object... queryParams) {

    Object toreturn = null;

      if (streams.containsKey(streamname)) {
        Stream stream = streams.get(streamname);

        switch (queryType) {

          case All: {
            return stream.query().getAll();
          }
          case Last: {
            return stream.query().getLast();
          }
          case First: {
            return stream.query().getFirst();
          }
          case AllAfter: {
            return stream.query().getAllAfter((long) queryParams[0]);
          }
          case AllBefore: {
            return stream.query().getAllBefore((long) queryParams[0]);
          }
          case Average: {
            return stream.query().getAverage((String) queryParams[0]);
          }
          case LastBy: {
            return stream.query().getLastBy((String) queryParams[0], queryParams[1]);

          }
          default: {
            throw new UnsupportedOperationException(queryType.toString());
          }
        }
      }

    return EMPTY_EVENT;

  }

}
