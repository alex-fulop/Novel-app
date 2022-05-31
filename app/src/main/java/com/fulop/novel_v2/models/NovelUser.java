package com.fulop.novel_v2.models;

import java.util.List;

public class NovelUser {
    private String username;
    private String email;
    private String imageUrl;
    private List<String> followHashtags;
    private List<String> followUsers;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getFollowHashtags() {
        return followHashtags;
    }

    public void setFollowHashtags(List<String> followHashtags) {
        this.followHashtags = followHashtags;
    }

    public List<String> getFollowUsers() {
        return followUsers;
    }

    public void setFollowUsers(List<String> followUsers) {
        this.followUsers = followUsers;
    }
}
