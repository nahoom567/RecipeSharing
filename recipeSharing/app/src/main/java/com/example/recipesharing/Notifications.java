package com.example.recipesharing;

import java.util.List;

public class Notifications
{
    String nameUser;
    List<String> recipesNotify;

    public Notifications(String nameUser, List<String> recipesNotify)
    {
        this.nameUser = nameUser;
        this.recipesNotify = recipesNotify;
    }

    public String getNameUser()
    {
        return nameUser;
    }

    public void setNameUser(String nameUser)
    {
        this.nameUser = nameUser;
    }

    public List<String> getRecipesNotify()
    {
        return recipesNotify;
    }

    public void setRecipesNotify(List<String> recipesNotify)
    {
        this.recipesNotify = recipesNotify;
    }
}
