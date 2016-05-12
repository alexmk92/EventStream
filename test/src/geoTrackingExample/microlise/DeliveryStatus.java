package geoTrackingExample.microlise;

import java.util.Date;

public class DeliveryStatus {
  public String van_trip_id;
  public String order_number;
  public String customer_name;
  public Date planned_arrival_time;
  public Double delivery_lat;
  public Double delivery_lon;

  public DeliveryStatus(String van_trip_id, String order_number, String customer_name,
      Date planned_arrival_time, Double delivery_lat, Double delivery_lon) {
    this.van_trip_id = van_trip_id;
    this.order_number = order_number;
    this.customer_name = customer_name;
    this.planned_arrival_time = planned_arrival_time;
    this.delivery_lat = delivery_lat;
    this.delivery_lon = delivery_lon;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("DeliveryStatus{");
    sb.append("van_trip_id='").append(van_trip_id).append('\'');
    sb.append(", order_number='").append(order_number).append('\'');
    sb.append(", customer_name='").append(customer_name).append('\'');
    sb.append(", planned_arrival_time=").append(planned_arrival_time);
    sb.append(", delivery_lat=").append(delivery_lat);
    sb.append(", delivery_lon=").append(delivery_lon);
    sb.append('}');
    return sb.toString();
  }
}
