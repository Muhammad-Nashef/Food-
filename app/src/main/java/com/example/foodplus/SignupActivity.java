package com.example.foodplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText ageEditText;
    private EditText passwordEditText;
    private Button submitButton;
    private Button cancelButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();

        String _db_url = "https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/";
        FirebaseDatabase db = FirebaseDatabase.getInstance(_db_url);
        mDatabase = db.getReference();

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        ageEditText = findViewById(R.id.ageEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        submitButton = findViewById(R.id.signup_submit_btn);
        cancelButton = findViewById(R.id.signup_cancel_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    usernameEditText.setError("Username is required");
                    return;
                }
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }
                if (age.isEmpty()) {
                    ageEditText.setError("Age is required");
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Please enter a valid email address");
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters long");
                    return;
                }

                progressDialog.show();
                submitDataToFirebase(username, email, age, password);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(700);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    navigateToMain();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void submitDataToFirebase(final String username, final String email, final String age, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                writeNewUser(user.getUid(), username, email, age);

                                Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);

                                int atIndex = email.indexOf('@');
                                String displayName = email.substring(0, atIndex);
                                displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
                                intent.putExtra("LOGIN_NAME", displayName);
                                intent.putExtra("USER_ID",user.getUid());
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeNewUser(String userId, String username, String email, String age) {
        Map<String, Recipe> emptyRecipeMap = new HashMap<>();

        User user = new User(username, email, age, emptyRecipeMap);

        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "writeNewUser:success");
                        } else {
                            Log.w(TAG, "writeNewUser:failure", task.getException());
                        }
                    }
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
