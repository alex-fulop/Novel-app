package com.fulop.novel_v2.activities;

import static com.fulop.novel_v2.util.Constants.DATA_NOVELS;
import static com.fulop.novel_v2.util.Constants.DATA_PICTURE;
import static com.fulop.novel_v2.util.Utils.loadUrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fulop.novel_v2.R;
import com.fulop.novel_v2.database.DatabaseHelper;
import com.fulop.novel_v2.models.Novel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NovelActivity extends AppCompatActivity {

    public static final String PARAM_USER_ID = "userid";
    public static final String PARAM_USER_NAME = "userName";

    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

    private LinearLayout novelProgressLayout;
    private TextView novelText;
    private ImageView novelImage;

    private String imageUrl;
    private String userId;
    private String userName;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null)
                        storeImage(result.getData().getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);

        novelProgressLayout = findViewById(R.id.novelProgressLayout);
        novelText = findViewById(R.id.novelText);
        novelImage = findViewById(R.id.novelImage);

        if (getIntent().hasExtra(PARAM_USER_ID) && getIntent().hasExtra(PARAM_USER_NAME)) {
            userId = getIntent().getStringExtra(PARAM_USER_ID);
            userName = getIntent().getStringExtra(PARAM_USER_NAME);
        } else {
            Toast.makeText(this, "Error creating novel", Toast.LENGTH_SHORT).show();
            finish();
        }

        novelProgressLayout.setOnTouchListener((v, event) -> true);
    }

    public static Intent newIntent(Context context, String userId, String userName) {
        Intent intent = new Intent(context, NovelActivity.class);
        intent.putExtra(PARAM_USER_ID, userId);
        intent.putExtra(PARAM_USER_NAME, userName);
        return intent;
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void storeImage(Uri imageUri) {
        Toast.makeText(this, "Uploading image to cloud", Toast.LENGTH_SHORT).show();
        novelProgressLayout.setVisibility(View.VISIBLE);
        StorageReference filePath = firebaseStorage.child(DATA_PICTURE).child(userId);
        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            filePath.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        loadUrl(imageUrl, novelImage, R.drawable.novel_logo);
                        novelProgressLayout.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        onUploadFailure();
                    });
        }).addOnFailureListener(e -> onUploadFailure());
    }

    private void onUploadFailure() {
        Toast.makeText(this, "Image upload failed. Please try again later.", Toast.LENGTH_SHORT).show();
        novelProgressLayout.setVisibility(View.GONE);
    }

    public void postNovel(View view) {
        novelProgressLayout.setVisibility(View.VISIBLE);

        DatabaseHelper db = new DatabaseHelper(this);

        DocumentReference novelReference = firebaseDB.collection(DATA_NOVELS).document();
        String novelText = this.novelText.getText().toString();
        List<String> hashtags = getHashtags(novelText);

        Novel novel = new Novel();
        novel.setNovelId(novelReference.getId());
        novel.setText(novelText);
        novel.setUsername(userName);
        novel.setUserIds(Arrays.asList(userId));
        novel.setLikes(new ArrayList<>());
        novel.setHashtags(hashtags);
        novel.setImageUrl(imageUrl);
        novel.setTimestamp(System.currentTimeMillis());

        novelReference.set(novel)
                .addOnCompleteListener(unused -> finish())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    novelProgressLayout.setVisibility(View.GONE);
                    Toast.makeText(this, "Novel post failed. Please try again later", Toast.LENGTH_SHORT).show();
                });
        db.addNovel(novel);
    }

    private List<String> getHashtags(String text) {
        List<String> hashtags = new ArrayList<>();

        while (text.contains("#")) {
            String hashtag;
            int hash = text.indexOf("#");
            text = text.substring(hash + 1);

            int firstSpace = text.indexOf(" ");
            int firstHash = text.indexOf("#");

            if (firstSpace == -1 && firstHash == -1) hashtag = text;
            else if (firstSpace != -1 && firstSpace < firstHash) {
                hashtag = text.substring(0, firstSpace);
                text = text.substring(firstSpace + 1);
            } else {
                hashtag = text.substring(0, firstHash);
                text = text.substring(firstHash);
            }
            if (!hashtag.isEmpty()) hashtags.add(hashtag);
        }

        return hashtags;
    }
}