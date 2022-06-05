package com.fulop.novel_v2.database;

import static com.fulop.novel_v2.util.Constants.DATABASE_NAME;
import static com.fulop.novel_v2.util.Constants.DATABASE_VERSION;
import static com.fulop.novel_v2.util.Constants.NOVELS_HASHTAGS_HASHTAG;
import static com.fulop.novel_v2.util.Constants.NOVELS_HASHTAGS_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_HASHTAGS_NOVEL_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_IMAGE_URL;
import static com.fulop.novel_v2.util.Constants.NOVELS_LIKES_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_LIKES_NOVEL_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_LIKES_USER_LIKE;
import static com.fulop.novel_v2.util.Constants.NOVELS_TEXT;
import static com.fulop.novel_v2.util.Constants.NOVELS_TIMESTAMP;
import static com.fulop.novel_v2.util.Constants.NOVELS_USERNAME;
import static com.fulop.novel_v2.util.Constants.NOVELS_USER_IDS_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_USER_IDS_NOVEL_ID;
import static com.fulop.novel_v2.util.Constants.NOVELS_USER_IDS_USER_ID;
import static com.fulop.novel_v2.util.Constants.TABLE_NOVELS;
import static com.fulop.novel_v2.util.Constants.TABLE_NOVELS_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.TABLE_NOVELS_LIKES;
import static com.fulop.novel_v2.util.Constants.TABLE_NOVELS_USER_IDS;
import static com.fulop.novel_v2.util.Constants.TABLE_USERS;
import static com.fulop.novel_v2.util.Constants.TABLE_USERS_FOLLOW_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.TABLE_USERS_FOLLOW_USERS;
import static com.fulop.novel_v2.util.Constants.USERS_EMAIL;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_HASHTAGS_HASHTAG;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_HASHTAGS_ID;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_HASHTAGS_USER_ID;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_USERS_FOLLOW_USER;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_USERS_ID;
import static com.fulop.novel_v2.util.Constants.USERS_FOLLOW_USERS_USER_ID;
import static com.fulop.novel_v2.util.Constants.USERS_ID;
import static com.fulop.novel_v2.util.Constants.USERS_IMAGE_URL;
import static com.fulop.novel_v2.util.Constants.USERS_USERNAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.NovelUser;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNovelsTable = "CREATE TABLE " + TABLE_NOVELS + " (" +
                NOVELS_ID + " TEXT PRIMARY KEY NOT NULL, " +
                NOVELS_TEXT + " TEXT, " +
                NOVELS_USERNAME + " TEXT, " +
                NOVELS_IMAGE_URL + " TEXT, " +
                NOVELS_TIMESTAMP + " INTEGER);";

        String createNovelUserIdsTable = "CREATE TABLE " + TABLE_NOVELS_USER_IDS + " (" +
                NOVELS_USER_IDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                NOVELS_USER_IDS_NOVEL_ID + " INTEGER, " +
                NOVELS_USER_IDS_USER_ID + " TEXT, " +
                "\n FOREIGN KEY(" + NOVELS_USER_IDS_NOVEL_ID + ") " +
                "REFERENCES " + TABLE_NOVELS + "(" + NOVELS_ID + "));";

        String createNovelHashtagsTable = "CREATE TABLE " + TABLE_NOVELS_HASHTAGS + " (" +
                NOVELS_HASHTAGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                NOVELS_HASHTAGS_NOVEL_ID + " INTEGER, " +
                NOVELS_HASHTAGS_HASHTAG + " TEXT, " +
                "\n FOREIGN KEY(" + NOVELS_HASHTAGS_NOVEL_ID + ") " +
                "REFERENCES " + TABLE_NOVELS + "(" + NOVELS_ID + "));";

        String createNovelLikesTable = "CREATE TABLE " + TABLE_NOVELS_LIKES + " (" +
                NOVELS_LIKES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                NOVELS_LIKES_NOVEL_ID + " INTEGER, " +
                NOVELS_LIKES_USER_LIKE + " TEXT, " +
                "\n FOREIGN KEY(" + NOVELS_LIKES_NOVEL_ID + ") " +
                "REFERENCES " + TABLE_NOVELS + "(" + NOVELS_ID + "));";


        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                USERS_USERNAME + " TEXT, " +
                USERS_EMAIL + " TEXT, " +
                USERS_IMAGE_URL + " TEXT); ";

        String createUserFollowUsersTable = "CREATE TABLE " + TABLE_USERS_FOLLOW_USERS + " (" +
                USERS_FOLLOW_USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                USERS_FOLLOW_USERS_USER_ID + " INTEGER, " +
                USERS_FOLLOW_USERS_FOLLOW_USER + " TEXT, " +
                "\n FOREIGN KEY(" + USERS_FOLLOW_USERS_USER_ID + ") " +
                "REFERENCES " + TABLE_USERS + "(" + USERS_ID + "));";

        String createUserFollowHashtagsTable = "CREATE TABLE " + TABLE_USERS_FOLLOW_HASHTAGS + " (" +
                USERS_FOLLOW_HASHTAGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                USERS_FOLLOW_HASHTAGS_USER_ID + " INTEGER, " +
                USERS_FOLLOW_HASHTAGS_HASHTAG + " TEXT, " +
                "\n FOREIGN KEY(" + USERS_FOLLOW_USERS_USER_ID + ") " +
                "REFERENCES " + TABLE_USERS + "(" + USERS_ID + "));";

        db.execSQL(createNovelsTable);
        db.execSQL(createNovelUserIdsTable);
        db.execSQL(createNovelLikesTable);
        db.execSQL(createNovelHashtagsTable);

        db.execSQL(createUsersTable);
        db.execSQL(createUserFollowHashtagsTable);
        db.execSQL(createUserFollowUsersTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOVELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOVELS_LIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOVELS_HASHTAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOVELS_USER_IDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_FOLLOW_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_FOLLOW_HASHTAGS);
        onCreate(db);
    }

    public void addNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOVELS_ID, novel.getNovelId());
        cv.put(NOVELS_USERNAME, novel.getUsername());
        cv.put(NOVELS_TEXT, novel.getText());
        cv.put(NOVELS_IMAGE_URL, novel.getImageUrl());
        cv.put(NOVELS_TIMESTAMP, novel.getTimestamp());

        db.insert(TABLE_NOVELS, null, cv);
        cv.clear();

        novel.getUserIds().forEach(userId -> {
            cv.put(NOVELS_USER_IDS_NOVEL_ID, novel.getNovelId());
            cv.put(NOVELS_USER_IDS_USER_ID, userId);

            db.insert(TABLE_NOVELS_USER_IDS, null, cv);
            cv.clear();
        });

        novel.getHashtags().forEach(hashtag -> {
            cv.put(NOVELS_HASHTAGS_NOVEL_ID, novel.getNovelId());
            cv.put(NOVELS_HASHTAGS_HASHTAG, hashtag);

            db.insert(TABLE_NOVELS_HASHTAGS, null, cv);
            cv.clear();
        });

        novel.getLikes().forEach(like -> {
            cv.put(NOVELS_LIKES_NOVEL_ID, novel.getNovelId());
            cv.put(NOVELS_LIKES_USER_LIKE, like);

            db.insert(TABLE_NOVELS_LIKES, null, cv);
            cv.clear();
        });
    }

    public void addUser(NovelUser user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USERS_USERNAME, user.getUsername());
        cv.put(USERS_EMAIL, user.getEmail());
        cv.put(USERS_IMAGE_URL, user.getImageUrl());

        long userId = db.insert(TABLE_USERS, null, cv);
        cv.clear();

        user.getFollowUsers().forEach(followUser -> {
            cv.put(USERS_FOLLOW_USERS_USER_ID, userId);
            cv.put(USERS_FOLLOW_USERS_FOLLOW_USER, followUser);

            db.insert(TABLE_USERS_FOLLOW_USERS, null, cv);
            cv.clear();
        });

        user.getFollowHashtags().forEach(hashtag -> {
            cv.put(USERS_FOLLOW_HASHTAGS_ID, userId);
            cv.put(USERS_FOLLOW_HASHTAGS_HASHTAG, hashtag);

            db.insert(TABLE_USERS_FOLLOW_USERS, null, cv);
            cv.clear();
        });
    }
}
