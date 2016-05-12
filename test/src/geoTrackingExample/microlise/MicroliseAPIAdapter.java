package geoTrackingExample.microlise;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MicroliseAPIAdapter {

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  public enum EndPoint {
    GetTripStatus
  }

  public static Object callMicrolise(String serviceURI, EndPoint endpointName, String userName,
      String password) {
    try {
      SOAPMessage soapResponse = callMicroliseSOAPAPI(serviceURI, endpointName, userName, password);
      switch (endpointName) {
        case GetTripStatus: {
          return new TripStatusResult(captureVanTripStatus(soapResponse),
              captureDeliveryStatus(soapResponse));
        }
      }
    } catch (Exception e) {
      System.err.println("Error occurred while sending SOAP Request to Server");
      e.printStackTrace();
    }
    return null;
  }

  public static SOAPMessage callMicroliseSOAPAPI(String serviceURI, EndPoint endpointName,
      String userName, String password) throws SOAPException, IOException {
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
    SOAPMessage soapResponse =
        soapConnection.call(makeMicroliseSOAPRequest(endpointName, userName, password), serviceURI);
    soapConnection.close();
    return soapResponse;
  }

  private static List<VanTripStatus> captureVanTripStatus(SOAPMessage soapResponse)
      throws Exception {
    ArrayList<VanTripStatus> vanTripStatuses = new ArrayList<>();

    StringWriter writer = transformResponse(soapResponse);
    NodeList nList = getNodeList(writer, "VanTrips");
    processVehicleStatusNodeList(vanTripStatuses, nList);
    return vanTripStatuses;
  }

  private static List<DeliveryStatus> captureDeliveryStatus(SOAPMessage soapResponse)
      throws Exception {
    ArrayList<DeliveryStatus> deliveryStatuses = new ArrayList<>();

    StringWriter writer = transformResponse(soapResponse);
    NodeList nList = getNodeList(writer, "Deliveries");
    processDeliveryStatusesNodeList(deliveryStatuses, nList);
    return deliveryStatuses;
  }

  private static void processDeliveryStatusesNodeList(ArrayList<DeliveryStatus> deliveryStatuses,
      NodeList nList) throws ParseException {
    for (int temp = 0; temp < nList.getLength(); temp++) {
      org.w3c.dom.Node nNode = nList.item(temp);
      if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
        try {
          Element eElement = (Element) nNode;
          String van_trip_id = eElement.getElementsByTagName("VanTripID").item(0).getTextContent();
          String order_number =
              eElement.getElementsByTagName("OrderNumber").item(0).getTextContent();
          String customer_name =
              eElement.getElementsByTagName("CustomerName").item(0).getTextContent();
          Double delivery_lat = Double.valueOf(
              eElement.getElementsByTagName("LocationDegreesLat").item(0).getTextContent());
          Double delivery_lon = Double.valueOf(
              eElement.getElementsByTagName("LocationDegreesLong").item(0).getTextContent());
          String planned_arrival_time =
              eElement.getElementsByTagName("PlannedTimeArrival").item(0).getTextContent();
          Date takeUpdatesAfter = new Date(System.currentTimeMillis() - 60 * 1000 * 15);
          deliveryStatuses.add(new DeliveryStatus(van_trip_id, order_number, customer_name,
              dateFormat.parse(planned_arrival_time), delivery_lat, delivery_lon));
        } catch (NullPointerException e) {//Element Not Found
        }
      }
    }

  }

  private static void processVehicleStatusNodeList(ArrayList<VanTripStatus> vanTripStatuses,
      NodeList nList) throws ParseException {
    for (int temp = 0; temp < nList.getLength(); temp++) {
      org.w3c.dom.Node nNode = nList.item(temp);
      if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
        try {
          Element eElement = (Element) nNode;
          String van_trip_id = eElement.getElementsByTagName("VanTripID").item(0).getTextContent();
          String van_reg =
              eElement.getElementsByTagName("VehicleRegistration").item(0).getTextContent();
          Double van_lat = Double.valueOf(
              eElement.getElementsByTagName("CurrentPositionDegreesLat").item(0).getTextContent());
          Double van_lon = Double.valueOf(
              eElement.getElementsByTagName("CurrentPositionDegreesLong").item(0).getTextContent());
          String last_contact =
              eElement.getElementsByTagName("LastContactTime").item(0).getTextContent();
          Date takeUpdatesAfter = new Date(System.currentTimeMillis() - (60 * 1000 * 15));

          last_contact = last_contact.substring(0, 19);
          Date lastContactDate = dateFormat.parse(last_contact);

          if (van_lat != null && van_lon != null && lastContactDate.after(takeUpdatesAfter)) {
            vanTripStatuses.add(new VanTripStatus(van_trip_id, van_reg, van_lat, van_lon,
                dateFormat.parse(last_contact)));
          }
        } catch (NullPointerException e) {//Element Not Found
        }
      }
    }
  }

  private static NodeList getNodeList(StringWriter writer, String elementName)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new InputSource(new StringReader(writer.toString())));
    document.getDocumentElement().normalize();
    Element rootElement = document.getDocumentElement();
    return document.getElementsByTagName(elementName);
  }

  private static StringWriter transformResponse(SOAPMessage soapResponse)
      throws SOAPException, TransformerException, IOException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    Source sourceContent = soapResponse.getSOAPPart().getContent();
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    transformer.transform(sourceContent, result);
    writer.close();
    System.out.print("\nResponse SOAP Message = ");
    System.out.println(writer.toString());
    return writer;
  }


  private static SOAPMessage makeMicroliseSOAPRequest(EndPoint endpointName, String username,
      String password) throws SOAPException, IOException {
    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage soapMessage = messageFactory.createMessage();
    SOAPPart soapPart = soapMessage.getSOAPPart();
    Date startDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + (1000 * 60 * 60));

    SOAPEnvelope envelope = addSoapNamespaces(soapPart);
    addSOAPBody(endpointName.name(), startDate, endDate, envelope);
    addAuthorization(username, password, soapMessage);
    addSOAPAction(endpointName.name(), soapMessage);

    soapMessage.saveChanges();
    printSOAPMessage(soapMessage);

    return soapMessage;
  }

  private static void printSOAPMessage(SOAPMessage soapMessage) throws SOAPException, IOException {
  /* Print the request message */
    System.out.print("Request SOAP Message = ");
    soapMessage.writeTo(System.out);
    System.out.println();
  }

  private static SOAPEnvelope addSoapNamespaces(SOAPPart soapPart) throws SOAPException {
    SOAPEnvelope envelope = soapPart.getEnvelope();
    envelope.addNamespaceDeclaration("tws", "http://tsc.microlise.com/TWS/");
    envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
    envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
    return envelope;
  }

  private static void addSOAPBody(String endpointName, Date startDate, Date endDate,
      SOAPEnvelope envelope) throws SOAPException {
    SOAPBody soapBody = envelope.getBody();
    QName tripStatus = soapBody.createQName(endpointName, "tws");
    SOAPElement soapBodyElem = soapBody.addChildElement(tripStatus);
    SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("GroupName", "tws");
    soapBodyElem1.addTextNode("5379");
    SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("StartDate", "tws");
    soapBodyElem2.addTextNode(dateFormat.format(startDate) + "+01:00");
    SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("EndDate", "tws");
    soapBodyElem3.addTextNode(dateFormat.format(endDate) + "+01:00");
  }

  private static void addAuthorization(String username, String password, SOAPMessage soapMessage) {
    if (username != null && password != null) {
      String authorization =
          new sun.misc.BASE64Encoder().encode((username + ":" + password).getBytes());
      MimeHeaders hd = soapMessage.getMimeHeaders();
      hd.addHeader("Authorization", "Basic " + authorization);
    }

  }

  private static void addSOAPAction(String endpointName, SOAPMessage soapMessage) {
    MimeHeaders hd = soapMessage.getMimeHeaders();
    hd.addHeader("SOAPAction", "\"http://tsc.microlise.com/TWS/" + endpointName + "\"");
  }


}
