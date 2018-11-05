package com.example.yami.posv_application.notice_board;

public class Comment {

    String commentNum;
    String postNum;
    String userID;
    String comment;
    String time;

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Comment(String commentNum, String postNum, String userID, String comment, String time){
        this.commentNum = commentNum;
        this.postNum = postNum;
        this.userID = userID;
        this.comment = comment;
        this.time = time;
    }


}
