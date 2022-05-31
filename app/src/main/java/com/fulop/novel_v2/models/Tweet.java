package com.fulop.novel_v2.models;

import com.google.gson.JsonObject;

public class Tweet {

    private String createdAt;
    private String idStr;
    private String text;

    public Tweet(String createdAt, String idStr, String text) {
        this.createdAt = createdAt;
        this.idStr = idStr;
        this.text = text;
    }

    public static Tweet fromJsonObject(JsonObject jsonObject) {
        return new Tweet(
                jsonObject.get("created_at").getAsString(),
                jsonObject.get("id_str").getAsString(),
                jsonObject.get("text").getAsString());
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getText() {
        return text;
    }
}
