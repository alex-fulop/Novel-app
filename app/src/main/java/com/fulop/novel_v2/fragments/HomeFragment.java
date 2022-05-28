package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.adapters.NovelListAdapter;
import com.fulop.novel_v2.listeners.NovelListenerImpl;
import com.fulop.novel_v2.models.Novel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends NovelFragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        novelList = view.findViewById(R.id.novelList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        listener = new NovelListenerImpl(novelList, currentUser, callback);

        novelListAdapter = new NovelListAdapter(userId, new ArrayList<>());
        novelListAdapter.setListener(listener);
        if (novelList != null) {
            novelList.setLayoutManager(new LinearLayoutManager(getContext()));
            novelList.setAdapter(novelListAdapter);
            novelList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            updateList();
        });
    }

    @Override
    public void updateList() {
        novelList.setVisibility(View.GONE);
        if (currentUser != null) {
            if (currentUser.getFollowUsers() == null) currentUser.setFollowUsers(new ArrayList<>());
            List<Novel> novels = new ArrayList<>();

            if (currentUser.getFollowHashtags() == null)
                currentUser.setFollowHashtags(new ArrayList<>());

            for (String hashtag : currentUser.getFollowHashtags())
                addNovels(novels, hashtag, DATA_NOVEL_HASHTAGS);
            for (String followedUser : currentUser.getFollowUsers())
                addNovels(novels, followedUser, DATA_NOVEL_USER_IDS);
        }
    }

    private void addNovels(List<Novel> novels, String novelId, String novelTable) {
        firebaseDB.collection(DATA_NOVELS)
                .whereArrayContains(novelTable, novelId).get()
                .addOnSuccessListener(list -> {
                    for (DocumentSnapshot document : list.getDocuments()) {
                        Novel novel = document.toObject(Novel.class);
                        if (novel != null) novels.add(novel);
                    }
                    updateAdapter(novels);
                    novelList.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    novelList.setVisibility(View.VISIBLE);
                });
    }

    private void updateAdapter(List<Novel> novels) {
        novelListAdapter.updateNovels(removeDublicates(novels
                .stream()
                .sorted()
                .collect(Collectors.toList())));
    }

    private List<Novel> removeDublicates(List<Novel> originalList) {
        return originalList.stream().distinct().collect(Collectors.toList());
    }
}