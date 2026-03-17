package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.*;

import model.Room;
import model.Booking;

/**
 * Tiện ích đọc/ghi dữ liệu XML bằng DOM Parser.
 */
public class XMLUtil {

    // ==================== ROOM ====================

    /**
     * Đọc danh sách phòng từ file rooms.xml
     */
    public static List<Room> readRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            java.net.URL url = XMLUtil.class.getClassLoader().getResource("data/rooms.xml");
            if (url == null) {
                System.out.println("[XMLUtil] Không tìm thấy file rooms.xml");
                return rooms;
            }
            java.io.File file = new java.io.File(url.toURI());
            InputStream is = new java.io.FileInputStream(file);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("room");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Room room = new Room(
                            getTagValue("id", element),
                            getTagValue("type", element),
                            Double.parseDouble(getTagValue("price", element)),
                            getTagValue("status", element));
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Ghi danh sách phòng vào file rooms.xml
     */
    public static void writeRooms(List<Room> rooms) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("rooms");
            doc.appendChild(rootElement);

            for (Room room : rooms) {
                Element roomElement = doc.createElement("room");

                roomElement.appendChild(createElement(doc, "id", room.getId()));
                roomElement.appendChild(createElement(doc, "type", room.getType()));
                roomElement.appendChild(createElement(doc, "price", String.valueOf((long) room.getPrice())));
                roomElement.appendChild(createElement(doc, "status", room.getStatus()));

                rootElement.appendChild(roomElement);
            }

            // Ghi vào file
            writeXmlToFile(doc, "data/rooms.xml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== BOOKING ====================

    /**
     * Đọc danh sách booking từ file bookings.xml
     */
    public static List<Booking> readBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            java.net.URL url = XMLUtil.class.getClassLoader().getResource("data/bookings.xml");
            if (url == null) {
                System.out.println("[XMLUtil] Không tìm thấy file bookings.xml");
                return bookings;
            }
            java.io.File file = new java.io.File(url.toURI());
            InputStream is = new java.io.FileInputStream(file);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("booking");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String checkOutStr = getTagValue("checkOutDate", element);
                    String nightsStr = getTagValue("nights", element);
                    String paymentStatus = getTagValue("paymentStatus", element);
                    String paidAmountStr = getTagValue("paidAmount", element);
                    String bankName = getTagValue("bankName", element);
                    double amount = Double.parseDouble(getTagValue("amount", element));
                    double paidAmount = (paidAmountStr != null && !paidAmountStr.isEmpty())
                            ? Double.parseDouble(paidAmountStr) : amount;
                    Booking booking = new Booking(
                            getTagValue("id", element),
                            getTagValue("customerName", element),
                            getTagValue("email", element),
                            getTagValue("roomId", element),
                            getTagValue("checkInDate", element),
                            checkOutStr != null && !checkOutStr.isEmpty() ? checkOutStr : "",
                            nightsStr != null && !nightsStr.isEmpty() ? Integer.parseInt(nightsStr) : 1,
                            amount,
                            getTagValue("status", element),
                            paymentStatus != null && !paymentStatus.isEmpty() ? paymentStatus : "unknown",
                            paidAmount,
                            bankName != null ? bankName : "");
                    bookings.add(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Thêm một booking mới vào file bookings.xml
     */
    public static void writeBooking(Booking booking) {
        try {
            // Đọc danh sách hiện có
            List<Booking> bookings = readBookings();
            bookings.add(booking);

            // Tạo document mới
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("bookings");
            doc.appendChild(rootElement);

            for (Booking b : bookings) {
                Element bookingElement = doc.createElement("booking");

                bookingElement.appendChild(createElement(doc, "id", b.getId()));
                bookingElement.appendChild(createElement(doc, "customerName", b.getCustomerName()));
                bookingElement.appendChild(createElement(doc, "email", b.getEmail()));
                bookingElement.appendChild(createElement(doc, "roomId", b.getRoomId()));
                bookingElement.appendChild(createElement(doc, "checkInDate", b.getCheckInDate()));
                bookingElement.appendChild(
                        createElement(doc, "checkOutDate", b.getCheckOutDate() != null ? b.getCheckOutDate() : ""));
                bookingElement.appendChild(createElement(doc, "nights", String.valueOf(b.getNights())));
                bookingElement.appendChild(createElement(doc, "amount", String.valueOf((long) b.getAmount())));
                bookingElement.appendChild(createElement(doc, "status", b.getStatus()));
                bookingElement.appendChild(createElement(doc, "paymentStatus",
                        b.getPaymentStatus() != null ? b.getPaymentStatus() : ""));
                bookingElement.appendChild(createElement(doc, "paidAmount", String.valueOf((long) b.getPaidAmount())));
                bookingElement.appendChild(createElement(doc, "bankName",
                        b.getBankName() != null ? b.getBankName() : ""));

                rootElement.appendChild(bookingElement);
            }

            // Ghi vào file
            writeXmlToFile(doc, "data/bookings.xml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lấy giá trị text của một tag XML.
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return "";
    }

    /**
     * Tạo một element XML với tag và giá trị text.
     */
    private static Element createElement(Document doc, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(value));
        return element;
    }

    /**
     * Ghi Document XML vào file resource.
     */
    private static void writeXmlToFile(Document doc, String resourcePath) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);

        // Tìm đường dẫn file thực tế của resource (trong thư mục build/deploy)
        URL url = XMLUtil.class.getClassLoader().getResource(resourcePath);
        if (url != null) {
            File file = new File(url.toURI());
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            System.out.println("[XMLUtil] Đã ghi dữ liệu vào build: " + file.getAbsolutePath());
            
            // Ghi đè ngược lại vào thư mục src để không mất dữ liệu sau khi clean and build
            try {
                String srcPath = file.getAbsolutePath().replace("\\build\\web\\WEB-INF\\classes\\", "\\src\\java\\");
                File srcFile = new File(srcPath);
                if (srcFile.exists()) {
                    StreamResult srcResult = new StreamResult(srcFile);
                    transformer.transform(source, srcResult);
                    System.out.println("[XMLUtil] Đã ghi dữ liệu vào src: " + srcFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                System.out.println("[XMLUtil] Lỗi ghi file src: " + ex.getMessage());
            }
        } else {
            System.out.println("[XMLUtil] Không tìm thấy file: " + resourcePath);
        }
    }

    /**
     * Tạo ID booking mới dựa trên ID cuối cùng.
     */
    public static String generateBookingId() {
        List<Booking> bookings = readBookings();
        if (bookings.isEmpty()) {
            return "BK1001";
        }

        // Lấy ID cuối cùng và tăng lên 1
        String lastId = bookings.get(bookings.size() - 1).getId();
        int number = Integer.parseInt(lastId.replace("BK", ""));
        return "BK" + (number + 1);
    }
}
