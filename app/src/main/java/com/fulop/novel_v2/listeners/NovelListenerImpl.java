package com.fulop.novel_v2.listeners;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_LIKES;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_FOLLOW;
import static com.fulop.novel_v2.util.Utils.isNetworkAvailable;

import android.app.AlertDialog;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.fulop.novel_v2.database.DatabaseHelper;
import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.NovelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class NovelListenerImpl implements NovelListener {

    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getUid();


    private RecyclerView novelList;
    private NovelUser user;
    private HomeCallback callback;

    public NovelListenerImpl(RecyclerView novelList, NovelUser user, HomeCallback callback) {
        this.novelList = novelList;
        this.user = user;
        this.callback = callback;
    }

    @Override
    public void onLayoutClick(Novel novel) {
        if (novel != null) {
            String ownerId = novel.getUserIds().get(0);
            if (!Objects.equals(userId, ownerId)) {
                if (isNetworkAvailable(novelList.getContext())) {
                    if (user.getFollowUsers() == null) user.setFollowUsers(new ArrayList<>());
                    boolean isOwnerFollowingUser = user.getFollowUsers().contains(ownerId);
                    displayAlertDialog(isOwnerFollowingUser, novel, ownerId);
                }
            }
        }
    }

    @Override
    public void onLike(Novel novel) {
        if (novel != null) {
            novelList.setClickable(false);
            if (isNetworkAvailable(novelList.getContext())) {
                if (novel.getLikes().contains(userId)) novel.getLikes().remove(userId);
                else novel.getLikes().add(userId);

                firebaseDB.collection(DATA_NOVELS)
                        .document(novel.getNovelId())
                        .update(DATA_NOVEL_LIKES, novel.getLikes())
                        .addOnSuccessListener(unused -> {
                            callback.onRefresh();
                            novelList.setClickable(true);
                        });
            } else {
                DatabaseHelper db = new DatabaseHelper(novelList.getContext());
                db.onNovelLike(novel.getNovelId(), userId);
                callback.onRefresh();
                novelList.setClickable(true);
            }
        }
    }

    @Override
    public void onShare(Novel novel) {
        if (novel != null) {
            novelList.setClickable(false);
            if (isNetworkAvailable(novelList.getContext())) {
                if (novel.getUserIds().contains(userId)) novel.getUserIds().remove(userId);
                else novel.getUserIds().add(userId);

                firebaseDB.collection(DATA_NOVELS)
                        .document(novel.getNovelId())
                        .update(DATA_NOVEL_USER_IDS, novel.getUserIds())
                        .addOnSuccessListener(unused -> {
                            callback.onRefresh();
                            novelList.setClickable(true);
                        });
            } else {
                DatabaseHelper db = new DatabaseHelper(novelList.getContext());
                db.onNovelShare(novel.getNovelId(), userId);
                callback.onRefresh();
                novelList.setClickable(true);
            }
        }
    }

    private void displayAlertDialog(boolean isOwnerFollowingUser, Novel novel, String ownerId) {
        new AlertDialog.Builder(novelList.getContext())
                .setTitle(String.format(isOwnerFollowingUser ? "Unfollow %s?" : "Follow %s?", novel.getUsername()))
                .setPositiveButton("Yes", (dialog, which) -> {
                    novelList.setClickable(false);
                    if (isOwnerFollowingUser) user.getFollowUsers().remove(ownerId);
                    else user.getFollowUsers().add(ownerId);
                    updateFollowedUsers(isOwnerFollowingUser);
                }).setNegativeButton("Cancel", ((dialog, which) -> {
        })).show();
    }

    private void updateFollowedUsers(boolean followed) {
        firebaseDB.collection(DATA_USERS)
                    .document(userId)
                    .update(DATA_USER_FOLLOW, user.getFollowUsers());

        novelList.setClickable(true);
        callback.onUserUpdated();
        Toast.makeText(novelList.getContext(), followed ?
                "User unfollowed successfully" : "User followed successfully", Toast.LENGTH_SHORT).show();
    }

    public RecyclerView getNovelList() {
        return novelList;
    }

    public void setNovelList(RecyclerView novelList) {
        this.novelList = novelList;
    }

    public NovelUser getUser() {
        return user;
    }

    public void setUser(NovelUser user) {
        this.user = user;
    }

    public HomeCallback getCallback() {
        return callback;
    }

    public void setCallback(HomeCallback callback) {
        this.callback = callback;
    }
}
