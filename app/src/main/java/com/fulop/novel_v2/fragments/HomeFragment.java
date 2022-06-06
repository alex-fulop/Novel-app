package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_HASHTAGS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;
import static com.fulop.novel_v2.util.Utils.isNetworkAvailable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.database.DatabaseHelper;
import com.fulop.novel_v2.models.Novel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends NovelFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initFragmentComponents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void updateList() {
        if (isNetworkAvailable(requireContext())) refreshList();
        else refreshListWithLocalData();
    }

    @Override
    public void refreshList() {
        if (currentUser != null) {
            List<Novel> novels = new ArrayList<>();

            if (currentUser.getFollowUsers() == null)
                currentUser.setFollowUsers(new ArrayList<>());

            if (currentUser.getFollowHashtags() == null)
                currentUser.setFollowHashtags(new ArrayList<>());

            for (String hashtag : currentUser.getFollowHashtags())
                addNovelsToDisplay(novels, hashtag, DATA_NOVEL_HASHTAGS);
            for (String followedUser : currentUser.getFollowUsers())
                addNovelsToDisplay(novels, followedUser, DATA_NOVEL_USER_IDS);

            updateAdapter(novels);
        }
    }

    @Override
    public void refreshListWithLocalData() {
        if (currentUser != null) {
            List<Novel> novels = new ArrayList<>();
            DatabaseHelper db = new DatabaseHelper(requireContext());

            if (currentUser.getFollowUsers() == null)
                currentUser.setFollowUsers(new ArrayList<>());

            if (currentUser.getFollowHashtags() == null)
                currentUser.setFollowHashtags(new ArrayList<>());

            for (String hashtag : currentUser.getFollowHashtags())
                novels.addAll(db.findNovelsByHashtag(hashtag));
            for (String followedUser : currentUser.getFollowUsers())
                novels.addAll(db.findNovelsByFollowedUser(followedUser));

            updateAdapter(novels);
        }
    }

    private void addNovelsToDisplay(List<Novel> novels, String novelId, String novelTable) {
        firebaseDB.collection(DATA_NOVELS)
                .whereArrayContains(novelTable, novelId).get()
                .addOnSuccessListener(list -> {
                    for (DocumentSnapshot document : list.getDocuments()) {
                        Novel novel = document.toObject(Novel.class);
                        if (novel != null) novels.add(novel);
                    }
                    updateAdapter(novels);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void updateAdapter(List<Novel> novels) {
        novelListAdapter.updateNovels(removeDuplicates(novels
                .stream()
                .sorted()
                .collect(Collectors.toList())));
    }

    private List<Novel> removeDuplicates(List<Novel> originalList) {
        return originalList.stream().distinct().collect(Collectors.toList());
    }

    protected void initFragmentComponents(View view) {
        novelList = view.findViewById(R.id.novelList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        super.initFragmentComponents();
    }
}