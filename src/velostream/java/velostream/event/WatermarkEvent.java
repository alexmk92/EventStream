package velostream.event;

/**
 * Created by Admin on 20/09/2014.
 */
public class WatermarkEvent extends Event {

      public WatermarkEvent(long eventid, long watermarktimestamp) {
          super(eventid, watermarktimestamp);
      }

}
