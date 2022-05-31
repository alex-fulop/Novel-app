package com.fulop.novel_v2.listeners;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_LIKES;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_FOLLOW;

import android.app.AlertDialog;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

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
                if (user.getFollowUsers() == null) user.setFollowUsers(new ArrayList<>());
                if (user.getFollowUsers().contains(ownerId)) unfollowUser(novel, ownerId);
                else followUser(novel, ownerId);
            }
        }
    }

    private void unfollowUser(Novel novel, String ownerId) {
        user.getFollowUsers().remove(ownerId);
        displayAlertDialog("Unfollow %s?", novel);
        Toast.makeText(novelList.getContext(),
                "User unfollowed successfully", Toast.LENGTH_SHORT).show();

    }

    private void followUser(Novel novel, String ownerId) {
        user.getFollowUsers().add(ownerId);
        displayAlertDialog("Follow %s?", novel);
        Toast.makeText(novelList.getContext(),
                "User followed successfully", Toast.LENGTH_SHORT).show();
    }

    private void displayAlertDialog(String message, Novel novel) {
        new AlertDialog.Builder(novelList.getContext())
                .setTitle(String.format(message, novel.getUsername()))
                .setPositiveButton("Yes", (dialog, which) -> {
                    novelList.setClickable(false);
                    updateFollowedUsers();
                }).setNegativeButton("Cancel", ((dialog, which) -> {
        })).show();
    }

    private void updateFollowedUsers() {
        firebaseDB.collection(DATA_USERS)
                .document(userId)
                .update(DATA_USER_FOLLOW, user.getFollowUsers())
                .addOnSuccessListener(unused -> {
                    novelList.setClickable(true);
                    callback.onUserUpdated();
                })
                .addOnFailureListener(e -> novelList.setClickable(true));
    }

    @Override
    public void onLike(Novel novel) {
        if (novel != null) {
            novelList.setClickable(false);
            if (novel.getLikes().contains(userId)) novel.getLikes().remove(userId);
            else novel.getLikes().add(userId);

            firebaseDB.collection(DATA_NOVELS)
                    .document(novel.getNovelId())
                    .update(DATA_NOVEL_LIKES, novel.getLikes())
                    .addOnSuccessListener(unused -> {
                        novelList.setClickable(true);
                        callback.onUserUpdated();
                    })
                    .addOnFailureListener(e -> novelList.setClickable(true));
        }
    }

    @Override
    public void onShare(Novel novel) {
        if (novel != null) {
            novelList.setClickable(false);
            if (novel.getUserIds().contains(userId)) novel.getUserIds().remove(userId);
            else novel.getUserIds().add(userId);

            firebaseDB.collection(DATA_NOVELS)
                    .document(novel.getNovelId())
                    .update(DATA_NOVEL_USER_IDS, novel.getUserIds())
                    .addOnSuccessListener(unused -> {
                        novelList.setClickable(true);
                        callback.onRefresh();
                    })
                    .addOnFailureListener(e -> novelList.setClickable(true));
        }
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
