package com.example.iot_proj.ui;


public class Book {
    public int getChair() {
        return chair;
    }
    public int getFloor() {
        return floor;
    }
    public String getDate() {
        return date;
    }
    public String getUserId() {
        return userId;
    }
    public String userId ;
    public String date;
    public int floor;
    public int chair;
    public Book(String userId, String date, int floor, int chair){
        this.userId = userId;
        this.date = date;
        this.floor = floor;
        this.chair = chair;
    }
}
