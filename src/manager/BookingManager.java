package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Booking;
import model.Homestay;
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
    
    // --- CASE 6: XÓA BOOKING ---
    public void removeBookingById(String bookingID) {
        Booking b = searchBookingById(bookingID);
        if (b == null) {
            System.out.println("This booking does not exist!");
            return;
        }

        // Lưu lại mã Tour trước khi xóa để kiểm tra
        String tourIdOfRemovedBooking = b.getTourID();
        
        // Tiến hành xóa
        this.remove(b);
        System.out.println("Thành công: Đã xóa Booking [" + bookingID + "] khỏi hệ thống!");

        // Logic bổ sung: Kiểm tra xem Tour này còn ai đặt không. Nếu hết người đặt -> Đổi isBooked về false
        boolean isStillBooked = false;
        for (Booking bk : this) {
            if (bk.getTourID().equalsIgnoreCase(tourIdOfRemovedBooking)) {
                isStillBooked = true;
                break;
            }
        }
        if (!isStillBooked) {
            Tour t = tourManager.searchTourById(tourIdOfRemovedBooking);
            if (t != null) t.setBooking(false); // Trả lại trạng thái trống cho Tour
        }
    }

    // --- CASE 7: CẬP NHẬT BOOKING ---
    public void updateBookingById(String bookingID) {
        Booking b = searchBookingById(bookingID);
        if (b == null) {
            System.out.println("This Booking does not exist!");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- CẬP NHẬT BOOKING [" + bookingID + "] ---");
        System.out.println("(Mẹo: Nhấn ENTER để bỏ qua nếu muốn giữ nguyên giá trị cũ)");

        // 1. Cập nhật Tên
        System.out.print("Tên khách hàng cũ: " + b.getFullName() + " -> Tên mới: ");
        String newName = sc.nextLine().trim();
        if (!newName.isEmpty()) b.setFullName(newName);

        // 2. Cập nhật Tour ID
        Tour targetTour = tourManager.searchTourById(b.getTourID()); // Tour hiện tại
        System.out.print("Mã Tour cũ: " + b.getTourID() + " -> Mã Tour mới: ");
        String newTourID = sc.nextLine().trim();
        
        if (!newTourID.isEmpty()) {
            Tour t = tourManager.searchTourById(newTourID);
            if (t == null) {
                System.out.println("-> Lỗi: Tour này không tồn tại! Hệ thống giữ nguyên mã Tour cũ.");
            } else {
                b.setTourID(newTourID);
                targetTour = t; // Đổi sang Tour mới để lát nữa check ngày tháng
                t.setBooking(true); // Đánh dấu Tour mới đã có người đặt
            }
        }

        // 3. Cập nhật Ngày đặt (Logic: Ngày đặt < Ngày khởi hành của Tour mục tiêu)
        while (true) {
            System.out.print("Ngày đặt cũ: " + b.getBooking_date() + " -> Ngày mới (dd/mm/yyyy): ");
            String newDate = sc.nextLine().trim();
            if (newDate.isEmpty()) break;
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date bDate = sdf.parse(newDate);
                Date dDate = sdf.parse(targetTour.getDeparture_date());
                
                if (bDate.before(dDate)) {
                    b.setBooking_date(newDate);
                    break;
                } else {
                    System.out.println("-> Lỗi: Ngày đặt phải TRƯỚC ngày khởi hành (" + targetTour.getDeparture_date() + ")!");
                }
            } catch (Exception e) {
                System.out.println("-> Lỗi định dạng ngày!");
            }
        }

        // 4. Cập nhật Số điện thoại
        System.out.print("SĐT cũ: " + b.getPhone() + " -> SĐT mới: ");
        String newPhone = sc.nextLine().trim();
        if (!newPhone.isEmpty()) b.setPhone(newPhone);

        System.out.println("\nThành công: Đã cập nhật xong Booking [" + bookingID + "]!");
    }
    
    
    // --- CASE 8: TÌM KIẾM BOOKING THEO TÊN (Partial Name) ---
    public void listBookingsByName(String searchName) {
        System.out.println("\n--- KẾT QUẢ TÌM KIẾM BOOKING CHO: '" + searchName + "' ---");
        boolean found = false;
        // Đổi chuỗi tìm kiếm về chữ thường để không phân biệt hoa thường (vd: "binh" vẫn tìm ra "Binh")
        String searchLower = searchName.trim().toLowerCase();
        
        for (Booking b : this) {
            if (b.getFullName().toLowerCase().contains(searchLower)) {
                System.out.println(b.toString());
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("Không tìm thấy Booking nào khớp với tên này!");
        }
    }

    // --- CASE 9: THỐNG KÊ SỐ LƯỢNG KHÁCH THEO HOMESTAY ---
    public void printStatistics(HomestayManager hsManager) {
        System.out.println("\n--- THỐNG KÊ SỐ LƯỢNG KHÁCH THEO HOMESTAY ---");
        System.out.printf("| %-30s | %-15s |\n", "HomeName", "Number_Tourist");
        System.out.println("----------------------------------------------------");
        
        boolean hasData = false;

        // Vòng lặp 1: Lấy từng Homestay ra để kiểm tra
        for (Homestay hs : hsManager) {
            int totalTourists = 0; // Biến đếm số khách cho Homestay hiện tại

            // Vòng lặp 2: Quét toàn bộ danh sách Booking
            for (Booking b : this) {
                // Lấy thông tin Tour mà khách này đã đặt
                Tour t = tourManager.searchTourById(b.getTourID());
                
                // Nếu Tour tồn tại VÀ Tour đó thuộc về Homestay đang xét
                if (t != null && t.getHomeID().equalsIgnoreCase(hs.getHomeID())) {
                    // Cộng dồn số lượng khách của Tour đó vào biến đếm
                    totalTourists += t.getNumber_Tourist();
                }
            }

            // Sau khi đếm xong, nếu Homestay này có khách đặt thì mới in ra màn hình
            if (totalTourists > 0) {
                System.out.printf("| %-30s | %-15d |\n", hs.getHomeName(), totalTourists);
                hasData = true; // Đánh dấu là hệ thống có dữ liệu
            }
        }

        if (!hasData) {
            System.out.println("Chưa có dữ liệu Booking nào để thống kê!");
        }
        System.out.println("----------------------------------------------------");
    }
    
}