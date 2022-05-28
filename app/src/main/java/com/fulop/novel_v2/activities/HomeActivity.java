package com.fulop.novel_v2.activities;

import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Utils.loadUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.fragments.HomeFragment;
import com.fulop.novel_v2.fragments.MyActivityFragment;
import com.fulop.novel_v2.fragments.NovelFragment;
import com.fulop.novel_v2.fragments.SearchFragment;
import com.fulop.novel_v2.listeners.HomeCallback;
import com.fulop.novel_v2.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity implements HomeCallback {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();

    private final HomeFragment homeFragment = new HomeFragment();
    private final SearchFragment searchFragment = new SearchFragment();
    private final MyActivityFragment myActivityFragment = new MyActivityFragment();

    private LinearLayout homeProgressLayout;
    private ImageView profilePicture;
    private FloatingActionButton actionButton;
    private EditText search;
    private TextView titleBar;
    private CardView searchBar;

    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private NovelFragment currentFragment = homeFragment;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager2 viewPagerContainer = findViewById(R.id.viewPagerContainer);
        TabLayout tabLayout = findViewById(R.id.tabs);
        homeProgressLayout = findViewById(R.id.homeProgressLayout);
        profilePicture = findViewById(R.id.profilePicture);
        actionButton = findViewById(R.id.floatingActionButton);
        search = findViewById(R.id.search);
        titleBar = findViewById(R.id.titleBar);
        searchBar = findViewById(R.id.searchBar);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerContainer.setAdapter(sectionPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPagerContainer,
                (tab, position) -> {
                    if (position == 0)
                        tab.setIcon(AppCompatResources.getDrawable(this, R.drawable.selector_home));
                    else if (position == 1)
                        tab.setIcon(AppCompatResources.getDrawable(this, R.drawable.selector_search));
                    else
                        tab.setIcon(AppCompatResources.getDrawable(this, R.drawable.selector_myactivity));
                }
        ).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    titleBar.setVisibility(View.VISIBLE);
                    titleBar.setText("Home");
                    searchBar.setVisibility(View.GONE);
                    currentFragment = homeFragment;
                } else if (tab.getPosition() == 1) {
                    titleBar.setVisibility(View.GONE);
                    searchBar.setVisibility(View.VISIBLE);
                    currentFragment = searchFragment;
                } else {
                    titleBar.setVisibility(View.VISIBLE);
                    titleBar.setText("My Activity");
                    searchBar.setVisibility(View.GONE);
                    currentFragment = myActivityFragment;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        profilePicture.setOnClickListener(view -> {
            startActivity(ProfileActivity.newIntent(this));
        });

        actionButton.setOnClickListener(v -> {
            startActivity(NovelActivity.newIntent(this, userId, currentUser.getUsername()));
        });

        homeProgressLayout.setOnTouchListener((v, event) -> true);

        search.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFragment.newHashtag(view.getText().toString());
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(LoginActivity.newIntent(this));
            finish();
        } else {
            populate();
        }
    }

    @Override
    public void onUserUpdated() {
        populate();
    }

    @Override
    public void onRefresh() {
        currentFragment.updateList();
    }

    private void populate() {
        homeProgressLayout.setVisibility(View.VISIBLE);
        firebaseDB.collection(DATA_USERS).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    homeProgressLayout.setVisibility(View.GONE);
                    currentUser = documentSnapshot.toObject(User.class);
                    loadProfilePicture();
                    updateFragmentUser();
                }).addOnFailureListener(e -> {
            e.printStackTrace();
            finish();
        });
    }

    private void loadProfilePicture() {
        if (currentUser != null && currentUser.getImageUrl() != null) {
            loadUrl(currentUser.getImageUrl(), profilePicture, R.drawable.novel_logo);
        }
    }

    private void updateFragmentUser() {
        homeFragment.setUser(currentUser);
        searchFragment.setUser(currentUser);
        myActivityFragment.setUser(currentUser);
        currentFragment.updateList();
    }

    private class SectionPagerAdapter extends FragmentStateAdapter {

        public SectionPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return homeFragment;
            else if (position == 1) return searchFragment;
            else return myActivityFragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }
}