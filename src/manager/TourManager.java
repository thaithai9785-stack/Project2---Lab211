package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Homestay;
import model.Tour;

public class TourManager extends ArrayList<Tour> {

    private HomestayManager hsManager;
    private String pathFile;
   
    public TourManager(HomestayManager hsManager) {
        super();
        this.hsManager = hsManager;
        this.pathFile = "./Tours.txt";
        this.readFromFile();

        this.add(new Tour("T11111", "Da Lat", "3 Days", 1500.0, "HS0001", "15/03/2026", "17/03/2026", 10, false));
        this.add(new Tour("T11112", "Nha Trang", "2 Days", 800.5, "HS0002", "20/04/2026", "21/04/2026", 5, true));
        this.add(new Tour("T11113", "Sapa", "4 Days", 2500.0, "HS0003", "10/05/2025", "13/05/2026", 20, false));
    }

    public void readFromFile() {
        this.clear(); 
        File f = new File(pathFile);
        if (!f.exists()) {
            System.out.println("File not found: " + pathFile);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Tour x = textToTour(line);
                if (x != null) this.add(x);
            }
            // In số lượng để kiểm tra thực tế trong RAM
            System.out.println("Nạp thành công " + this.size() + " tours từ file!");
        } catch (IOException ex) {
            Logger.getLogger(TourManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
 
    
    
    public Tour textToTour(String tam){
        Tour t = null;
        String[] temp = tam.split(",");
        try {

            if (temp.length == 9) {
                String id = temp[0].trim();
                String name = temp[1].trim();
                String time = temp[2].trim();
                double price = Double.parseDouble(temp[3].trim().replaceAll("[^0-9.]", ""));
                String homeId = temp[4].trim();
                String depDate = temp[5].trim();
                String endDate = temp[6].trim();
                int numTourist = Integer.parseInt(temp[7].trim().replaceAll("[^0-9]", ""));
                boolean isBooked = Boolean.parseBoolean(temp[8].trim());
   
                t = new Tour(id, name, time, price, homeId, depDate, endDate, numTourist, isBooked);            
 
            }
        } catch (Exception e) {
            t = null; 
        }     
        return t;
    }
    
    
    public void printAllTours() {
      
        if (this.isEmpty()) {
            System.out.println("Empty list. No tours found!");
            return;
        }
        
        System.out.println("\n--- LIST OF TOURS ---");
      
        for (Tour t : this) {
            System.out.println(t.toString()); 
        }
        System.out.println("---------------------");
    }
    
    public Tour searchTourById(String id) {
        for (Tour t : this) {
            if (t.tourID.equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }

    public void addNew(Tour x) {
        Homestay hs = hsManager.searchHomestayById(x.homeID);
        if (searchTourById(x.tourID) != null) {
            System.out.println("Lỗi: tourID đã tồn tại!");
            return;
        }
        if (hs == null) {
            System.out.println("Lỗi: Mã Homestay không tồn tại!");
            return;
        }
        if (x.number_Tourist > hs.getMaximumcapacity()) {
            System.out.println("Lỗi: Quá sức chứa tối đa (" + hs.getMaximumcapacity() + ")!");
            return;
        }
        this.add(x);
        System.out.println("Thêm Tour [" + x.tourID + "] thành công!");

    }
    
    public void updateTourById(String tourID) {
        // 1. Kiểm tra tồn tại
        Tour t = searchTourById(tourID);
        if (t == null) {
            System.out.println("This tour does not exist!");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- CẬP NHẬT TOUR [" + tourID + "] ---");
        System.out.println("(Mẹo: Nhấn phím ENTER để bỏ qua nếu muốn giữ nguyên giá trị cũ)");

        // 2. Cập nhật Tên Tour
        System.out.print("Tên mới: ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) t.setTourName(name);

        // 3. Cập nhật Thời gian
        System.out.print("Thời gian mới: ");
        String time = sc.nextLine().trim();
        if (!time.isEmpty()) t.setTime(time);

        // 4. Cập nhật Giá (Xử lý lỗi nhập chữ)
        while (true) {
            System.out.print("Giá mới: ");
            String priceStr = sc.nextLine().trim();
            if (priceStr.isEmpty()) {
                break;
            }
            try {
                double p = Double.parseDouble(priceStr);
                if (p > 0) {
                    t.setPrice(p);
                    break;
                } else {
                    System.out.println("-> Giá phải lớn hơn 0!");
                }
            } catch (Exception e) {
                System.out.println("-> Lỗi định dạng số!");
            }
        }

        // 5. Cập nhật Mã Homestay (Check tồn tại)
        while (true) {
            System.out.print("Mã mới (vd: HS0001): ");
            String hId = sc.nextLine().trim();
            if (hId.isEmpty()) {
                break;
            }

            Homestay hs = hsManager.searchHomestayById(hId);
            if (hs == null) {
                System.out.println("-> Lỗi: Mã Homestay không tồn tại!");
            } else {
                t.setHomeID(hId);
                break;
            }
        }

        // 6. Cập nhật Ngày khởi hành
        System.out.print("Ngày mới (dd/mm/yyyy): ");
        String dDate = sc.nextLine().trim();
        if (!dDate.isEmpty()) t.setDeparture_date(dDate);

        // 7. Cập nhật Ngày kết thúc
        System.out.print("Ngày mới (dd/mm/yyyy): ");
        String eDate = sc.nextLine().trim();
        if (!eDate.isEmpty()) t.setEnd_date(eDate);

        // 8. Cập nhật Số lượng khách (Check sức chứa Homestay)
        while (true) {
            System.out.print("Số khách mới: ");
            String numStr = sc.nextLine().trim();
            if (numStr.isEmpty()) break; 
            try {
                int n = Integer.parseInt(numStr);
                Homestay hs = hsManager.searchHomestayById(t.getHomeID()); 
                if (n > 0 && hs != null && n <= hs.getMaximumcapacity()) {
                    t.setNumber_Tourist(n);
                    break;
                } else {
                    System.out.println("-> Lỗi: Quá sức chứa của Homestay (" + hs.getMaximumcapacity() + " khách)!");
                }
            } catch (Exception e) { System.out.println("-> Lỗi định dạng số!"); }
        }

        // 9. Cập nhật trạng thái Booking
        System.out.print("Trạng thái Booking cũ: " + t.isBooking()+ " -> Đổi thành (true/false): ");
        String bStatus = sc.nextLine().trim();
        if (!bStatus.isEmpty())
            t.setBooking(Boolean.parseBoolean(bStatus));

        System.out.println("\nThành công: Đã cập nhật Tour [" + tourID + "]!");
    }
    
    
    public void listToursDepartureEarlier() {
        System.out.println("\n--- TOURS DEPARTURE EARLIER THAN " + LocalDate.now() + " ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        boolean found = false;

        for (Tour t : this) {
            try {
                // Dọn dẹp chuỗi ngày: Chỉ giữ lại số và dấu gạch chéo 
                String cleanDate = t.getDeparture_date().trim().replaceAll("[^0-9/]", "");
                LocalDate depDate = LocalDate.parse(cleanDate, formatter);
                
                if (depDate.isBefore(currentDate)) {
                    System.out.println(t.toString());
                    found = true;
                }
            } catch (Exception e) {
                // In lỗi ra để debug nếu ngày vẫn bị sai định dạng
                System.err.println("Lỗi parse ngày tại Tour " + t.getTourID() + ": " + t.getDeparture_date());
            }
        }
        if (!found) System.out.println("No tours found earlier than current date!");
    }
    

    // --- CASE 4: Lọc Tour khởi hành SAU ngày hiện tại & Sắp xếp giảm dần ---
    public void listToursDepartureLaterAndSort() {
        System.out.println("\n--- DANH SÁCH TOUR KHỞI HÀNH SAU NGÀY HIỆN TẠI (SORTED BY TOTAL AMOUNT) ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        
        // Tạo một giỏ phụ để chứa các Tour thỏa mãn điều kiện
        List<Tour> futureTours = new ArrayList<>();

        for (Tour t : this) {
            try {
                LocalDate depDate = LocalDate.parse(t.getDeparture_date(), formatter);
                if (depDate.isAfter(currentDate)) { // So sánh SAU (Later)
                    futureTours.add(t);
                }
            } catch (Exception e) {}
        }

        if (futureTours.isEmpty()) {
            System.out.println("Không có tour nào thỏa mãn điều kiện!");
            return;
        }

        // Sắp xếp giỏ phụ GIẢM DẦN theo Total Amount (Giá x Số lượng khách)
        Collections.sort(futureTours, new Comparator<Tour>() {
            @Override
            public int compare(Tour t1, Tour t2) {
                double total1 = t1.getPrice() * t1.getNumber_Tourist();
                double total2 = t2.getPrice() * t2.getNumber_Tourist();
                return Double.compare(total2, total1); // t2 đứng trước t1 để xếp giảm dần
            }
        });

        // In kết quả
        for (Tour t : futureTours) {
            double totalAmount = t.getPrice() * t.getNumber_Tourist();
            System.out.println(t.toString() + " | Total Amount: " + totalAmount);
        }
    }
    
 
    // --- LƯU DỮ LIỆU TOUR XUỐNG FILE ---
    public void saveToFile() {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.File(pathFile))) {
            for (Tour t : this) {
                // Nối các thuộc tính bằng dấu phẩy giống hệt cấu trúc lúc đọc file
                pw.println(t.getTourID() + "," + t.getTourName() + "," + t.getTime() + "," +
                           t.getPrice() + "," + t.getHomeID() + "," + t.getDeparture_date() + "," +
                           t.getEnd_date() + "," + t.getNumber_Tourist() + "," + t.isBooking());
            }
            System.out.println("-> Đã lưu dữ liệu Tour vào file thành công!");
        } catch (Exception e) {
            System.err.println("-> Lỗi không thể lưu file Tour!");
        }
    }
    
}
