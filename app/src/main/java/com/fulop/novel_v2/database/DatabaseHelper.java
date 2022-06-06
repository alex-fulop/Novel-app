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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.NovelUser;

import java.util.ArrayList;
import java.util.List;

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

        if (user.getFollowUsers() != null)
            user.getFollowUsers().forEach(followUser -> {
                cv.put(USERS_FOLLOW_USERS_USER_ID, userId);
                cv.put(USERS_FOLLOW_USERS_FOLLOW_USER, followUser);

                db.insert(TABLE_USERS_FOLLOW_USERS, null, cv);
                cv.clear();
            });

        if (user.getFollowHashtags() != null)
            user.getFollowHashtags().forEach(hashtag -> {
                cv.put(USERS_FOLLOW_HASHTAGS_ID, userId);
                cv.put(USERS_FOLLOW_HASHTAGS_HASHTAG, hashtag);

                db.insert(TABLE_USERS_FOLLOW_USERS, null, cv);
                cv.clear();
            });
    }

    public void updateNovel(Novel novel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOVELS_ID, novel.getNovelId());
        cv.put(NOVELS_USERNAME, novel.getUsername());
        cv.put(NOVELS_TEXT, novel.getText());
        cv.put(NOVELS_IMAGE_URL, novel.getImageUrl());
        cv.put(NOVELS_TIMESTAMP, novel.getTimestamp());

        db.update(TABLE_NOVELS, cv, NOVELS_ID + "=?", new String[]{novel.getNovelId()});
        cv.clear();

        novel.getUserIds().forEach(userId -> {
            cv.put(NOVELS_USER_IDS_USER_ID, userId);

            db.update(TABLE_NOVELS_USER_IDS, cv, NOVELS_USER_IDS_NOVEL_ID + "=?", new String[]{novel.getNovelId()});
            cv.clear();
        });

        novel.getHashtags().forEach(hashtag -> {
            cv.put(NOVELS_HASHTAGS_HASHTAG, hashtag);

            db.update(TABLE_NOVELS_HASHTAGS, cv, NOVELS_HASHTAGS_NOVEL_ID + "=?", new String[]{novel.getNovelId()});
            cv.clear();
        });

        novel.getLikes().forEach(like -> {
            cv.put(NOVELS_LIKES_USER_LIKE, like);

            db.update(TABLE_NOVELS_LIKES, cv, NOVELS_LIKES_NOVEL_ID + "=?", new String[]{novel.getNovelId()});
            cv.clear();
        });
    }

    public void onNovelLike(String novelId, String userId) {
        List<String> novelLikes = getNovelLikes(novelId);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (!novelLikes.contains(userId)) {
            cv.put(NOVELS_LIKES_NOVEL_ID, novelId);
            cv.put(NOVELS_LIKES_USER_LIKE, userId);

            db.insert(TABLE_NOVELS_LIKES, null, cv);
        } else
            db.delete(TABLE_NOVELS_LIKES, NOVELS_LIKES_NOVEL_ID + " =? AND " +
                    NOVELS_LIKES_USER_LIKE + "=?", new String[]{novelId, userId});
        cv.clear();
    }

    public void onNovelShare(String novelId, String userId) {
        List<String> novelLikes = getNovelShares(novelId);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (!novelLikes.contains(userId)) {
            cv.put(NOVELS_USER_IDS_NOVEL_ID, novelId);
            cv.put(NOVELS_USER_IDS_USER_ID, userId);

            db.insert(TABLE_NOVELS_USER_IDS, null, cv);
        } else
            db.delete(TABLE_NOVELS_USER_IDS, NOVELS_USER_IDS_NOVEL_ID + " =? AND " +
                    NOVELS_USER_IDS_USER_ID + "=?", new String[]{novelId, userId});
        cv.clear();
    }

    public void updateUser(NovelUser user, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USERS_USERNAME, user.getUsername());
        cv.put(USERS_EMAIL, user.getEmail());
        cv.put(USERS_IMAGE_URL, user.getImageUrl());

        db.update(TABLE_USERS, cv, USERS_ID + "=?", new String[]{userId});
        cv.clear();

        user.getFollowUsers().forEach(followUser -> {
            cv.put(USERS_FOLLOW_USERS_FOLLOW_USER, followUser);

            db.update(TABLE_USERS_FOLLOW_USERS, cv, USERS_FOLLOW_USERS_USER_ID + "=?", new String[]{userId});
            cv.clear();
        });

        user.getFollowHashtags().forEach(hashtag -> {
            cv.put(USERS_FOLLOW_HASHTAGS_HASHTAG, hashtag);

            db.update(TABLE_USERS_FOLLOW_HASHTAGS, cv, USERS_FOLLOW_HASHTAGS_USER_ID + "=?", new String[]{userId});
            cv.clear();
        });
    }

    public List<Novel> findNovelsByHashtag(String hashtag) {
        List<String> novelIds = getNovelsByHashtag(hashtag);
        return getNovelsByIds(novelIds);
    }

    public List<Novel> findNovelsByFollowedUser(String followedUserId) {
        List<String> novelIds = getNovelsByFollowedUserId(followedUserId);
        return getNovelsByIds(novelIds);
    }

    public List<Novel> findNovelsForSearchTerm(String searchTerm) {
        List<String> novelIds = getNovelIdsForSearchedTerm(searchTerm);
        return getNovelsByIds(novelIds);
    }

//    public List<Novel> findNovelsById(String novelId) {
//        List<String> novelIds = getNovelIdsForSearchedTerm(novelId);
//        return getNovelsByIds(novelIds);
//    }

    public List<Novel> findNovelsForUserId(String userId) {
        List<String> novelIds = getNovelIdsForUserId(userId);
        return getNovelsByIds(novelIds);
    }

    private List<Novel> getNovelsByIds(List<String> novelIds) {
        novelIds.replaceAll(id -> "\"" + id + "\"");
        String query = "SELECT * FROM " + TABLE_NOVELS +
                " WHERE " + NOVELS_ID +
                " IN (" + TextUtils.join(", ", novelIds) + ");";

        List<Novel> novels = new ArrayList<>();
        Cursor cursor = getCursorForQuery(query);
        if (cursor != null) {
            novels = getNovelsUsingCursor(cursor);
            cursor.close();
        }
        return novels;
    }

    public List<String> getNovelLikes(String novelId) {
        String query = "SELECT " + NOVELS_LIKES_USER_LIKE +
                " FROM " + TABLE_NOVELS_LIKES +
                " WHERE " + NOVELS_LIKES_NOVEL_ID +
                " = " + "\"" + novelId + "\"";

        return getNovelIdsForQuery(query);
    }

    public List<String> getNovelShares(String novelId) {
        String query = "SELECT " + NOVELS_USER_IDS_USER_ID +
                " FROM " + TABLE_NOVELS_USER_IDS +
                " WHERE " + NOVELS_USER_IDS_NOVEL_ID +
                " = " + "\"" + novelId + "\"";

        return getNovelIdsForQuery(query);
    }

    public List<String> getNovelIdsForUserId(String userId) {
        String query = "SELECT " + NOVELS_USER_IDS_NOVEL_ID +
                " FROM " + TABLE_NOVELS_USER_IDS +
                " WHERE " + NOVELS_USER_IDS_USER_ID + " = \"" + userId + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getUserIdsForNovel(String novelId) {
        String query = "SELECT " + NOVELS_USER_IDS_USER_ID +
                " FROM " + TABLE_NOVELS_USER_IDS +
                " WHERE " + NOVELS_USER_IDS_NOVEL_ID + " = \"" + novelId + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getLikesForNovel(String novelId) {
        String query = "SELECT " + NOVELS_LIKES_USER_LIKE +
                " FROM " + TABLE_NOVELS_LIKES +
                " WHERE " + NOVELS_LIKES_NOVEL_ID + " = \"" + novelId + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getHashtagsForNovel(String novelId) {
        String query = "SELECT " + NOVELS_HASHTAGS_HASHTAG +
                " FROM " + TABLE_NOVELS_HASHTAGS +
                " WHERE " + NOVELS_HASHTAGS_NOVEL_ID + " = \"" + novelId + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getNovelIdsForSearchedTerm(String searchTerm) {
        String query = "SELECT " + NOVELS_HASHTAGS_NOVEL_ID +
                " FROM " + TABLE_NOVELS_HASHTAGS +
                " WHERE " + NOVELS_HASHTAGS_HASHTAG + " = \"" + searchTerm + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getNovelsByFollowedUserId(String followedUserId) {
        String query = "SELECT " + NOVELS_USER_IDS_NOVEL_ID +
                " FROM " + TABLE_NOVELS_USER_IDS +
                " WHERE " + NOVELS_USER_IDS_USER_ID +
                " = \"" + followedUserId + "\"";

        return getNovelIdsForQuery(query);
    }

    private List<String> getNovelsByHashtag(String hashtag) {
        String query = "SELECT " + NOVELS_HASHTAGS_NOVEL_ID +
                " FROM " + TABLE_NOVELS_HASHTAGS +
                " WHERE " + NOVELS_HASHTAGS_HASHTAG +
                " = \"" + hashtag + "\"";

        return getNovelIdsForQuery(query);
    }

//    private List<String> getNovelById(String novelId) {
//        String query = "SELECT " + NOVELS_HASHTAGS_NOVEL_ID +
//                " FROM " + TABLE_NOVELS_HASHTAGS +
//                " WHERE " + NOVELS_HASHTAGS_HASHTAG + " = \"" + novelId + "\"";
//
//        return getNovelIdsForQuery(query);
//    }

    @NonNull
    private List<String> getNovelIdsForQuery(String query) {
        List<String> novelIds = new ArrayList<>();
        Cursor cursor = getCursorForQuery(query);
        if (cursor != null) {
            while (cursor.moveToNext()) novelIds.add(cursor.getString(0));
            cursor.close();
        }
        return novelIds;
    }

    @NonNull
    private List<Novel> getNovelsUsingCursor(Cursor cursor) {
        List<Novel> novels = new ArrayList<>();

        while (cursor.moveToNext()) {
            Novel novel = new Novel();
            novel.setNovelId(cursor.getString(0));
            novel.setText(cursor.getString(1));
            novel.setUsername(cursor.getString(2));
            novel.setImageUrl(cursor.getString(3));
            novel.setTimestamp(Long.valueOf(cursor.getString(4)));
            novel.setLikes(getLikesForNovel(cursor.getString(0)));
            novel.setUserIds(getUserIdsForNovel(cursor.getString(0)));
            novel.setHashtags(getHashtagsForNovel(cursor.getString(0)));
            novels.add(novel);
        }
        return novels;
    }

    @Nullable
    private Cursor getCursorForQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) cursor = db.rawQuery(query, null);
        return cursor;
    }
}
