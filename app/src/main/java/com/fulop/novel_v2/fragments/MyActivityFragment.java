package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.models.Novel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyActivityFragment extends NovelFragment {

    public MyActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
        initFragmentComponents(view);
        return view;
    }

    @Override
    public void updateList() {
        novelList.setVisibility(View.GONE);
        List<Novel> novels = new ArrayList<>();

        firebaseDB.collection(DATA_NOVELS)
                .whereArrayContains(DATA_NOVEL_USER_IDS, userId).get()
                .addOnSuccessListener(list -> {
                    for (DocumentSnapshot document : list.getDocuments()) {
                        Novel novel = document.toObject(Novel.class);
                        if (novel != null) novels.add(novel);
                    }
                    List<Novel> sortedNovels = novels.stream()
                            .sorted(Novel::compareTo)
                            .collect(Collectors.toList());
                    novelListAdapter.updateNovels(sortedNovels);
                })
                .addOnFailureListener(Throwable::printStackTrace);
        novelList.setVisibility(View.VISIBLE);
//    SEARCH FOR NOVELS LOCALLY
    }

    @Override
    public void refreshList() {
        updateList();
    }

    protected void initFragmentComponents(View view) {
        novelList = view.findViewById(R.id.novelList);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        super.initFragmentComponents();
    }
}