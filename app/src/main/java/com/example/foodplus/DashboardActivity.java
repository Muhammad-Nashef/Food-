package com.example.foodplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private ImageButton profileButton;
    private LinearLayout itemsLayout;
    private TypewriterAnimation typewriterAnimation;
    private Button ingredientsBtn;

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserRecipes();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        profileButton = findViewById(R.id.profileButton);
        itemsLayout = findViewById(R.id.items_layout);
        ingredientsBtn = findViewById(R.id.ingredientsBtn);

        String loginName = getIntent().getStringExtra("LOGIN_NAME");

        String welcomeMessage = "Welcome " + loginName + ", ";

        Snackbar.make(findViewById(android.R.id.content), "Tap on the recipe name to show/edit", Snackbar.LENGTH_LONG).show();
        typewriterAnimation = new TypewriterAnimation(welcomeTextView);
        typewriterAnimation.setCharacterDelay(150);
        typewriterAnimation.animateText(welcomeMessage);

        Button addMealButton = findViewById(R.id.add_meal_btn);

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, IngredientsActivity.class);
                intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                intent.putExtra("LOGIN_NAME", getIntent().getStringExtra("LOGIN_NAME"));
                startActivity(intent);
            }
        });

        ingredientsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        fetchUserRecipes();

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

    private void fetchUserRecipes() {
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            String _db_url = "https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/";
            DatabaseReference recipesRef = FirebaseDatabase.getInstance(_db_url).getReference()
                    .child("users").child(userId).child("recipes");

            recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    itemsLayout.removeAllViews();
                    if(dataSnapshot.exists()){
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> recipeData = (Map<String, Object>) recipeSnapshot.getValue();
                        if (recipeData != null) {
                            String recipeName = (String) recipeData.get("recipe_name");
                            createRecipeView(recipeName);
                        }
                    }
                }
                    else{
                        LinearLayout linearLayout = findViewById(R.id.items_layout);

                        ImageView imageView = new ImageView(DashboardActivity.this);
                        imageView.setImageResource(R.drawable.nodata);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        TextView textView = new TextView(DashboardActivity.this);
                        textView.setText("No recipes");
                        textView.setTextColor(Color.parseColor("#800080"));
                        textView.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity = Gravity.CENTER;
                        layoutParams.setMargins(50, 50, 0, 0);
                        layoutParams.width = 400;
                        layoutParams.height = 400;
                        imageView.setLayoutParams(layoutParams);

                        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        textLayoutParams.setMargins(50, 10, 0, 0);
                        textLayoutParams.gravity = Gravity.CENTER;
                        textView.setLayoutParams(textLayoutParams);
                        textView.setTextSize(16);
                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                        linearLayout.addView(imageView);
                        linearLayout.addView(textView);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DashboardActivity.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createRecipeView(String recipeName) {
        LinearLayout linearLayout = findViewById(R.id.items_layout);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.recipe);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView textView = new TextView(this);
        textView.setText(recipeName);
        textView.setTextColor(Color.parseColor("#800080"));
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(50, 50, 0, 0);
        imageView.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textLayoutParams.setMargins(100, 10, 0, 0);
        textView.setTextSize(16);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setLayoutParams(textLayoutParams);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, IngredientsActivity.class);
                intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                intent.putExtra("LOGIN_NAME", getIntent().getStringExtra("LOGIN_NAME"));
                intent.putExtra("RECIPE_NAME", recipeName);
                startActivity(intent);
            }
        });

        linearLayout.addView(imageView);
        linearLayout.addView(textView);
    }

}