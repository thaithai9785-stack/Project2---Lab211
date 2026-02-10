
package model;

public class Booking {
    public String bookingID;
    public String fullName;
    public String tourID;
    public String bookingDate;
    public String phone;

    public Booking() {
    }

    public Booking(String bookingID, String fullName, String tourID, String bookingDate, String phone) {
        this.bookingID = bookingID;
        this.fullName = fullName;
        this.tourID = tourID;
        this.bookingDate = bookingDate;
        this.phone = phone;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getBooking_date() {
        return bookingDate;
    }

    public void setBooking_date(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public String toString() {
        return String.format("| %-6s | %-20s | %-6s | %-12s | %-12s |",
                bookingID, fullName, tourID, bookingDate, phone);
    }
    
}
