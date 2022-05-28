package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_HASHTAGS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.adapters.NovelListAdapter;
import com.fulop.novel_v2.listeners.NovelListenerImpl;
import com.fulop.novel_v2.models.Novel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends NovelFragment {

    public SearchFragment() {
    }

    private String currentHashtag;
    private ImageView followHashtag;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean hashtagIsFollowed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        followHashtag = view.findViewById(R.id.followHashtag);
        novelList = view.findViewById(R.id.novelList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = new NovelListenerImpl(novelList, currentUser, callback);

        novelListAdapter = new NovelListAdapter(userId, new ArrayList<>());
        novelListAdapter.setListener(listener);
        novelList.setLayoutManager(new LinearLayoutManager(getContext()));
        novelList.setAdapter(novelListAdapter);
        novelList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            updateList();
        });

        followHashtag.setOnClickListener(v -> {
            followHashtag.setClickable(false);

            if (currentUser.getFollowHashtags() == null)
                currentUser.setFollowHashtags(new ArrayList<>());

            if (hashtagIsFollowed) currentUser.getFollowHashtags().remove(currentHashtag);
            else currentUser.getFollowHashtags().add(currentHashtag);

            firebaseDB.collection(DATA_USERS)
                    .document(userId)
                    .update(DATA_USER_HASHTAGS, currentUser.getFollowHashtags())
                    .addOnSuccessListener(unused -> {
                        if (callback != null) callback.onUserUpdated();
                        followHashtag.setClickable(true);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        followHashtag.setClickable(true);
                    });
        });
    }

    public void newHashtag(String term) {
        currentHashtag = term;
        followHashtag.setVisibility(View.VISIBLE);
        updateList();
    }

    private void updateFollowDrawable() {
        hashtagIsFollowed = currentUser.getFollowHashtags().contains(currentHashtag);
        if (getContext() != null)
            if (hashtagIsFollowed)
                followHashtag.setImageDrawable(ContextCompat
                        .getDrawable(getContext(), R.drawable.follow_active));
            else
                followHashtag.setImageDrawable(ContextCompat
                        .getDrawable(getContext(), R.drawable.follow));
    }

    @Override
    public void updateList() {
        if (currentUser.getFollowHashtags() == null)
            currentUser.setFollowHashtags(new ArrayList<>());

        novelList.setVisibility(View.GONE);
        firebaseDB.collection(DATA_NOVELS)
                .whereArrayContains(DATA_NOVEL_HASHTAGS, currentHashtag).get()
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