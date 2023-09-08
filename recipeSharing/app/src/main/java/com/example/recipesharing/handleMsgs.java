package com.example.recipesharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class handleMsgs extends AppCompatActivity
{
    RecyclerView recyclerView;
    Button btnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgs);
        btnExit = findViewById(R.id.exit);
        recyclerView = findViewById(R.id.recyclerView);
        // getting the messages and then displaying them to the user
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference msgRef = mDatabase.getReference("messages");
        msgRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren())
                {
                    // finding the messages that are for this user
                    Messages msg = msgSnapshot.getValue(Messages.class);
                    if (msg.userId.equals(FirebaseAuth.getInstance().getUid()))
                    {
                        // displaying the messages to the user
                        List<Item> itemList = new ArrayList<>();

                        for (String message : msg.messages)
                        {
                            itemList.add(new Item(message));
                        }
                        // Add more items as needed
                        CustomAdapter adapter = new CustomAdapter(itemList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(handleMsgs.this));
                    }
                }
            }
            // handling errors
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(handleMsgs.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentMsg = new Intent(handleMsgs.this, Menu.class);
                startActivity(intentMsg);
            }
        });
    }
}
