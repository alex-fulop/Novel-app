package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_HASHTAGS;
import static com.google.firebase.auth.UserRecord.CreateRequest;
import static io.github.redouane59.twitter.dto.tweet.TweetV2.TweetData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.api.TweetsAsyncApiCall;
import com.fulop.novel_v2.api.UsersAsyncApiCall;
import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.NovelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
    private boolean hashtagIsFollowed;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
        if (currentUser.getFollowHashtags() == null)
            currentUser.setFollowHashtags(new ArrayList<>());

        novelList.setVisibility(View.GONE);

        if (searchTerm != null) {
            List<TweetData> tweets = getTweetsForSearchTerm();
            tweets.forEach(this::createNovelUserForTweetIfNotPresent);
            tweets.forEach(this::createNovelFromTweet);

            firebaseDB.collection(DATA_NOVELS)
                    .whereArrayContains(DATA_NOVEL_HASHTAGS, searchTerm).get()
                    .addOnSuccessListener(list -> {
                        novelList.setVisibility(View.VISIBLE);
                        List<Novel> novels = new ArrayList<>();
                        for (DocumentSnapshot document : list.getDocuments()) {
                            Novel novel = document.toObject(Novel.class);
                            if (novel != null) novels.add(novel);
                        }
                        Collections.reverse(novels);
                        novelListAdapter.updateNovels(novels);
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
            updateFollowDrawable();
        }
    }

    public void searchByHashtag(String term) {
        searchTerm = term;
        followHashtag.setVisibility(View.VISIBLE);
        updateList();
    }

    private void createNovelUserForTweetIfNotPresent(TweetData tweet) {
        User user = getTweetUser(tweet);
        firebaseDB.collection(DATA_USERS).document(user.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    NovelUser existingUser = documentSnapshot.toObject(NovelUser.class);
                    if (existingUser == null) createNewNovelUserFromTwitterUser(user);
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

//    private void createUserFromTwitterWithEmailAndPassword(User user) {
//        firebaseAuth.createUser(new UserRecord.CreateRequest().setEmail())
//        firebaseAuth.createUser(user.getName() + "@novel.com", user.getName())
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        String error = String.format("User fetch error: %s", requireNonNull(task.getException()).getLocalizedMessage());
//                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
//                    } else createNewNovelUserFromTwitterUser(user);
//                }).addOnFailureListener(Throwable::printStackTrace);
//    }

    private void createNewNovelUserFromTwitterUser(User user) {
        CreateRequest request = new CreateRequest()
                .setDisplayName(user.getDisplayedName())
                .setEmail(user.getName() + "@novel.com")
                .setEmailVerified(false)
                .setPassword("secret")
                .setPhotoUrl(user.getProfileImageUrl())
                .setDisabled(false);

        try {
            firebaseAuth.createUser(request);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
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
        Novel novel = new Novel();
        novel.setNovelId(tweet.getId());
        novel.setText(tweet.getText());
        novel.setUserIds(Arrays.asList(tweet.getAuthorId()));
        novel.setHashtags(Arrays.asList(tweet.getAuthorId()));
        firebaseDB.collection(DATA_NOVELS).document().set(novel);
    }

    private void followOrUnfollowClickedHashtag() {
        if (currentUser.getFollowHashtags() == null)
            currentUser.setFollowHashtags(new ArrayList<>());
        if (hashtagIsFollowed) currentUser.getFollowHashtags().remove(searchTerm);
        else currentUser.getFollowHashtags().add(searchTerm);
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
        hashtagIsFollowed = currentUser.getFollowHashtags()
                .contains(searchTerm);
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
        super.initFragmentComponents();
    }
}