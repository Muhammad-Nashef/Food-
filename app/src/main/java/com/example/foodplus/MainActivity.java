package com.example.foodplus;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    FirebaseAuth fp_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_btn);
        signupButton = findViewById(R.id.signup_btn);

        String _db_url = "https://foodplus-19e77-default-rtdb.europe-west1.firebasedatabase.app/";
        FirebaseDatabase db = FirebaseDatabase.getInstance(_db_url);
        fp_auth = FirebaseAuth.getInstance();
        DatabaseReference ref = db.getReference("Name");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty email or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                validateLogin(email, password);
            }
        });
    }

    private void navigateToDashboard(String username, String user_id) {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        intent.putExtra("LOGIN_NAME", username);
        intent.putExtra("USER_ID", user_id);
        startActivity(intent);
    }

    private void validateLogin(String email, String password) {
        fp_auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = fp_auth.getCurrentUser();
                            assert user != null;
                            String email = user.getEmail();
                            assert email != null;
                            int atIndex = email.indexOf('@');

                            String displayName = email.substring(0, atIndex);
                            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

                            String finalDisplayName = displayName;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        synchronized (this) {
                                            wait(500);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    navigateToDashboard(finalDisplayName, user.getUid());
                                                }
                                            });
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}