
package model;

public class Tour {
    public String tourID;
    public String tourName;
    public String time;
    public double price;
    public String homeID;
    public String departure_date;
    public String end_date;
    public int number_Tourist;
    public boolean booking;

    public Tour() {
    }

    public Tour(String tourID, String tourName, String time, double price, String homeID, String departure_date, String end_date, int number_Tourist, boolean booking) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.time = time;
        this.price = price;
        this.homeID = homeID;
        this.departure_date = departure_date;
        this.end_date = end_date;
        this.number_Tourist = number_Tourist;
        this.booking = booking;
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getHomeID() {
        return homeID;
    }

    public void setHomeID(String homeID) {
        this.homeID = homeID;
    }

    public String getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(String departure_date) {
        this.departure_date = departure_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getNumber_Tourist() {
        return number_Tourist;
    }

    public void setNumber_Tourist(int number_Tourist) {
        this.number_Tourist = number_Tourist;
    }

    public boolean isBooking() {
        return booking;
    }

    public void setBooking(boolean booking) {
        this.booking = booking;
    }


    @Override
    public String toString() {
        return String.format("| %-6s | %-15s | %-15s | %10.0f | %-6s | %-10s | %-10s | %2d | %-5s |",
                tourID, tourName, time, price, homeID, departure_date, end_date, number_Tourist, booking);
    }
}
