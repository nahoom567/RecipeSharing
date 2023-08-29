package com.example.recipesharing;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class handleAddRecipes extends AppCompatActivity
{
    FirebaseAuth mAuth;
    EditText editNameRecipe;
    EditText editInstructions;
    EditText editIngredient;
    Button ingredientsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        // the nameUser cannot change from its first initialization because it gets its value from msgSnapshot
        // you can't reassign a final variable after it's been initialized which is why this had to have been done like this
        final String[] nameUser = {null};

        editNameRecipe = findViewById(R.id.editNameRecipe);
        editInstructions = findViewById(R.id.editInstructions);
        editIngredient = findViewById(R.id.editIngredients);
        ingredientsButton = findViewById(R.id.ingredientsButton);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String idUser = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = mDatabase.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren())
                {
                    // finding the messages that are for this user
                    String userId = msgSnapshot.getValue(String.class);
                    if (userId.equals(idUser))
                    {
                        nameUser[0] = msgSnapshot.child("name").getValue(String.class);
                    }
                }
            }
            // handling errors
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(handleAddRecipes.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        ingredientsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String ingredient = editIngredient.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient))
                {
                    DatabaseReference ingRef = FirebaseDatabase.getInstance().getReference().child("recipes").child(editNameRecipe.toString()).child("ingredients");
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("recipes").child(editNameRecipe.toString()).child("username");
                    userRef.setValue(nameUser[0]);
                    FirebaseDatabase.getInstance().getReference().child("recipes").child(editNameRecipe.toString()).child("instructions").setValue(editInstructions.toString());
                    // generating a unique key for the task
                    String taskId = ingRef.push().getKey();
                    ingRef.child(taskId).setValue(ingredient);
                    editIngredient.setText(""); // clearing the EditText
                }
            }
        });
    }
}
