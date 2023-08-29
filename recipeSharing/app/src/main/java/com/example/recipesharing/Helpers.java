package com.example.recipesharing;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Helpers
{
    // getUsername cannot work because Firebase Realtime Database operations are asynchronous.
    // This means that you cannot directly return a value from a method that involves asynchronous
    // operations like DataSnapshot retrieval.
    public String getUsername()
    {
        return "not gonna work";
    }
}
