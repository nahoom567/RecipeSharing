package com.example.recipesharing;

import java.util.List;

public class likedRecipes
{
    String nameUser;
    List<String> recipesLiked;

    public likedRecipes(String nameUser, List<String> recipes)
    {
        this.nameUser = nameUser;
        this.recipesLiked = recipes;
    }

    public String getNameUser()
    {
        return nameUser;
    }

    public void setNameUser(String nameUser)
    {
        this.nameUser = nameUser;
    }

    public List<String> getRecipesLiked()
    {
        return recipesLiked;
    }

    public void setRecipesLiked(List<String> recipes)
    {
        this.recipesLiked = recipes;
    }
}
