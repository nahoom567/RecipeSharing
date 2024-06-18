package com.example.recipeproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRecipes extends MenuActivity {
    FirebaseAuth mAuth;
    EditText editNameRecipe;
    EditText editInstructions;
    EditText editIngredient;
    Button ingredientsButton;
    DatabaseReference usersRef;
    /*
    You will see this ValueEventListener a lot during this application.
    This is used in order to fix the issue where the onDataChange callback
    is triggered even when you're in another application, because the
    realtime database can change outside of this application
    */
    ValueEventListener userListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_add_recipes);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        final String[] nameUser = {null};

        editNameRecipe = findViewById(R.id.editNameRecipe);
        editInstructions = findViewById(R.id.editInstructions);
        editIngredient = findViewById(R.id.editIngredients);
        ingredientsButton = findViewById(R.id.ingredientsButton);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String idUser = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        usersRef = mDatabase.getReference("users");

        // getting name of user
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    String userId = msgSnapshot.getKey();
                    if (userId.equals(idUser)) {
                        nameUser[0] = msgSnapshot.child("name").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddRecipes", "got to on canceled");
                Toast.makeText(AddRecipes.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        usersRef.addValueEventListener(userListener);


        // adding recipe/ingredient/changing it
        ingredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = editIngredient.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(editNameRecipe.getText().toString())
                        && !TextUtils.isEmpty(editInstructions.getText().toString())) {
                    DatabaseReference recRef = FirebaseDatabase.getInstance().getReference().child("recipes");
                    DatabaseReference ingRef = recRef.child(editNameRecipe.getText().toString()).child("ingredients");
                    DatabaseReference userRef = recRef.child(editNameRecipe.getText().toString()).child("username");

                    userRef.setValue(nameUser[0]);
                    recRef.child(editNameRecipe.getText().toString()).child("instructions").setValue(editInstructions.getText().toString());
                    String taskId = ingRef.push().getKey();
                    ingRef.child(taskId).setValue(ingredient);
                    editIngredient.setText(""); // clearing the EditText
                } else {
                    Toast.makeText(AddRecipes.this, "Please fill all the needed information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // onDestroy() is called when the system is freeing up resources.
    // This happens here when the activity is finishing or being destroyed by the system
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usersRef != null && userListener != null) {
            usersRef.removeEventListener(userListener);
        }
    }
}
