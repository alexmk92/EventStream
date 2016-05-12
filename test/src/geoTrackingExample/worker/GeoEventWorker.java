package geoTrackingExample.worker;

import geoTrackingExample.domain.AlertType;
import geoTrackingExample.util.LatLongDistanceCalculator;
import geoTrackingExample.domain.Stop;
import velostream.interfaces.IEvent;
import velostream.interfaces.IEventWorker;

import static velostream.util.EventBuilder.eventBuilder;

import java.util.*;

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

  public void setJourney(ArrayList<Stop> stops) {
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

    long latemillis = ((getCurrentTimeInMillisecconds() + time_to_first_delivery) - stop
        .getExpected_delivery_timestamp());

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
    return now.get(Calendar.HOUR_OF_DAY);
  }

  long getCurrentTimeInMillisecconds() {
    return System.currentTimeMillis();
  }

  long getMinutesToTimeStampFromNow(long timestamp) {
    return (timestamp - getCurrentTimeInMillisecconds()) / 1000 / 60;
  }

  public List<IEvent> work(IEvent eventIn, Map<String, Object> params) {
    ArrayList<IEvent> alerts = new ArrayList<>();
    van_lat = (double) eventIn.getFieldValue("van_lat");
    van_lon = (double) eventIn.getFieldValue("Van_lon");
    avg_roadspeed_KMH = (double) eventIn.getFieldValue("avg_speed");

    ArrayList<Stop> stops_remaining = getStopsForNextHour(getCurrentTimeInMillisecconds());
    if (stops_remaining.size() > 0) {
      firstStop = stops_remaining.get(0);

      for (Stop stop : getStopsForNextHour(getCurrentTimeInMillisecconds())) {
        if (isOnTime(stop, getAvg_roadspeed_KMH()))
          alerts.add(eventBuilder("CustomerGeoUpdate").addFieldValue("status", AlertType.ONTIME)
              .addFieldValue("customerId", stop.getCustomerId())
              .addFieldValue("message", "Your Delivery Is On Time").build());
        else
          alerts.add(eventBuilder("CustomerGeoUpdate").addFieldValue("status", AlertType.LATE)
              .addFieldValue("customerId", stop.getCustomerId())
              .addFieldValue("message", "Your Delivery Is Late").addFieldValue("expected_mins",
                  getMinutesToTimeStampFromNow(stop.getExpected_delivery_timestamp()
                      + getTravelTimeToFirstDeliveryInMillisecods(avg_roadspeed_KMH))).build());

      }

    }
    return alerts;
  }
}
