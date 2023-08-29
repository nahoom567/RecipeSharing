package com.example.recipesharing;

public class Recipe
{
    // listIngredients - a string with commas separating each ingredients
    // instructions - a string that explains how to make the recipe
    String nameRecipe, nameUser, listIngredients, instructions;

    public Recipe()
    {

    }

    public Recipe(String nameRecipe, String nameUser, String listIngredients, String instructions)
    {
        this.nameRecipe = nameRecipe;
        this.nameUser = nameUser;
        this.listIngredients = listIngredients;
        this.instructions = instructions;
    }

    public String getNameRecipe()
    {
        return nameRecipe;
    }

    public void setNameRecipe(String nameRecipe)
    {
        this.nameRecipe = nameRecipe;
    }

    public String getNameUser()
    {
        return nameUser;
    }

    public void setNameUser(String nameUser)
    {
        this.nameUser = nameUser;
    }

    public String getListIngredients()
    {
        return listIngredients;
    }

    public void setListIngredients(String listIngredients)
    {
        this.listIngredients = listIngredients;
    }

    public String getInstructions()
    {
        return instructions;
    }

    public void setInstructions(String instructions)
    {
        this.instructions = instructions;
    }
}
