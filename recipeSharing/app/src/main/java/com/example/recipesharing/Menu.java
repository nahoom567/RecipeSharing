package com.example.recipesharing;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
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
                Intent intentSearch = new Intent(Menu.this, handleSearch.class);
                startActivity(intentSearch);
            }
        });

        // handling adding of recipes
        buttonRecipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentAdd = new Intent(Menu.this, handleAddRecipes.class);
                startActivity(intentAdd);
            }
        });

        // handling messages
        buttonMessages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentMsgs = new Intent(Menu.this, handleMsgs.class);
                startActivity(intentMsgs);
            }
        });
    }
}
