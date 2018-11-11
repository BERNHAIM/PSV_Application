package com.example.yami.posv_application.admin;

public class dangerArea {

    String subject;
    String location_x;
    String location_y;


    public String subject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLocationX() { return location_x; }

    public void setLocationX(String location_x) {
        this.location_x = location_x;
    }

    public String getLocationY() { return location_y; }

    public void setLocationY(String location_y) {
        this.location_y = location_y;
    }

    public dangerArea(String subject, String location_x, String location_y){
        this.subject = subject;
        this.location_x = location_x;
        this.location_y = location_y;
    }


}
