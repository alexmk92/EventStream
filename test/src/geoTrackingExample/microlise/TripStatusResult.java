package geoTrackingExample.microlise;

import java.util.List;

public class TripStatusResult {
  List<VanTripStatus> vanTripStatuses;
  List<DeliveryStatus> deliveryStatuses;

  public TripStatusResult(List<VanTripStatus> vanTripStatuses,
      List<DeliveryStatus> deliveryStatuses) {
    this.vanTripStatuses = vanTripStatuses;
    this.deliveryStatuses = deliveryStatuses;
  }
}
