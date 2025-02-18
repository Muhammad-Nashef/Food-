package com.example.foodplus;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userAgeTextView;
    private TextView userRecipeCountTextView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userAgeTextView = findViewById(R.id.userAgeTextView);
        userRecipeCountTextView = findViewById(R.id.userRecipeCountTextView);
        videoView = findViewById(R.id.videoView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            String userI = user.getUid();
            mDatabase.child("users").child(userI).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userNameTextView.setText("Username: " + snapshot.child("username").getValue(String.class));
                    userEmailTextView.setText("Email: " + snapshot.child("email").getValue(String.class));
                    userAgeTextView.setText("Age: " + snapshot.child("age").getValue(String.class));
                    userRecipeCountTextView.setText("Recipe count: " + snapshot.child("recipes").getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to read username.", Toast.LENGTH_SHORT).show();
                }
            });
        }



        playVideoFromUrl();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the previous activity
                finish();
            }
        };

        // Add callback to the dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private void playVideoFromUrl() {
        VideoView videoview = findViewById(R.id.videoView);
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.food;
        videoview.setVideoURI(Uri.parse(uri));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
