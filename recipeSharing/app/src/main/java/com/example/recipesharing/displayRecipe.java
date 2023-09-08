package com.example.recipesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class displayRecipe extends AppCompatActivity
{
    TextView nameRecipe;
    TextView instructions;
    RecyclerView ingRecycler;
    Button likeBtn;
    Button exitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        nameRecipe = findViewById(R.id.nameRecipe);
        instructions = findViewById(R.id.instructionsRecipe);
        ingRecycler = findViewById(R.id.ingredientsRecycler);
        likeBtn = findViewById(R.id.like);
        exitBtn = findViewById(R.id.exit);

        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            Toast.makeText(this, "did not deliver anything", Toast.LENGTH_SHORT).show();
            finish();
        }
        String name = extras.getString("STRING_KEY");
        if (name == null) {
            Toast.makeText(displayRecipe.this, "failed to deliver name of recipe between intents", Toast.LENGTH_SHORT).show();
            finish();
        }

        DatabaseReference recRef = FirebaseDatabase.getInstance().getReference().child("recipes");
        recRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in order to check if the recipe was found
                boolean foundRecipe = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    String currName = userSnapshot.getKey();
                    if (currName.equals(name)) {
                        foundRecipe = true;
                        nameRecipe.setText(name);
                        // getting the ingredients and the instructions
                        DatabaseReference nameRef = recRef.child(currName);
                        DatabaseReference ingrRef = nameRef.child("ingredients");
                        nameRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Toast.makeText(displayRecipe.this, "trying to change instructions", Toast.LENGTH_SHORT).show();
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    if (item.getKey().equals("instructions"))
                                    {
                                        String insString = item.getValue(String.class);

                                        if (!insString.isEmpty() && insString != null)
                                        {
                                            Toast.makeText(displayRecipe.this, "the string it got is: " + insString, Toast.LENGTH_SHORT).show();
                                            instructions.setText(insString);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(displayRecipe.this, "failed to get the instructions", Toast.LENGTH_SHORT).show();
                            }
                        });

                        ingrRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Item> itemList = new ArrayList<>();
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    itemList.add(new Item(item.getValue(String.class)));
                                }
                                CustomAdapter adapter = new CustomAdapter(itemList);
                                ingRecycler.setAdapter(adapter);
                                ingRecycler.setLayoutManager(new LinearLayoutManager(displayRecipe.this));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(displayRecipe.this, "failed to get the ingredients", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // using finish in order to make sure that the toast message after
                        // the for loop won't run
                    }
                }
                if (!foundRecipe)
                {
                    Toast.makeText(displayRecipe.this, "failed to find recipe that matches with the given name", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(displayRecipe.this, "failed to get any recipes", Toast.LENGTH_SHORT).show();
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference().child("liked");
                DatabaseReference userRef = likedRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                // using push in order to not delete an existing child if it already does exist
                DatabaseReference newChild = userRef.push();
                newChild.setValue(name);

                // using JobScheduler and not AlarmManager + Notification because the remainder should
                // be useful the for user even if he/her already left the app, and also in case
                // the user would want to know his liked recipes
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                ComponentName componentName = new ComponentName(displayRecipe.this, updateDatabase.class);
                // Create a bundle to pass data to the job
                PersistableBundle extras = new PersistableBundle();
                extras.putString("messageToAdd", "Your message goes here");

                JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                        .setMinimumLatency(2 * 60 * 1000)  // 2 minutes in milliseconds
                        .setExtras(extras)  // Pass the message as an extra
                        .build();

                int resultCode = jobScheduler.schedule(jobInfo);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Toast.makeText(displayRecipe.this, "Job was successful", Toast.LENGTH_SHORT).show();
                }

            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intentDisplay = new Intent(displayRecipe.this, Menu.class);
                startActivity(intentDisplay);
            }
        });
    }
}