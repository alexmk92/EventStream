package geoTrackingExample;

public class Stop {

  private double lat;
  private double lon;
  private long expected_delivery_timestamp;
  private String customerId;

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  public String getCustomerId() {
    return customerId;
  }

  public long getExpected_delivery_timestamp() {
    return expected_delivery_timestamp;
  }

  public Stop(String customerId, double lat, double lon, long expected_delivery_timestamp) {
    this.lat = lat;
    this.lon = lon;
    this.expected_delivery_timestamp = expected_delivery_timestamp;
    this.customerId = customerId;
  }


}
