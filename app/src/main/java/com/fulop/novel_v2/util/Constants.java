package com.fulop.novel_v2.util;

public class Constants {

    //Firebase
    public static final String DATA_USERS = "Users";
    public static final String DATA_USER_USERNAME = "username";
    public static final String DATA_USER_EMAIL = "email";
    public static final String DATA_USER_IMAGE_URL = "imageUrl";
    public static final String DATA_USER_HASHTAGS = "followHashtags";
    public static final String DATA_USER_FOLLOW = "followUsers";
    public static final String DATA_PICTURE = "profilePicture";
    public static final String DATA_NOVELS = "Novels";
    public static final String DATA_NOVEL_USER_IDS = "userIds";
    public static final String DATA_NOVEL_HASHTAGS = "hashtags";
    public static final String DATA_NOVEL_LIKES = "likes";
    public static final String DATA_NOVEL_IMAGES = "novelImages";

    //SQLite DB
    public static final String DATABASE_NAME = "Novel.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOVELS = "NOVELS";
    public static final String NOVELS_ID = "NOVEL_ID";
    public static final String NOVELS_USERNAME = "USERNAME";
    public static final String NOVELS_TEXT = "TEXT";
    public static final String NOVELS_IMAGE_URL = "IMAGE_URL";
    public static final String NOVELS_TIMESTAMP = "TIMESTAMP";

    public static final String TABLE_USERS = "USERS";
    public static final String USERS_ID = "USER_ID";
    public static final String USERS_USERNAME = "USERNAME";
    public static final String USERS_EMAIL = "EMAIL";
    public static final String USERS_IMAGE_URL = "IMAGE_URL";

    public static final String TABLE_NOVELS_USER_IDS = "NOVEL_USER_IDS";
    public static final String NOVELS_USER_IDS_ID = "ID";
    public static final String NOVELS_USER_IDS_NOVEL_ID = "NOVEL_ID";
    public static final String NOVELS_USER_IDS_USER_ID = "USER_ID";

    public static final String TABLE_NOVELS_HASHTAGS = "NOVEL_HASHTAGS";
    public static final String NOVELS_HASHTAGS_ID = "ID";
    public static final String NOVELS_HASHTAGS_NOVEL_ID = "NOVEL_ID";
    public static final String NOVELS_HASHTAGS_HASHTAG = "HASHTAG";

    public static final String TABLE_NOVELS_LIKES = "NOVEL_LIKES";
    public static final String NOVELS_LIKES_ID = "ID";
    public static final String NOVELS_LIKES_NOVEL_ID = "NOVEL_ID";
    public static final String NOVELS_LIKES_USER_LIKE = "USER_LIKE";

    public static final String TABLE_USERS_FOLLOW_USERS = "USER_FOLLOW_USERS";
    public static final String USERS_FOLLOW_USERS_ID = "ID";
    public static final String USERS_FOLLOW_USERS_USER_ID = "NOVEL_ID";
    public static final String USERS_FOLLOW_USERS_FOLLOW_USER = "FOLLOW_USER";

    public static final String TABLE_USERS_FOLLOW_HASHTAGS = "USER_FOLLOW_HASHTAGS";
    public static final String USERS_FOLLOW_HASHTAGS_ID = "ID";
    public static final String USERS_FOLLOW_HASHTAGS_USER_ID = "NOVEL_ID";
    public static final String USERS_FOLLOW_HASHTAGS_HASHTAG = "HASHTAG";

    //Twitter API V2
    public static final String CONSUMER_KEY = "peZSfUKnx0gvgwvyMrMi4ZdU5";
    public static final String CONSUMER_SECRET = "8Jhoy7nLFnTlgvroXjGgn9VxnTcOQdnISSm37vZG27kVveEySD";
    public static final String ACCESS_TOKEN = "1530890092830662656-SSIhQYvDBCdLict4KPsGgxxZbi5w88";
    public static final String ACCESS_TOKEN_SECRET = "hRoedhSgPjzT060D5WndsAVnzndRPu9l0UIvlzoOhxHDp";
    public static final String BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAE94dAEAAAAAM0OoUhahr9ZRnWDfOFVySi7x7AU%3DIlSJmfeow0bjYepvuhAgWtx10uQ5cUxP8eo62qfHt26OVREk1b";

    public static final String NEWS_QUERY = " -is:reply -is:retweet (from:Telegraph" +
            " OR from:CNBC" +
            " OR from:CBCNews" +
            " OR from:guardiannews" +
            " OR from:France24_en" +
            " OR from:Newsweek" +
            " OR from:SkyNews" +
            " OR from:SkyNewsBreak" +
            " OR from:Independent" +
            " OR from:AJEnglish)";
}
