package com.example.foodplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientsActivity extends AppCompatActivity {

    private EditText ingredientCountEditText;
    private LinearLayout ingredientsContainer;
    private EditText recipeNameEditText;
    private Spinner ingredientSpinner;

    private final String _db_url = "https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/";
    private final DatabaseReference recipeRef = FirebaseDatabase.getInstance(_db_url).getReference();

    private final List<Ingredient> ingredients = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients);

        recipeNameEditText = findViewById(R.id.recipe_name);
        ingredientCountEditText = findViewById(R.id.ingredientCountEditText);
        Button addButton = findViewById(R.id.addButton);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        ingredientSpinner = findViewById(R.id.ingredientSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.basic_ingredients, R.layout.my_selected_item);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        ingredientSpinner.setAdapter(adapter);

        ImageView checkMark = findViewById(R.id.checkMark);

        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //submitRecipeToFirebase();
                ObjectAnimator moveUp = ObjectAnimator.ofFloat(checkMark, "translationY", -50f);
                moveUp.setDuration(500);
                moveUp.setRepeatMode(ObjectAnimator.REVERSE);
                moveUp.setRepeatCount(1);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(moveUp);
                animatorSet.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {}
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        submitRecipeToFirebase();
                    }
                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {}
                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {}
                });
                animatorSet.start();
            }
        });

        ImageView cancelImageView = findViewById(R.id.cancel);
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(cancelImageView, "rotation", 0f, 180f);
                rotateAnim.setDuration(500);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(rotateAnim);
                animatorSet.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {}
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        Intent intent = new Intent(IngredientsActivity.this, DashboardActivity.class);
                        intent.putExtra("LOGIN_NAME", getIntent().getStringExtra("LOGIN_NAME"));
                        intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {}
                });
                animatorSet.start();
            }
        });

        ImageView shareImageView = findViewById(R.id.share);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText phoneNumberInput = new EditText(IngredientsActivity.this);
                if (ContextCompat.checkSelfPermission(IngredientsActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(IngredientsActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                    try {
                        phoneNumberInput.setHint("Phone number");
                        AlertDialog.Builder shareDialogBuilder = new AlertDialog.Builder(IngredientsActivity.this);
                        shareDialogBuilder.setTitle("Share recipe");
                        shareDialogBuilder.setView(phoneNumberInput);
                        shareDialogBuilder.setMessage("Enter phone number:").setPositiveButton("Share",(dialog,which) ->{
                            String phoneNumber = phoneNumberInput.getText().toString();
                            phoneNumber = phoneNumber.substring(1);
                            phoneNumber = "+972" + phoneNumber;
                            //send sms
                            String msg = IngredientsActivity.this.toString();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
                            Toast.makeText(IngredientsActivity.this, "Recipe sent", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton("Cancel",null).show();


                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    }

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredientToList();
            }
        });

        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        if (recipeName != null) {
            recipeNameEditText.setText(recipeName);
            fetchRecipeIngredients(recipeName);
        }

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

    private void fetchRecipeIngredients(String recipeName) {
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId != null && recipeName != null) {
            DatabaseReference _recipeRef = recipeRef.child("users").child(userId).child("recipes");

            _recipeRef.orderByChild("recipe_name").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        Recipe recipeData = recipeSnapshot.getValue(Recipe.class);
                        if (recipeData != null) {
                            List<Ingredient> temp_ingredients = recipeData.getIngredients();
                            for (Ingredient ingredient : temp_ingredients) {
                                String ingredientName = ingredient.getName();
                                float ingredientCount = Float.parseFloat(String.valueOf(ingredient.getQuantity()));
                                String unit = ingredient.getUnit();
                                ingredients.add(ingredient);
                                TextView ingredientTextView = new TextView(IngredientsActivity.this);
                                ingredientTextView.setText(ingredientName + " (" + ingredientCount + " " + unit + ")");
                                ingredientTextView.setTextSize(18);
                                ingredientTextView.setTextColor(Color.parseColor("#000000"));
                                ingredientsContainer.addView(ingredientTextView);

                                // Animate the text view
                                animateTextView(ingredientTextView);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(IngredientsActivity.this, "Failed to fetch recipe ingredients", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void animateTextView(TextView textView) {
        ObjectAnimator mover = ObjectAnimator.ofFloat(textView, "translationY", 400f, 0f);
        mover.setDuration(500);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn).with(mover);
        animatorSet.start();
    }

    private void submitRecipeToFirebase() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String userId = getIntent().getStringExtra("USER_ID");
        if (recipeName.isEmpty()) {
            Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Ingredients cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        assert userId != null;
        DatabaseReference recipesRef = FirebaseDatabase.getInstance("https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("recipes");
        recipesRef.orderByChild("recipe_name").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recipe exists, update it
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        assert key != null;
                        recipesRef.child(key).child("ingredients").setValue(ingredients);
                    }
                    Toast.makeText(IngredientsActivity.this, "Recipe updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IngredientsActivity.this, DashboardActivity.class);
                    intent.putExtra("LOGIN_NAME", getIntent().getStringExtra("LOGIN_NAME"));
                    intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                    startActivity(intent);
                } else {
                    String recipeKey = recipeRef.child("users").child(userId).child("recipes").push().getKey();
                    Map<String, Object> recipeMap = new HashMap<>();
                    recipeMap.put("recipe_name", recipeName);
                    recipeMap.put("ingredients", ingredients);
                    assert recipeKey != null;
                    DatabaseReference _recipeRef = recipeRef.child("users").child(userId).child("recipes").child(recipeKey);
                    _recipeRef.setValue(recipeMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(IngredientsActivity.this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(IngredientsActivity.this, DashboardActivity.class);
                                    intent.putExtra("LOGIN_NAME", getIntent().getStringExtra("LOGIN_NAME"));
                                    intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(IngredientsActivity.this, "Failed to add recipe!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IngredientsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void addIngredientToList() {
        String ingredient_name = ingredientSpinner.getSelectedItem().toString();
        String count = ingredientCountEditText.getText().toString().trim();
        RadioGroup radio_group = findViewById(R.id.units);
        int selectedId = radio_group.getCheckedRadioButtonId();
        String unit = "";
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            unit = selectedRadioButton.getText().toString();
        }
        if (!ingredient_name.isEmpty() && !count.isEmpty() && !unit.isEmpty()) {
            Ingredient temp_ingredient = new Ingredient(ingredient_name,Float.parseFloat(count),unit);
            addOrReplaceIngredient(temp_ingredient);
            TextView ingredientTextView = new TextView(this);
            ingredientTextView.setText(ingredient_name + " (" + count +" "+ unit +")");
            ingredientTextView.setTextSize(18);
            ingredientTextView.setTextColor(Color.parseColor("#000000"));
            ingredientsContainer.addView(ingredientTextView);
            ingredientCountEditText.setText("");

            animateTextView(ingredientTextView);
        }
    }
    public void addOrReplaceIngredient(Ingredient newIngredient) {
        boolean found = false;
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient.getName().equals(newIngredient.getName())) {
                ingredients.set(i, newIngredient);
                found = true;
                break;
            }
        }
        if (!found) {
            ingredients.add(newIngredient);
        }
    }
    @NonNull
    @Override
    public String toString(){
        return "Recipe: " + recipeNameEditText.getText() + "\nIngredients:" + "\n" + " " + ingredients.toString();
    }
}
