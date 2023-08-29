package com.example.recipesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class handleSearch extends AppCompatActivity {
    Button btSearch;
    EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_search);
        btSearch = findViewById(R.id.search_btn);
        editSearch = findViewById(R.id.search);

        btSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String searchedRecipe = editSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(searchedRecipe))
                {
                    DatabaseReference recRef = FirebaseDatabase.getInstance().getReference().child("recipes");
                    recRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            for (DataSnapshot userSnapshot : snapshot.getChildren())
                            {
                                String name = userSnapshot.getKey();
                                if (name == searchedRecipe)
                                {
                                    // sending an intent that will display recipe and options
                                    // of what to do with the recipe
                                    Intent intentDisplay = new Intent(handleSearch.this, displayRecipe.class);
                                    startActivity(intentDisplay);
                                    // using finish in order to make sure that the toast message after
                                    // the for loop won't run
                                    finish();
                                }
                            }
                            Toast.makeText(handleSearch.this, "failed to find recipe that matches with the give name", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            Toast.makeText(handleSearch.this, "failed to get any recipes", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }
    }
}