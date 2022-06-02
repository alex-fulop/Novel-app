package com.fulop.novel_v2.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulop.novel_v2.adapters.NovelListAdapter;
import com.fulop.novel_v2.listeners.HomeCallback;
import com.fulop.novel_v2.listeners.NovelListenerImpl;
import com.fulop.novel_v2.models.NovelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public abstract class NovelFragment extends Fragment {
    protected final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    protected final String userId = FirebaseAuth.getInstance().getUid();

    protected NovelUser currentUser;
    protected NovelListAdapter novelListAdapter;
    protected NovelListenerImpl listener;
    protected HomeCallback callback;
    protected RecyclerView novelList;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeCallback) callback = (HomeCallback) context;
        else throw new RuntimeException(context + " must implement HomeCallback");
    }

    protected void initFragmentComponents() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    public abstract void updateList();

    public abstract void refreshList();

    public void setUser(NovelUser user) {
        this.currentUser = user;
        if (listener != null) listener.setUser(user);
    }

}
