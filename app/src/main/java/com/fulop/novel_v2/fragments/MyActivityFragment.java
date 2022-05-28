package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_NOVEL_USER_IDS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MyActivityFragment extends NovelFragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    public MyActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        novelList = view.findViewById(R.id.novelList);
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
                    novelList.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    novelList.setVisibility(View.VISIBLE);
                });
    }
}