package geoTrackingExample;
public class GeoAlert {

  AlertType type;
  String customerID;
  String message;
  double minutes_to_delivery;

  public AlertType getType() {
    return type;
  }

  public String getCustomerID() {
    return customerID;
  }

  public String getMessage() {
    return message;
  }

  public double getMinutes_to_delivery() {
    return minutes_to_delivery;
  }

  public GeoAlert() {
    super();
  }

  public GeoAlert(AlertType type, String customerID, String message, double minutes_to_delivery) {
    this.type = type;
    this.customerID = customerID;
    this.message = message;
    this.minutes_to_delivery = minutes_to_delivery;
  }
}
