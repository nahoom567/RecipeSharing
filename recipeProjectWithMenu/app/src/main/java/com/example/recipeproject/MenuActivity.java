package com.example.recipeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Menu;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class MenuActivity extends AppCompatActivity{
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_id && !getClass().equals(AddRecipes.class)) {
            Intent intentAdd = new Intent(this, AddRecipes.class);
            TransitionUtility.startActivityWithTransition(MenuActivity.this, intentAdd);
        } else if (id == R.id.search_id && !getClass().equals(Search.class)) {
            Intent intentSearch = new Intent(this, Search.class);
            TransitionUtility.startActivityWithTransition(MenuActivity.this, intentSearch);
        } else if (id == R.id.messages_id && !getClass().equals(HandleMessages.class)) {
            Intent intentMsg = new Intent(this, HandleMessages.class);
            TransitionUtility.startActivityWithTransition(MenuActivity.this, intentMsg);
        }

        return super.onOptionsItemSelected(item);
    }
}
