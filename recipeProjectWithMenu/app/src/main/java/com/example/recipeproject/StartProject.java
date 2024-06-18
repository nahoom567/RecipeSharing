package com.example.recipeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StartProject extends AppCompatActivity
{
    FirebaseAuth auth;
    Button button;
    Button continueButton;
    TextView textView;
    FirebaseUser user;
    FirebaseDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        continueButton = findViewById(R.id.continueUser);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        if (user == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            TransitionUtility.startActivityWithTransition(StartProject.this, intent);
            finish();
        }
        else
        {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                TransitionUtility.startActivityWithTransition(StartProject.this, intent);
                finish();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                TransitionUtility.startActivityWithTransition(StartProject.this, intent);
            }
        });
    }
}