package geoTrackingExample;

import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;
import velostream.util.EventBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;



public class GeoEventWorker implements IEventWorker {

  static long ONE_HOUR_Milliseconds = 60 * 60 * 1000;
  static long ONE_HOUR_Seconds = 60 * 60;
  static long AVG_ROAD_SPEED_KM_HOUR = 20;

  double van_lat;
  double van_lon;
  ArrayList<Stop> stops = new ArrayList<>();
  Stop firstStop;
  double avg_roadspeed_KMH = 20;

  public double getAvg_roadspeed_KMH() {
    return avg_roadspeed_KMH;
  }

  public void setAvg_roadspeed_KMH(double avg_roadspeed_KMH) {
    this.avg_roadspeed_KMH = avg_roadspeed_KMH;
  }


  public void setStops(ArrayList<Stop> stops) {
    this.stops = stops;
  }

  private ArrayList<Stop> getStopsForNextHour(long currentTimeMilliseconds) {

    ArrayList<Stop> next_stops = new ArrayList<>();

    for (Stop stop : stops) {
      if (isStopInDeliveryWindow(currentTimeMilliseconds, stop)) {
        next_stops.add(stop);

      }
    }

    return next_stops;

  }

  public boolean isStopInDeliveryWindow(long currentTimeMilliseconds, Stop stop) {
    if (stop.getExpected_delivery_timestamp() > currentTimeMilliseconds)
      if (stop.getExpected_delivery_timestamp() < currentTimeMilliseconds + ONE_HOUR_Milliseconds)
        return true;
    return false;

  }

  private double calculateTimeToNextDeliveryInSeconds(double lat1, double lon1, Stop stop,
      double avg_road_speed) {

    return LatLongDistanceCalculator
        .timeSeconds(lat1, lon1, stop.getLat(), stop.getLon(), avg_road_speed);
  }

  public long getTravelTimeToFirstDeliveryInMillisecods(double roadspeed) {
    return (long) (calculateTimeToNextDeliveryInSeconds(van_lat, van_lon, firstStop, roadspeed))
        * 1000;
  }


  public boolean isOnTime(Stop stop, double avg_road_speed) {

    long time_to_first_delivery = getTravelTimeToFirstDeliveryInMillisecods(avg_road_speed);

    long latemillis =    ((getCurrentTimeInMillisecconds() + time_to_first_delivery)
            - stop.getExpected_delivery_timestamp());

    if (latemillis > 0) {
      int deliveryHour = getDeliveryHour(latemillis + stop.getExpected_delivery_timestamp());
      int expectedDeliveryHour = getDeliveryHour(stop.getExpected_delivery_timestamp());
      if (deliveryHour <= expectedDeliveryHour)
        return true;
      else
        return false;
    } else
      return true;
  }

  int getDeliveryHour(long timestamp) {
    Calendar now = Calendar.getInstance();
    now.setTime(new Date(timestamp));
    return now.get(Calendar.HOUR);
  }

  long getCurrentTimeInMillisecconds() {
    return System.currentTimeMillis();
  }

  long getCurrentTimeInSeconds() {
    return System.currentTimeMillis() / 1000 / 60;
  }

  long getMinutesToTimeStampFromNow(long timestamp) {
    return (timestamp-getCurrentTimeInMillisecconds())/1000/60;
  }

  public IEvent work(IEvent eventIn, Map<String, Object> params) {
    ArrayList<GeoAlert> alerts = new ArrayList<>();
    van_lat = (double) eventIn.getFieldValue("lat");
    van_lon = (double) eventIn.getFieldValue("lon");

    ArrayList<Stop> stops_remaining = getStopsForNextHour(getCurrentTimeInMillisecconds());
    if (stops_remaining.size() > 0) {
      firstStop = stops_remaining.get(0);

      for (Stop stop : getStopsForNextHour(getCurrentTimeInMillisecconds())) {
        if (isOnTime(stop, getAvg_roadspeed_KMH()))
          alerts.add(
              new GeoAlert(AlertType.ONTIME, stop.getCustomerId(), "Your delivery is on time",
                  getMinutesToTimeStampFromNow(stop.getExpected_delivery_timestamp())));
        else
          alerts.add(new GeoAlert(AlertType.LATE, stop.getCustomerId(), "Your delivery is late",
              getMinutesToTimeStampFromNow(stop.getExpected_delivery_timestamp() + getTravelTimeToFirstDeliveryInMillisecods(
                  this.getAvg_roadspeed_KMH()))));
      }

      return EventBuilder.builder("geoalert").addFieldValue("Alert", alerts).build();
    }
    return null;
  }
}
