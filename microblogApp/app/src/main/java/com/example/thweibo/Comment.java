package com.example.thweibo;

public class Comment {
    private int mCommentID;
    private int mBlogID;
    private String mUsername;
    private String mContent;
    private String mCommentTime;

    public Comment() { }

    public void setCommentId(int commentId) {
        mCommentID = commentId;
    }

    public void setBlogId(int blogId) {
        mBlogID = blogId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String userName) {
        mUsername = userName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCommentTime() {
        return mCommentTime;
    }

    public void setCommentTime(String commentTime) {
        mCommentTime = commentTime;
    }
}
