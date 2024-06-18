package com.example.recipeproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HandleMessages extends MenuActivity {
    RecyclerView recyclerView;
    Button btnExit;
    FirebaseAuth mAuth;
    DatabaseReference userMessagesRef;
    ValueEventListener userMessagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_msgs);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        btnExit = findViewById(R.id.exit);
        recyclerView = findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String realIdUser = user.getUid();

        // getting the messages and then displaying them to the user
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference msgRef = mDatabase.getReference("messages");

        // navigating to the specific user's messages
        userMessagesRef = msgRef.child(realIdUser);
        userMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Item> itemList = new ArrayList<>();
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    String msg = msgSnapshot.getValue(String.class);
                    // displaying the messages to the user
                    itemList.add(new Item(msg));
                }
                CustomAdapter adapter = new CustomAdapter(itemList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(HandleMessages.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Failed to read messages: " + error.getMessage());
            }
        };

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMsg = new Intent(HandleMessages.this, Menu.class);
                TransitionUtility.startActivityWithTransition(HandleMessages.this, intentMsg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userMessagesRef.addValueEventListener(userMessagesListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userMessagesRef.removeEventListener(userMessagesListener);
    }
}
