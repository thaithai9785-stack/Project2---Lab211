package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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
    Scanner sc = new Scanner(System.in);
    private HomestayManager hsManager;
    private String pathFile;
    private final String TABLE_HEADER = ("|------------------------------------------------------------------|\n"
                + "| ID     | Tên Tour        | Thời gian       | Giá        | HomeID | Ngày đi    | Ngày về   |SL |Booking|\n"
                + "|--------|-----------------|-----------------|------------|--------|------------|-----------|---|-------|");
   
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
            if(t.getTourID().equalsIgnoreCase(id))
                return t;
        }
        return null;
    }

    public void addNew(Tour x){
        if (searchTourById(x.getTourID()) != null) {
            System.out.println("Tour đã tồn tại");
            return;
        }
        
        Homestay hs = hsManager.searchHomestayById(x.getHomeID());
        if(hs == null){
            System.out.println("ko tim thay homestay");
            return;
        }
        
        if(x.getNumber_Tourist() > hs.getMaximumcapacity()){
            System.out.println("ko du suc chua");
            return;
        }
        
        this.add(x);
        System.out.println("Add new thanh cong");
    }
    
       public void UpdateByTourId(String id) {
           Tour t = searchTourById(id);
           if(searchTourById(id)==null){
               System.out.println("This tour does not exist");
               return;
           }
           
           System.out.println("Update:");
           System.out.println("(ko muốn update, enter lần nữa)");
           
           t.setTourName(getNewData("Tour Name: ", t.getTourName()));
           t.setTime(getNewData("time: ", t.getTime()));
           
          
           boolean flag = false;
           while (flag == false) {
               System.out.print("Price: ");
               String priceS = sc.nextLine();
               if (priceS.isEmpty()) {
                   break;
               }
               try {
                   Double p = Double.parseDouble(priceS);
                   if (p < 0) {
                       System.out.println("price ko dc âm");
                   } else {
                       t.setPrice(p);
                       flag = true;
                   }
               } catch (Exception e) {
                   System.out.println("Lổi định dang");
               }

           }
        
           
           boolean flag1 = false;
           while (flag == false) {
               System.out.print("HomeID: ");
               String HomeID = sc.nextLine();
               if (HomeID.isEmpty()) {
                   break;
               }

               Homestay hs = hsManager.searchHomestayById(HomeID);
               if (hs == null) {
                   System.out.println("ko tim thay homestay");
               } else {
                   t.setHomeID(HomeID);
                   flag = true;
               }
           }
           
           
           t.setDeparture_date(getNewData("departure_date: ", t.getDeparture_date()));
           t.setEnd_date(getNewData("end_date: ", t.getEnd_date()));
        
           boolean flag3 = false;
           while (flag == false) {
               System.out.print("number_Tourist: ");
               String number_Tourist = sc.nextLine();
               if (number_Tourist.isEmpty()) {
                   break;
               }
               try {
                   int n = Integer.parseInt(number_Tourist);
                   Homestay hs = hsManager.searchHomestayById(t.getHomeID());
                   if (n> hs.getMaximumcapacity()) {
                       System.out.println("Qua suc chua");
                   } else {
                       t.setNumber_Tourist(n);
                       flag = true;
                   }
               } catch (Exception e) {
                   System.out.println("Lổi định dang");
               }
           }
           
           System.out.println("trạng thái booking cũ" + t.isBooking() +"-> update (true/false):");
           String chance = sc.nextLine();
           if(!chance.isEmpty()) t.setBooking(Boolean.parseBoolean(chance));
           System.out.println("update thanh cong");
           
    }
    
       
       public String getNewData(String mess, String OldData){
           System.out.print(mess);
           String newData = sc.nextLine();
           
           return newData.isEmpty()? OldData : newData;
       }
    
    public void listToursDepartureEarlier() {
        System.out.println("Tour trước giờ hiện tại (" + LocalDate.now() + ")");
        DateTimeFormatter fm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();
        boolean flag = false;
        System.out.println(TABLE_HEADER);
        for (Tour t : this) {
            try {
                LocalDate depDate = LocalDate.parse(t.getDeparture_date().trim(),fm);
                if (depDate.isAfter(currentDate)) {
                    System.out.println(t.toString());
                    flag = true;
                }
            } catch (Exception e) {
                System.out.println(" lỗi " +t.getDeparture_date());
            }

        }

        if (!flag) {
            System.out.println("ko co tour nao truoc ngay hien tai");
        }

    }
    

    // --- CASE 4: Lọc Tour khởi hành SAU ngày hiện tại và Sắp xếp giảm dần ---
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
            System.out.println(t.toString() + " Total Amount: " + totalAmount);
        }
    }

    
 
    // --- LƯU DỮ LIỆU TOUR XUỐNG FILE ---
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new java.io.File(pathFile))) {
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

 
    
    
//     Sắp xếp giỏ phụ GIẢM DẦN theo Total Amount (Giá x Số lượng khách)
//        Collections.sort(futureTours, new Comparator<Tour>() {
//            @Override
//            public int compare(Tour t1, Tour t2) {
//                double total1 = t1.getPrice() * t1.getNumber_Tourist();
//                double total2 = t2.getPrice() * t2.getNumber_Tourist();
//                return Double.compare(total2, total1); // t2 đứng trước t1 để xếp giảm dần
//            }
//        });
    
}
