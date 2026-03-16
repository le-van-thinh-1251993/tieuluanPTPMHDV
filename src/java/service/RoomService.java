package service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import model.Booking;
import model.Room;
import util.XMLUtil;

/**
 * Room Service - Quản lý thông tin phòng khách sạn.
 * 
 * Các chức năng chính:
 * - Lấy danh sách phòng
 * - Xem chi tiết phòng
 * - Lọc phòng theo loại
 * - Cập nhật trạng thái phòng
 */
public class RoomService {

    /**
     * Lấy danh sách tất cả phòng.
     */
    public static List<Room> getAllRooms() {
        return XMLUtil.readRooms();
    }

    /**
     * Tìm phòng theo ID.
     */
    public static Room getRoomById(String id) {
        for (Room room : getAllRooms()) {
            if (room.getId().equalsIgnoreCase(id)) {
                return room;
            }
        }
        return null;
    }

    /**
     * Lọc phòng theo loại (Deluxe, Standard, Suite, VIP, Family).
     */
    public static List<Room> getRoomsByType(String type) {
        List<Room> result = new ArrayList<>();
        for (Room room : getAllRooms()) {
            if (room.getType().equalsIgnoreCase(type)) {
                result.add(room);
            }
        }
        return result;
    }

    /**
     * Lọc phòng còn trống theo khoảng ngày.
     */
    public static List<Room> getAvailableRooms(String checkInDate, String checkOutDate) {
        List<Room> allRooms = getAllRooms();
        List<Booking> bookings = XMLUtil.readBookings();
        List<Room> available = new ArrayList<>();

        LocalDate in;
        LocalDate out;
        try {
            in = LocalDate.parse(checkInDate);
            if (checkOutDate != null && !checkOutDate.trim().isEmpty()) {
                out = LocalDate.parse(checkOutDate);
            } else {
                out = in.plusDays(1);
            }
        } catch (DateTimeParseException ex) {
            return allRooms; // nếu ngày sai định dạng thì trả về toàn bộ để client tự xử lý
        }

        for (Room room : allRooms) {
            if ("maintenance".equalsIgnoreCase(room.getStatus())) continue;
            boolean conflict = false;
            for (Booking b : bookings) {
                if (!"confirmed".equalsIgnoreCase(b.getStatus())) continue;
                if (!room.getId().equalsIgnoreCase(b.getRoomId())) continue;
                LocalDate exIn = parseDateSafe(b.getCheckInDate(), in);
                LocalDate exOut = parseDateSafe(b.getCheckOutDate(), exIn.plusDays(b.getNights()));
                if (isOverlap(in, out, exIn, exOut)) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) {
                available.add(room);
            }
        }
        return available;
    }

    /**
     * Cập nhật trạng thái phòng (available, booked, maintenance).
     */
    public static void updateRoomStatus(String id, String status) {
        if (status == null || !(status.equalsIgnoreCase("available")
                || status.equalsIgnoreCase("booked")
                || status.equalsIgnoreCase("maintenance"))) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ (available/booked/maintenance)");
        }

        List<Room> rooms = getAllRooms();
        for (Room room : rooms) {
            if (room.getId().equalsIgnoreCase(id)) {
                // Nếu chuyển sang available, đảm bảo không có booking đang/chuẩn bị check-in
                if (status.equalsIgnoreCase("available") && hasActiveBooking(id)) {
                    throw new IllegalArgumentException("Phòng đang có booking, không thể đặt trạng thái available");
                }
                room.setStatus(status);
                break;
            }
        }
        XMLUtil.writeRooms(rooms);
    }

    private static boolean hasActiveBooking(String roomId) {
        LocalDate today = LocalDate.now();
        for (Booking b : XMLUtil.readBookings()) {
            if (!"confirmed".equalsIgnoreCase(b.getStatus())) continue;
            if (!roomId.equalsIgnoreCase(b.getRoomId())) continue;
            LocalDate in = parseDateSafe(b.getCheckInDate(), today);
            LocalDate out = parseDateSafe(b.getCheckOutDate(), in.plusDays(b.getNights()));
            if (!today.isAfter(out.minusDays(1))) {
                return true; // còn hoặc sắp diễn ra
            }
        }
        return false;
    }

    private static boolean isOverlap(LocalDate newIn, LocalDate newOut, LocalDate exIn, LocalDate exOut) {
        return newIn.isBefore(exOut) && newOut.isAfter(exIn);
    }

    private static LocalDate parseDateSafe(String dateStr, LocalDate fallback) {
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                return LocalDate.parse(dateStr);
            }
        } catch (DateTimeParseException ignored) {
        }
        return fallback;
    }
}
