
package model;

public class Homestay {
    private String homeID;
    private String homeName;
    private int roomNumber;
    private String address;
    private int maximumCapacity;

    public Homestay() {
    }

    public Homestay(String homeID, String homeName, int roomNumber, String address, int maximumCapacity) {
        this.homeID = homeID;
        this.homeName = homeName;
        this.roomNumber = roomNumber;
        this.address = address;
        this.maximumCapacity = maximumCapacity;
    }
    
    public String getHomeID() {
        return homeID;
    }

    public void setHomeID(String homeID) {
        this.homeID = homeID;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMaximumcapacity() {
        return maximumCapacity;
    }

    public void setMaximumcapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }
    
    @Override
    public String toString() {
        return String.format("| %-8s | %-25s | %-5d | %-40s | %-5d |", 
                homeID, homeName, roomNumber, address, maximumCapacity);
    }
    
}
