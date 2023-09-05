package com.example.recipesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        nameRecipe = findViewById(R.id.nameRecipe);
        instructions = findViewById(R.id.instructionsRecipe);
        ingRecycler = findViewById(R.id.ingredientsRecycler);
        likeBtn = findViewById(R.id.like);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Toast.makeText(this, "did not deliver recipe name", Toast.LENGTH_SHORT).show();
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
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String currName = userSnapshot.getKey();
                    if (currName.equals(name)) {
                        nameRecipe.setText(name);
                        // getting the ingredients and the instructions
                        DatabaseReference nameRef = recRef.child(currName);
                        DatabaseReference instRef = nameRef.child("instructions");
                        DatabaseReference ingrRef = nameRef.child("ingredients");
                        instRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    String insString = item.getValue(String.class);
                                    instructions.setText(insString);
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
                        finish();
                    }
                }
                Toast.makeText(displayRecipe.this, "failed to find recipe that matches with the give name", Toast.LENGTH_SHORT).show();
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
            }
        });
    }
}