
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

public class Inputter {

    private Scanner ndl;

    public Inputter() {
        this.ndl = new Scanner(System.in);
    }

    public Scanner getNdl() {
        return ndl;
    }

    public String getString(mess){
        String input;
        System.out.print(mess);
        return this.ndl.nextLine();
        // Kiểm tra xem có rỗng không
        if (input.isEmpty()) {
            System.out.println("Invallid data. Try again:");
        } else {
            return input; 
        }
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

}
