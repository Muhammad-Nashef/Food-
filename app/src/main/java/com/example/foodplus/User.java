package com.example.foodplus;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class User {
    public String username;
    public String email;
    public String age;
    public Map<String, Recipe> recipes;

    public User() {
    }

    public User(String username, String email, String age, Map<String, Recipe> recipes) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.recipes = recipes;
    }
}