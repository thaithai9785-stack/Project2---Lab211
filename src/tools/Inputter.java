
package tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Booking;
import model.Tour;

public class Inputter {

    private Scanner ndl;

    
    public Inputter() {
        this.ndl = new Scanner(System.in);
    }

    public Scanner getNdl() {
        return ndl;
    }

    public String getString(String mess){
        System.out.print(mess);
        return this.ndl.nextLine();
      
    }
    
    //Kiểm tra dữ liệu nhập vào có hợp lệ hay không
    public String inputAndLoop(String mess, String pattern, boolean isLoop){
        boolean more = true;
        String result="";
        do{
            result = getString(mess);
            more = !Acceptable.isValid(result, pattern);
            
            if(more)
                System.out.println("Data is incorrect!");
        } while(more && isLoop);
        return result;
    }
    
    public int getInt(String mess) {
        int kq = 0;
        String tam = getString(mess);
        if (Acceptable.isValid(tam, Acceptable.INTEGER_VALID)) {
            kq = Integer.parseInt(tam);
        }
        return kq;
    }

    public double getDouble(String mess) {
        String tam = getString(mess);
        double kq=0;
        if (Acceptable.isValid(tam, Acceptable.DOUBLE_VALID))
            kq = Double.parseDouble(tam);
        return kq;
    }
    
    public String inputDate(String mess) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            try {
                String dateStr = getString(mess);
                if (!Acceptable.isValid(dateStr, Acceptable.DATE_VALID)) {
                    System.out.println("Invalid data");
                    continue;
                }
                LocalDate.parse(dateStr, dtf);
                return dateStr;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid! This date does not exist on the calendar.");
            }

        }
    }

    
  
    public Tour getTourInfo() {
        Tour x = new Tour();
      
        x.setTourID(inputAndLoop("Input Tour ID (ex: T00001): ", Acceptable.TOUR_ID_VALID, true));
        x.setTourName(inputAndLoop("Input Tour Name: ", Acceptable.NAME_VALID, true));
        x.setTime(inputAndLoop("Input Time (ex: 3 Days): ", Acceptable.TIME_VALID, true));
        
        // 2. Nhập số: Dùng INTEGER_VALID như đề yêu cầu
        String priceStr = inputAndLoop("Input Price (Positive Integer): ", Acceptable.INTEGER_VALID, true);
        x.setPrice(Double.parseDouble(priceStr));
        
        x.setHomeID(inputAndLoop("Input Homestay ID (ex: HS0001): ", Acceptable.HOMESTAY_ID_VALID, true));
        x.setDeparture_date(inputAndLoop("Input Departure Date (dd/mm/yyyy): ", Acceptable.DATE_VALID, true));
        x.setEnd_date(inputAndLoop("Input End Date (dd/mm/yyyy): ", Acceptable.DATE_VALID, true));
        
        // 3. Nhập số lượng khách
        String numStr = inputAndLoop("Input Number of Tourists: ", Acceptable.INTEGER_VALID, true);
        x.setNumber_Tourist(Integer.parseInt(numStr));
        return x;
    }
    
    public Booking getBookingInfo() {
        Booking b = new Booking();
        b.setBookingID(inputAndLoop("Input Booking ID (ex: B00001): ", Acceptable.BOOKING_ID_VALID, true));
        b.setFullName(inputAndLoop("Input Full Name: ", Acceptable.NAME_VALID, true));
        b.setTourID(inputAndLoop("Input Tour ID to book (ex: T00001): ", Acceptable.TOUR_ID_VALID, true));
        b.setBooking_date(inputAndLoop("Input Booking Date (dd/mm/yyyy): ", Acceptable.DATE_VALID, true));      
        b.setPhone(inputAndLoop("Input Phone Number (10 digits): ", Acceptable.PHONE_VALID, true));
        
        return b;
    }
    
}
