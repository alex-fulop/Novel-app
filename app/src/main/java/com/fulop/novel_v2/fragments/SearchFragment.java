package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_HASHTAGS;
import static io.github.redouane59.twitter.dto.tweet.TweetV2.TweetData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.api.TweetsAsyncApiCall;
import com.fulop.novel_v2.api.UsersAsyncApiCall;
import com.fulop.novel_v2.database.DatabaseHelper;
import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.NovelUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.redouane59.twitter.dto.user.User;

public class SearchFragment extends NovelFragment {

    public SearchFragment() {
    }

    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private ImageView followHashtag;
    private LinearLayout searchProgressLayout;
    private boolean hashtagIsFollowed;

    private String searchTerm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initFragmentComponents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        followHashtag.setOnClickListener(v -> {
            followHashtag.setClickable(false);
            followOrUnfollowClickedHashtag();
            updateFollowedHashtagsForCurrentUser();
        });
    }

    @Override
    public void updateList() {
        List<Novel> novels = new ArrayList<>();
        firebaseDB.collection(DATA_NOVELS)
                .whereArrayContains(DATA_NOVEL_HASHTAGS, searchTerm).get()
                .addOnSuccessListener(list -> {
                    for (DocumentSnapshot document : list.getDocuments()) {
                        Novel novel = document.toObject(Novel.class);
                        if (novel != null) novels.add(novel);
                    }
                    Collections.reverse(novels);
                    novelListAdapter.updateNovels(novels);
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void refreshList() {
        List<Novel> novels = new ArrayList<>();
        queryTwitterForTweets();

        if (searchTerm != null) {
            updateList();
            updateFollowDrawable();
            saveNovelsLocally(novels);
        } else refreshListWithLocalData();
    }

    @Override
    public void refreshListWithLocalData() {
        DatabaseHelper db = new DatabaseHelper(getContext());
        List<Novel> novels = db.findNovelsForSearchTerm(searchTerm);

        Collections.reverse(novels);
        novelListAdapter.updateNovels(novels);
        updateFollowDrawable();
    }

    private void queryTwitterForTweets() {
        List<TweetData> tweets = getTweetsForSearchTerm();
        if (tweets != null) {
            tweets.forEach(this::createNovelUserForTweetIfNotPresent);
            tweets.forEach(this::createNovelFromTweetIfNotPresent);
        }
    }

    private void saveNovelsLocally(List<Novel> novels) {
        DatabaseHelper db = new DatabaseHelper(requireContext());
        novels.forEach(db::addNovel);
    }

    public void searchByHashtag(String term) {
        searchTerm = term;
        followHashtag.setVisibility(View.VISIBLE);
        refreshList();
    }

    private void createNovelUserForTweetIfNotPresent(TweetData tweet) {
        User user = getTweetUser(tweet);
        firebaseDB.collection(DATA_USERS).document(user.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    NovelUser existingUser = documentSnapshot.toObject(NovelUser.class);
                    if (existingUser == null) createUserFromTweet(user);
                });
    }

    private void createNovelFromTweetIfNotPresent(TweetData tweet) {
        firebaseDB.collection(DATA_NOVELS).document(tweet.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Novel existingNovel = documentSnapshot.toObject(Novel.class);
                    if (existingNovel == null) createNovelFromTweet(tweet);
                });
    }

    private List<TweetData> getTweetsForSearchTerm() {
        TweetsAsyncApiCall call = new TweetsAsyncApiCall(searchTerm);
        Thread thread = new Thread(call);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return call.getResult().getData();
    }

    private void createUserFromTweet(User user) {
        DatabaseHelper db = new DatabaseHelper(getContext());

        NovelUser novelUser = new NovelUser();
        novelUser.setUsername(user.getDisplayedName());
        novelUser.setEmail(user.getName() + "@novel.com");
        novelUser.setImageUrl(user.getProfileImageUrl());
        novelUser.setFollowUsers(new ArrayList<>());
        novelUser.setFollowHashtags(new ArrayList<>());
        firebaseDB.collection(DATA_USERS).document(user.getId()).set(novelUser);
        db.addUser(novelUser);
    }

    private User getTweetUser(TweetData tweet) {
        UsersAsyncApiCall call = new UsersAsyncApiCall(tweet.getAuthorId());
        Thread thread = new Thread(call);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return call.getUser();
    }

    private void createNovelFromTweet(TweetData tweet) {
        DocumentReference novelReference = firebaseDB.collection(DATA_NOVELS).document(tweet.getId());
        DatabaseHelper db = new DatabaseHelper(getContext());

        Novel novel = new Novel();
        novel.setNovelId(novelReference.getId());
        novel.setText(tweet.getText());
        novel.setUsername(tweet.getUser().getName());
        novel.setUserIds(Arrays.asList(tweet.getAuthorId()));
        novel.setHashtags(Arrays.asList(searchTerm));
        novel.setLikes(new ArrayList<>());
        novel.setTimestamp(System.currentTimeMillis());
        if (tweet.getAttachments() != null)
            novel.setImageUrl(tweet.getAttachments().getMediaKeys()[0]);
        novelReference.set(novel);
        db.addNovel(novel);
    }

    private void followOrUnfollowClickedHashtag() {
        if (currentUser.getFollowHashtags() == null)
            currentUser.setFollowHashtags(new ArrayList<>());
        hashtagIsFollowed = currentUser.getFollowHashtags().contains(searchTerm);
        if (hashtagIsFollowed) currentUser.getFollowHashtags().remove(searchTerm);
        else currentUser.getFollowHashtags().add(searchTerm);
        updateFollowDrawable();
    }

    private void updateFollowedHashtagsForCurrentUser() {
        firebaseDB.collection(DATA_USERS)
                .document(userId)
                .update(DATA_USER_HASHTAGS, currentUser.getFollowHashtags())
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.onUserUpdated();
                })
                .addOnFailureListener(Throwable::printStackTrace);
        followHashtag.setClickable(true);
    }

    private void updateFollowDrawable() {
        if (currentUser.getFollowHashtags() == null)
            currentUser.setFollowHashtags(new ArrayList<>());

        hashtagIsFollowed = currentUser.getFollowHashtags().contains(searchTerm);

        if (getContext() != null)
            if (hashtagIsFollowed)
                followHashtag.setImageDrawable(ContextCompat
                        .getDrawable(getContext(), R.drawable.follow_active));
            else
                followHashtag.setImageDrawable(ContextCompat
                        .getDrawable(getContext(), R.drawable.follow));
    }

    protected void initFragmentComponents(View view) {
        followHashtag = view.findViewById(R.id.followHashtag);
        novelList = view.findViewById(R.id.novelList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        searchProgressLayout = view.findViewById(R.id.searchProgressLayout);
        searchProgressLayout.setOnTouchListener((v, event) -> true);
        super.initFragmentComponents();
    }
}