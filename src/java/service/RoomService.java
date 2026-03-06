package service;

import java.util.ArrayList;
import java.util.List;
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
     * Cập nhật trạng thái phòng (available, booked, maintenance).
     */
    public static void updateRoomStatus(String id, String status) {
        List<Room> rooms = getAllRooms();
        for (Room room : rooms) {
            if (room.getId().equalsIgnoreCase(id)) {
                room.setStatus(status);
                break;
            }
        }
        XMLUtil.writeRooms(rooms);
    }
}
