package com.example.recipeproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

public class Menu extends AppCompatActivity {
    Button buttonSearch;
    Button buttonRecipe;
    Button buttonMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        buttonSearch = findViewById(R.id.search_btn);
        buttonRecipe = findViewById(R.id.addRecipe);
        buttonMessages = findViewById(R.id.messages);

        // handling searches
        buttonSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentSearch = new Intent(Menu.this, Search.class);
                TransitionUtility.startActivityWithTransition(Menu.this, intentSearch);
            }
        });

        // handling adding of recipes
        buttonRecipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentAdd = new Intent(Menu.this, AddRecipes.class);
                TransitionUtility.startActivityWithTransition(Menu.this, intentAdd);
            }
        });

        // handling messages
        buttonMessages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentMsgs = new Intent(Menu.this, HandleMessages.class);
                TransitionUtility.startActivityWithTransition(Menu.this, intentMsgs);
            }
        });
    }
}
