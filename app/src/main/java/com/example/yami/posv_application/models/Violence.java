package com.example.yami.posv_application.models;

import android.graphics.Bitmap;

public class Violence {

    public String newsSubject;
    public String description;
    public String upTodate;
    public String area;
    public Bitmap logo;


    public Violence(String newsSubject,
                    String description,
                    String upTodate,
                    String area)
    {
        this.newsSubject = newsSubject;
        this.description = description;
        this.upTodate = upTodate;
        this.area = area;
    }
}
