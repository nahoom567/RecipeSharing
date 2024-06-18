package com.example.recipeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class Search extends MenuActivity {
    Button btSearch;
    EditText editSearch;
    DatabaseReference recRef;
    ValueEventListener searchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_search);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        btSearch = findViewById(R.id.search_btn);
        editSearch = findViewById(R.id.search);

        recRef = FirebaseDatabase.getInstance().getReference().child("recipes");

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedRecipe = editSearch.getText().toString().trim();
                SearchCorrector corrector = new SearchCorrector();

                searchListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // checking if the recipe was found
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String currName = userSnapshot.getKey();
                            corrector.addWordToDatabase(currName);
                        }

                        // Using NLP to check whether there is a similar recipe name
                        String strCorrector = corrector.correctSearchQuery(searchedRecipe);
                        if (!strCorrector.equals("No similar word found")) {
                            Intent intentDisplay = new Intent(Search.this, DisplayRecipe.class);
                            intentDisplay.putExtra("STRING_KEY", strCorrector);
                            TransitionUtility.startActivityWithTransition(Search.this, intentDisplay);
                        } else {
                            Toast.makeText(Search.this, "Didn't find any similar recipe", Toast.LENGTH_SHORT).show();
                        }

                        // removing the listener after the search is complete
                        recRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Search.this, "Failed to get any recipes", Toast.LENGTH_SHORT).show();
                        recRef.removeEventListener(this);
                    }
                };

                recRef.addValueEventListener(searchListener);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the listener is removed when the activity is destroyed
        if (recRef != null && searchListener != null) {
            recRef.removeEventListener(searchListener);
        }
    }
}
