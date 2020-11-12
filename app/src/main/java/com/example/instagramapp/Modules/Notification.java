package com.example.instagramapp.Modules;

public class Notification
{
    String userId,postId,text;
    boolean fromPost;

    public boolean isFromPost() {
        return fromPost;
    }

    public void setFromPost(boolean fromPost) {
        this.fromPost = fromPost;
    }

    public Notification() {

    }

    public Notification(String userId, String postId, String text, boolean fromPost) {
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.fromPost = fromPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
