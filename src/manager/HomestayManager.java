package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Homestay;


public class HomestayManager extends ArrayList<Homestay> {
    
    private final String pathFile = "./Homestays.txt"; 

    public HomestayManager() {
        super();
        this.readFromFile();
    }

  
    public void readFromFile() {
        this.clear(); 
        File file = new File(pathFile);
        
        if (!file.exists()) {
            System.err.println("Cảnh báo: Không tìm thấy file " + pathFile);
            return;
        }


        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Nằm trong vòng lặp while đọc file của HomestayManager:

                if (line.trim().isEmpty()) continue; 
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
             
                Homestay h = textToHomestay(line);
                if (h != null) {
                    this.add(h);
                }
            }
            System.out.println("Đã tải thành công " + this.size() + " homestays!");
        } catch (Exception ex) {
            Logger.getLogger(HomestayManager.class.getName()).log(Level.SEVERE, "Lỗi đọc file Homestay", ex);
        }
    }


    private Homestay textToHomestay(String line) {
        try {
            String[] parts = line.split("-");
            if (parts.length >= 5) {
                String id = parts[0].replaceAll("[^a-zA-Z0-9]", "");
                String name = parts[1].trim();

                int rooms = Integer.parseInt(parts[2].trim().replaceAll("[^0-9]", ""));

                StringBuilder address = new StringBuilder();
                for (int i = 3; i < parts.length - 1; i++) {
                    address.append(parts[i]);
                    if (i < parts.length - 2) address.append("-");
                }

                int capacity = Integer.parseInt(parts[parts.length - 1].trim().replaceAll("[^0-9]", ""));
                
                return new Homestay(id, name, rooms, address.toString().trim(), capacity);
            }
        } catch (Exception e) {
            System.err.println("Lỗi định dạng dòng: " + line);
        }
        return null; // Trả về null nếu dòng dữ liệu bị lỗi
    }

   public Homestay searchHomestayById1(String id) {
        if (id == null) return null;
        
        String cleanInputId = id.replaceAll("[^a-zA-Z0-9]", "");

        for (Homestay h : this) {
            if (h.getHomeID() != null) {
                String cleanListId = h.getHomeID().replaceAll("[^a-zA-Z0-9]", "");
                
                if (cleanListId.equalsIgnoreCase(cleanInputId)) {
                    return h;
                }
            }
        }
        return null;
    }
   
   public Homestay searchHomestayById(String id){
       for (Homestay h : this) {
           if(h.getHomeID().trim().equalsIgnoreCase(id.trim())){
            return h;
           }
       }
       return null;
   }
   
   
}