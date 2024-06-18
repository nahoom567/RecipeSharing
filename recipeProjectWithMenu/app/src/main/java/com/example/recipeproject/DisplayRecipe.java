package com.example.recipeproject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisplayRecipe extends MenuActivity {
    FirebaseAuth mAuth;
    TextView nameRecipe;
    TextView instructions;
    RecyclerView ingRecycler;
    Button likeBtn;
    Button exitBtn;

    DatabaseReference recRef;
    ValueEventListener recListener;

    DatabaseReference nameRef;
    ValueEventListener nameListener;

    DatabaseReference ingRef;
    ValueEventListener ingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        // generating a unique jobId using UUID
        int jobId = UUID.randomUUID().hashCode();

        nameRecipe = findViewById(R.id.nameRecipe);
        instructions = findViewById(R.id.instructionsRecipe);
        ingRecycler = findViewById(R.id.ingredientsRecycler);
        likeBtn = findViewById(R.id.like);
        exitBtn = findViewById(R.id.exit);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String idUser = user.getUid();

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "did not deliver anything", Toast.LENGTH_SHORT).show();
            finish();
        }
        String name = extras.getString("STRING_KEY");
        if (name == null) {
            Toast.makeText(DisplayRecipe.this, "failed to deliver name of recipe between intents", Toast.LENGTH_SHORT).show();
            finish();
        }

        recRef = FirebaseDatabase.getInstance().getReference().child("recipes");
        // displaying recipe
        recListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foundRecipe = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String currName = userSnapshot.getKey();
                    if (currName.equals(name)) {
                        foundRecipe = true;
                        nameRecipe.setText(name);
                        nameRef = recRef.child(currName);
                        ingRef = nameRef.child("ingredients");

                        nameListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    if (item.getKey().equals("instructions")) {
                                        String insString = item.getValue(String.class);
                                        if (insString != null && !insString.isEmpty()) {
                                            instructions.setText(insString);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(DisplayRecipe.this, "failed to get the instructions", Toast.LENGTH_SHORT).show();
                            }
                        };
                        nameRef.addValueEventListener(nameListener);

                        ingListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Item> itemList = new ArrayList<>();
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    itemList.add(new Item(item.getValue(String.class)));
                                }
                                CustomAdapter adapter = new CustomAdapter(itemList);
                                ingRecycler.setAdapter(adapter);
                                ingRecycler.setLayoutManager(new LinearLayoutManager(DisplayRecipe.this));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(DisplayRecipe.this, "failed to get the ingredients", Toast.LENGTH_SHORT).show();
                            }
                        };
                        ingRef.addValueEventListener(ingListener);
                    }
                }
                if (!foundRecipe) {
                    Toast.makeText(DisplayRecipe.this, "failed to find recipe that matches with the given name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayRecipe.this, "failed to get any recipes", Toast.LENGTH_SHORT).show();
            }
        };

        // like button functionality
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                ComponentName componentName = new ComponentName(DisplayRecipe.this, UpdateDatabase.class);
                PersistableBundle extras = new PersistableBundle();
                String msgToUser = "You have liked: " + nameRecipe.getText().toString();
                Log.d("your message to user", msgToUser);
                extras.putString("messageToAdd", msgToUser);
                extras.putString("idUser", idUser);

                JobInfo jobInfo = new JobInfo.Builder(jobId, componentName)
                        .setMinimumLatency(120000)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setExtras(extras)
                        .build();

                int resultCode = jobScheduler.schedule(jobInfo);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Toast.makeText(DisplayRecipe.this, "Job was successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DisplayRecipe.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDisplay = new Intent(DisplayRecipe.this, Menu.class);
                TransitionUtility.startActivityWithTransition(DisplayRecipe.this, intentDisplay);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recRef.addValueEventListener(recListener);
        if (nameRef != null) {
            nameRef.addValueEventListener(nameListener);
        }
        if (ingRef != null) {
            ingRef.addValueEventListener(ingListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        recRef.removeEventListener(recListener);
        if (nameRef != null) {
            nameRef.removeEventListener(nameListener);
        }
        if (ingRef != null) {
            ingRef.removeEventListener(ingListener);
        }
    }
}
