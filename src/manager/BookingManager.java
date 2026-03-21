package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Booking;
import model.Homestay;
import model.Tour;
import tools.Inputter;

public class BookingManager extends ArrayList<Booking> {
    Scanner sc = new Scanner(System.in);
    private Inputter ndl;
    private String pathFile = "./Bookings.txt";
    
    private TourManager tourManager; 

    public BookingManager(Inputter ndl,TourManager tourManager) {
        super();
        this.tourManager = tourManager;
        this.ndl = ndl;
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
        for (Booking b : this) {
            if(b.getBookingID().equalsIgnoreCase(id))
                return b;
        }
        return null;
    }

    // 3. THÊM BOOKING MỚI 
    public void addNewBooking() {
        Booking b = ndl.getBookingInfo();
        if(searchBookingById(b.getBookingID())!=null){
            System.out.println("Booking đã tồn tại");
            return;
        }
        
        Tour t = tourManager.searchTourById(b.getTourID());
        if(t== null){
            System.out.println("Tour đã tồn tại");
            return;
        }
        
        DateTimeFormatter fm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
            LocalDate depDate = LocalDate.parse(t.getDeparture_date(), fm);
            LocalDate bookDate = LocalDate.parse(b.getBooking_date(), fm);
            if (bookDate.isAfter(depDate)) {
                System.out.println("ngay dat phai truoc ngay khoi hanh");
                return;
            }
            
        t.setBooking(true);
        this.add(b);
        System.out.println("add thanh cong");
        
    }
    
    // --- CASE 6: XÓA BOOKING ---
    public void removeBookingById(String bookingID) {
        Booking bID = searchBookingById(bookingID);
        if(bID == null){
            System.out.println("ko tim thay booking");
            return;
        }
        
        String tourID = bID.getTourID();
        
        this.remove(bID);
        System.out.println("xoa thanh cong");
        
        boolean check = false;
        for (Booking b : this) {
            if(b.getTourID().equalsIgnoreCase(tourID)){
                check = true;
                break;
            }
        }
        
        if(!check){
            Tour t = tourManager.searchTourById(tourID);
            if(t!=null)
            t.setBooking(false);
        }
        
    }

    // --- CASE 7: CẬP NHẬT BOOKING ---
    public void updateBookingById(String bookingID) {
       Booking b = searchBookingById(bookingID);
       if(b==null){
           System.out.println("ko tim thay booking id");
           return;
       }
       
        System.out.println("update:");
        b.setFullName(UpdateDataBooking("Name: ", b.getFullName()));
        
        Tour targetTour = tourManager.searchTourById(b.getBookingID());
        System.out.print("Tour ID: ");
        String tourID= sc.nextLine();
        if(!tourID.isEmpty()){
            Tour t = tourManager.searchTourById(tourID);
            if(t==null)
                System.out.println("ko tim thay tour");
            else{
                b.setTourID(tourID);
                targetTour = t;
                t.setBooking(true);
            }
        }
        
        boolean flag = false;
        while (!flag) {
            String newDate = sc.nextLine();
            if (newDate.isEmpty()) {
                flag = true;
            } 
            else {
                try {
                    DateTimeFormatter fm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate bookDate = LocalDate.parse(newDate);
                    LocalDate depDate = LocalDate.parse(targetTour.getDeparture_date());
                    if (bookDate.isAfter(depDate)) {
                        System.out.println("phai nhap ngay truoc depDate");
                    } else {
                        b.setBookingID(newDate);
                        flag = true;
                    }
                } catch (Exception e) {
                    System.out.println("Loi dinh dang");
                }
            }
            
            b.setPhone(UpdateDataBooking("Phone", b.getPhone()));
            System.out.println("Thanh cong");
            
        }
        
        
        
    }
        
    
    
    public String UpdateDataBooking(String mess, String oldData){
        System.out.print(mess);
        String newData = sc.nextLine();
        return newData.isEmpty()? oldData:newData;
    }
    
    
    // --- CASE 8: TÌM KIẾM BOOKING THEO TÊN (Partial Name) ---
    public void listBookingsByName(String searchName) {
        System.out.println("\n--- KẾT QUẢ TÌM KIẾM BOOKING CHO: '" + searchName + "' ---");
        boolean found = false;
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
    
    
  
    public void printStatistics1(HomestayManager hsManager, TourManager tourManager) {
        System.out.println("\n--- THỐNG KÊ SỐ LƯỢNG KHÁCH THEO HOMESTAY ---");
        System.out.printf("| %-30s | %-15s |\n", "Tên Homestay", "Tổng số khách");
        System.out.println("----------------------------------------------------");
        
        boolean hasData = false;

        // 1. Quét từng Homestay
        for (Homestay hs : hsManager) {
            int totalTourists = 0;
            String currentHsId = hs.getHomeID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); 

            // 2. Quét kho Tour để lấy các Tour thuộc về Homestay này
            for (Tour t : tourManager) {
                String tourHomeId = t.getHomeID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                if (tourHomeId.equals(currentHsId)) {
                    String tourId = t.getTourID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    
                    // 3. Quét kho Booking: Đếm xem có bao nhiêu Booking đặt cái Tour này
                    for (Booking b : this) {
                        String bTourId = b.getTourID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                        if (bTourId.equals(tourId)) {
                            totalTourists += t.getNumber_Tourist();
                        }
                    }
                }
            }

            if (totalTourists > 0) {
                System.out.printf("| %-30s | %-15d |\n", hs.getHomeName(), totalTourists);
                hasData = true;
            }
        }

        if (!hasData) {
            System.out.println("Chưa có dữ liệu Booking nào để thống kê!");
        }
        System.out.println("----------------------------------------------------");
    }
    

    
    // --- CASE 9: THỐNG KÊ SỐ LƯỢNG KHÁCH THEO HOMESTAY ---
    public void printStatistics(HomestayManager hsManager, TourManager tourManager) {
        System.out.println("\n--- THỐNG KÊ SỐ LƯỢNG KHÁCH THEO HOMESTAY ---");
        System.out.printf("| %-30s | %-15s |\n", "HomeName", "Number_Tourist");
        System.out.println("----------------------------------------------------");
        
        boolean hasData = false;

        // Quét từng Homestay
        for (Homestay hs : hsManager) {
            int totalTourists = 0;
            String currentHsId = hs.getHomeID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); 

            // Quét từng Booking
            for (Booking b : this) {
                String bookingTourId = b.getTourID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                
                // Quét từng Tour
                for (Tour t : tourManager) {
                    String tourId = t.getTourID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    String tourHomeId = t.getHomeID().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                    if (tourId.equals(bookingTourId) && tourHomeId.equals(currentHsId)) {
                        totalTourists += t.getNumber_Tourist();
                    }
                }
            }


            if (totalTourists > 0) {
                System.out.printf("| %-30s | %-15d |\n", hs.getHomeName(), totalTourists);
                hasData = true;
            }
        }

        if (!hasData) {
            System.out.println("Chưa có dữ liệu Booking nào để thống kê!");
        }
        System.out.println("----------------------------------------------------");
    }
    
    
    // --- LƯU DỮ LIỆU BOOKING XUỐNG FILE ---
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new File("Bookings.txt"))) {
            for (Booking b : this) {
                pw.println(b.getBookingID() + "," + b.getFullName() + "," + 
                           b.getTourID() + "," + b.getBooking_date() + "," + b.getPhone());
            }
            System.out.println("-> Đã lưu dữ liệu Booking vào file thành công!");
        } catch (Exception e) {
            System.err.println("-> Lỗi không thể lưu file Booking!");
        }
    }
    
    
    
    
    
    
    
//     boolean check = false;
//        for (Booking b : this) {
//            if(b.getTourID().equalsIgnoreCase(tourID)){
//                check = true;
//                break;
//            }
//        }
//        
//        if(!check){
//            Tour t = tourManager.searchTourById(tourID);
//            if(t!=null)
//            t.setBooking(false);
//        }
    
    
    
    
//     Tour targetTour = tourManager.searchTourById(b.getBookingID());
//        System.out.print("Tour ID: ");
//        String tourID= sc.nextLine();
//        if(!tourID.isEmpty()){
//            Tour t = tourManager.searchTourById(tourID);
//            if(t==null)
//                System.out.println("ko tim thay tour");
//            else{
//                b.setTourID(tourID);
//                targetTour = t;
//                t.setBooking(true);
//            }
//        }
    
    
//    // 3. Cập nhật Ngày đặt (Dùng Cờ Boolean + Java 8 LocalDate cho đồng bộ)
//        boolean validDate = false;
//        while (!validDate) {
//            System.out.print("Ngày đặt mới (Cũ: " + b.getBooking_date() + "): ");
//            String newDate = sc.nextLine().trim();
//            if (newDate.isEmpty()) {
//                validDate = true; // Thoát nếu bấm Enter
//            } else {
//                try {
//                    DateTimeFormatter fm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                    LocalDate bookDate = LocalDate.parse(newDate, fm);
//                    LocalDate depDate = LocalDate.parse(targetTour.getDeparture_date().trim(), fm);
//                    
//                    if (bookDate.isBefore(depDate)) {
//                        b.setBooking_date(newDate);
//                        validDate = true; // Hợp lệ -> Thoát vòng lặp
//                    } else {
//                        System.out.println("-> Lỗi: Ngày đặt phải TRƯỚC ngày khởi hành (" + targetTour.getDeparture_date() + ")!");
//                    }
//                } catch (Exception e) {
//                    System.out.println("-> Lỗi định dạng ngày!");
//                }
//            }
//        }
    
}