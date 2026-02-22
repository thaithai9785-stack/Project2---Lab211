package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Booking;
import model.Tour;

public class BookingManager extends ArrayList<Booking> {
    private String pathFile = "./Bookings.txt";
    
    private TourManager tourManager; 

    public BookingManager(TourManager tourManager) {
        super();
        this.tourManager = tourManager;
        this.readFromFile();
    }

    // 1. ĐỌC DỮ LIỆU TỪ FILE BOOKINGS.TXT
    public void readFromFile() {
        this.clear();
        File f = new File(pathFile);
        if (!f.exists()) {
            System.err.println("Cảnh báo: Không tìm thấy file " + pathFile);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("\uFEFF")) line = line.substring(1); // Lọc BOM ẩn
                
                Booking b = textToBooking(line);
                if (b != null) this.add(b);
            }
            System.out.println("Loaded " + this.size() + " bookings successfully!");
        } catch (Exception ex) {
            Logger.getLogger(BookingManager.class.getName()).log(Level.SEVERE, "Lỗi nạp file Booking", ex);
        }
    }

    // Định dạng giả định trong file txt: bookingID, fullName, tourID, booking_date, phone
    private Booking textToBooking(String line) {
        try {
            String[] parts = line.split(","); // Tương tự Tour, dùng dấu phẩy
            if (parts.length >= 5) {
                return new Booking(
                    parts[0].trim(), // bookingID
                    parts[1].trim(), // fullName
                    parts[2].trim(), // tourID
                    parts[3].trim(), // booking_date
                    parts[4].trim()  // phone
                );
            }
        } catch (Exception e) {
            System.err.println("Bỏ qua dòng dữ liệu Booking lỗi: " + line);
        }
        return null;
    }

    // 2. TÌM KIẾM BOOKING (Ràng buộc 2a) [cite: 195]
    public Booking searchBookingById(String id) {
        if (id == null) return null;
        for (Booking b : this) {
            if (b.getBookingID().trim().equalsIgnoreCase(id.trim())) {
                return b;
            }
        }
        return null;
    }

    // 3. THÊM BOOKING MỚI (Xử lý các ràng buộc phức tạp) [cite: 207, 215]
    public void addNewBooking(Booking b) {
        // 1. Kiểm tra mã Booking không được trùng (Constraint 2a)
        if (searchBookingById(b.getBookingID()) != null) {
            System.out.println("Lỗi: Mã Booking [" + b.getBookingID() + "] đã tồn tại trên hệ thống!");
            return;
        }

        // 2. Kiểm tra Tour khách muốn đặt có tồn tại không (Constraint 2c)
        Tour t = tourManager.searchTourById(b.getTourID());
        if (t == null) {
            System.out.println("Lỗi: Mã Tour [" + b.getTourID() + "] không tồn tại! Vui lòng kiểm tra lại.");
            return;
        }

        // 3. Kiểm tra logic Thời gian: Ngày đặt PHẢI TRƯỚC ngày khởi hành (Constraint 2d)
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date bookingDate = sdf.parse(b.getBooking_date());
            Date departureDate = sdf.parse(t.getDeparture_date());

            if (!bookingDate.before(departureDate)) {
                System.out.println("Lỗi: Ngày đặt tour (" + b.getBooking_date() + ") phải TRƯỚC ngày khởi hành (" + t.getDeparture_date() + ")!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Hệ thống không thể xử lý định dạng ngày tháng!");
            return;
        }

        // 4. Mọi thứ hoàn hảo -> Lưu Booking và Cập nhật trạng thái Tour
        this.add(b);
        t.setBooking(true); // Đổi trạng thái Tour thành ĐÃ CÓ NGƯỜI ĐẶT
        System.out.println("Thành công: Đã chốt đơn Booking [" + b.getBookingID() + "] cho Tour [" + t.getTourID() + "]!");
    }
    
    // 4. HIỂN THỊ DANH SÁCH BOOKING (Cho chức năng số 8) [cite: 210]
    public void printAllBookings() {
        if (this.isEmpty()) {
            System.out.println("Danh sách Booking đang trống!");
            return;
        }
        System.out.println("\n--- LIST OF BOOKINGS ---");
        for (Booking b : this) {
            System.out.println(b.toString());
        }
        System.out.println("------------------------");
    }
}