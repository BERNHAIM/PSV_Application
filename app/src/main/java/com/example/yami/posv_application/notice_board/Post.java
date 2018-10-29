package com.example.yami.posv_application.notice_board;

import java.util.Date;

public class Post {

    String postNum;
    String postName;
    String contents;
    String userID;
    Date dateTime;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentTime;

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public Post(String postNum, String postName, String contents, String userID, String currentTime){
        this.postNum = postNum;
        this.postName = postName;
        this.contents = contents;
        this.userID = userID;
        this.currentTime = currentTime;
    }
}
