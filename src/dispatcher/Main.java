
package dispatcher;

import java.util.Scanner;
import manager.BookingManager;
import manager.HomestayManager;
import manager.TourManager;
import model.Booking;
import model.Tour;
import tools.Inputter;

public class Main {


    public static void main(String[] args) {
        Inputter ndl = new Inputter();
        HomestayManager hsManager = new HomestayManager();
        TourManager tourManager = new TourManager(ndl ,hsManager);
        BookingManager bookingManager = new BookingManager(ndl, tourManager);
        
        Scanner sc = new Scanner(System.in);
        int choice ;
        do {
            System.out.println("\n====== HOMESTAY BOOKING MANAGEMENT ======");
            System.out.println("1. Add a new Tour");
            System.out.println("2. Update a Tour by ID");
            System.out.println("3. List the Tours with departure dates earlier than the current date");
            System.out.println("4. List the total Booking amount for tours with departure dates later than the current date");
            System.out.println("5. Add a new Booking");
            System.out.println("6. Remove a Booking by bookingID");
            System.out.println("7. Update a Booking by bookingID");
            System.out.println("8. List all Booking by the fullName or a partial fullName");
            System.out.println("9. Statistics on the total number of tourists who have booked homestays");
            System.out.println("10. Quit program");
            System.out.print("Enter your choice (1-10): ");
            choice= sc.nextInt();
            sc.nextLine();
        switch (choice) {
                case 1:
                    tourManager.addNew();
                    break;
                case 2:
                    tourManager.UpdateByTourId();
                    break;
                case 3:
                    tourManager.listToursDepartureEarlier();
                    break;
                case 4:
                    tourManager.listToursDepartureLaterAndSort();
                    break;
                case 5:
                    bookingManager.addNewBooking();
                    break;
                case 6:
                    bookingManager.removeBookingById();
                    break;
                case 7:
                    bookingManager.updateBookingById();
                    break;
                case 8:
                    bookingManager.listBookingsByName();
                    break;
                case 9:
                    bookingManager.printStatistics(hsManager, tourManager); 
                    break;
                case 10:
                    System.out.print("\nBạn có muốn lưu mọi thay đổi vào file trước khi thoát không? (Y/N): ");
                    String confirm = sc.nextLine().trim();
                    if (confirm.equalsIgnoreCase("Y")) {
                        tourManager.saveToFile();
                        bookingManager.saveToFile();
                    }
                    System.out.println("\nCảm ơn bạn đã sử dụng hệ thống HOMESTAY BOOKING MANAGEMENT. Tạm biệt!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập từ 1 đến 10.");
            }
        } while (choice != 10);
    }
    
}
