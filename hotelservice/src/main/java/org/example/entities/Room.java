package org.example.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hotelId;
    private String number;
    private Boolean available = true;
    private Integer timesBooked = 0;
    private String lastRequestId;
    private String lastReleaseRequestId;
    public Room() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public Integer getTimesBooked() { return timesBooked; }
    public void setTimesBooked(Integer timesBooked) { this.timesBooked = timesBooked; }
    public String getLastRequestId() { return lastRequestId; }
    public void setLastRequestId(String lastRequestId) { this.lastRequestId = lastRequestId; }
    public String getLastReleaseRequestId() { return lastReleaseRequestId; }
    public void setLastReleaseRequestId(String lastReleaseRequestId) { this.lastReleaseRequestId = lastReleaseRequestId; }
}