package com.fulop.novel_v2.fragments;

import static com.fulop.novel_v2.Util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.Util.Constants.DATA_NOVEL_HASHTAGS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulop.novel_v2.Listeners.NovelListener;
import com.fulop.novel_v2.R;
import com.fulop.novel_v2.adapters.NovelListAdapter;
import com.fulop.novel_v2.models.Novel;
import com.fulop.novel_v2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends NovelFragment {

    public SearchFragment() {
    }

    private String currentHashtag;
    private ImageView followHashtag;
    private NovelListAdapter novelListAdapter;
    private User currentUser;
    private RecyclerView novelList;
    private NovelListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getUid();

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

        novelListAdapter = new NovelListAdapter(userId, new ArrayList<>());
        novelListAdapter.setListener(listener);
        novelList.setLayoutManager(new LinearLayoutManager(getContext()));
        novelList.setAdapter(novelListAdapter);
        novelList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            updateList();
        });
    }

    public void newHashtag(String term) {
        currentHashtag = term;
        followHashtag.setVisibility(View.VISIBLE);
        updateList();
    }

    private void updateList() {
        novelList.setVisibility(View.GONE);
        firebaseDB.collection(DATA_NOVELS).whereArrayContains(DATA_NOVEL_HASHTAGS, currentHashtag).get()
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
    }
}