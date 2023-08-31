package com.example.recipesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class displayRecipe extends AppCompatActivity
{
    TextView nameRecipe;
    TextView instructions;
    RecyclerView ingRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        nameRecipe = findViewById(R.id.nameRecipe);
        instructions = findViewById(R.id.instructionsRecipe);
        ingRecycler = findViewById(R.id.ingredientsRecycler);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            Toast.makeText(this, "did not deliver recipe name", Toast.LENGTH_SHORT).show();
            finish();
        }
        String name = extras.getString("STRING_KEY");
        if (name == null)
        {
            Toast.makeText(displayRecipe.this, "failed to deliver name of recipe between intents", Toast.LENGTH_SHORT).show();
            finish();
        }

        DatabaseReference recRef = FirebaseDatabase.getInstance().getReference().child("recipes");
        recRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    String currName = userSnapshot.getKey();
                    if (currName.equals(name))
                    {
                        nameRecipe.setText(name);
                        // there is a problem with using the name of recipe to get all the rest since we have done it so the key is id of user


                        //insRecipe = userSnapshot.child();
                        // using finish in order to make sure that the toast message after
                        // the for loop won't run
                        finish();
                    }
                }
                Toast.makeText(displayRecipe.this, "failed to find recipe that matches with the give name", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(displayRecipe.this, "failed to get any recipes", Toast.LENGTH_SHORT).show();
            }
        });

    }
}