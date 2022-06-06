package com.fulop.novel_v2.activities;

import static com.fulop.novel_v2.util.Constants.DATA_PICTURE;
import static com.fulop.novel_v2.util.Constants.DATA_USERS;
import static com.fulop.novel_v2.util.Constants.DATA_USER_EMAIL;
import static com.fulop.novel_v2.util.Constants.DATA_USER_IMAGE_URL;
import static com.fulop.novel_v2.util.Constants.DATA_USER_USERNAME;
import static com.fulop.novel_v2.util.Utils.isNetworkAvailable;
import static com.fulop.novel_v2.util.Utils.loadUrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.models.NovelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String imageUrl;

    private LinearLayout profileProgressLayout;
    private EditText usernameEditText;
    private EditText emailEditText;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileProgressLayout = findViewById(R.id.profileProgressLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        profilePicture = findViewById(R.id.profilePicture);

        if (userId == null) finish();

        profileProgressLayout.setOnTouchListener((view, event) -> true);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null)
                            storeImage(result.getData().getData());
                    }
                });

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        populateInfo();
    }

    private void storeImage(Uri imageUri) {
        Toast.makeText(this, "Uploading image to cloud", Toast.LENGTH_SHORT).show();
        profileProgressLayout.setVisibility(View.VISIBLE);
        StorageReference filePath = firebaseStorage.child(DATA_PICTURE).child(userId);
        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            filePath.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        updateUserProfilePicture(uri);
                        profileProgressLayout.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> onUploadFailure());
        }).addOnFailureListener(e -> onUploadFailure());
    }

    private void onUploadFailure() {
        Toast.makeText(this, "Image upload failed. Please try again later.", Toast.LENGTH_SHORT).show();
        profileProgressLayout.setVisibility(View.GONE);
    }

    private void populateInfo() {
        profileProgressLayout.setVisibility(View.VISIBLE);
        firebaseDB.collection(DATA_USERS)
                .document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    populateProfileWithUserInfo(documentSnapshot);
                    profileProgressLayout.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
            e.printStackTrace();
            finish();
        });
    }

    private void populateProfileWithUserInfo(DocumentSnapshot doc) {
        NovelUser user = doc.toObject(NovelUser.class);
        if (user != null) {
            usernameEditText.setText(user.getUsername(), TextView.BufferType.EDITABLE);
            emailEditText.setText(user.getEmail(), TextView.BufferType.EDITABLE);
            imageUrl = user.getImageUrl();
            if (imageUrl != null)
                loadUrl(user.getImageUrl(), profilePicture, R.drawable.novel_logo);
        }
    }

    private void updateUserProfilePicture(Uri uri) {
        String url = uri.toString();
        firebaseDB.collection(DATA_USERS)
                .document(userId)
                .update(DATA_USER_IMAGE_URL, url)
                .addOnSuccessListener(unused -> {
                    imageUrl = url;
                    loadUrl(url, profilePicture, R.drawable.novel_logo);
                });
    }

    public void onApply(View view) {
        profileProgressLayout.setVisibility(View.VISIBLE);
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put(DATA_USER_USERNAME, username);
        map.put(DATA_USER_EMAIL, email);

        if (isNetworkAvailable(this)) {
            firebaseDB.collection(DATA_USERS)
                    .document(userId).update(map)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                profileProgressLayout.setVisibility(View.GONE);
            });
        } else {
            finish();
        }

    }

    public void onSignOut(View view) {
        firebaseAuth.signOut();
        finish();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }
}