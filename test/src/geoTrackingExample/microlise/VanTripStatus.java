package geoTrackingExample.microlise;

import java.util.Date;

public class VanTripStatus {
  public String van_trip_id;
  public String van_reg;
  public Double van_lat;
  public Double van_lon;
  public Date last_contact_time;

  public VanTripStatus(String van_trip_id, String van_reg, Double van_lat, Double van_lon,
      Date last_contact_time) {
    this.van_trip_id = van_trip_id;
    this.van_reg = van_reg;
    this.van_lat = van_lat;
    this.van_lon = van_lon;
    this.last_contact_time = last_contact_time;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("VanTripStatus{");
    sb.append("van_trip_id='").append(van_trip_id).append('\'');
    sb.append(", van_reg='").append(van_reg).append('\'');
    sb.append(", van_lat=").append(van_lat);
    sb.append(", van_lon=").append(van_lon);
    sb.append(", last_contact_time=").append(last_contact_time);
    sb.append('}');
    return sb.toString();
  }
}
