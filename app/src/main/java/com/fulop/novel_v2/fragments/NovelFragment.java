package com.fulop.novel_v2.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fulop.novel_v2.adapters.NovelListAdapter;
import com.fulop.novel_v2.listeners.HomeCallback;
import com.fulop.novel_v2.listeners.NovelListenerImpl;
import com.fulop.novel_v2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class NovelFragment extends Fragment {
    protected final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    protected final String userId = FirebaseAuth.getInstance().getUid();

    protected User currentUser;
    protected NovelListAdapter novelListAdapter;
    protected NovelListenerImpl listener;
    protected HomeCallback callback;
    protected RecyclerView novelList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeCallback) {
            callback = (HomeCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HomeCallback");
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (listener != null) listener.setUser(user);
    }

    public abstract void updateList();

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }
}
