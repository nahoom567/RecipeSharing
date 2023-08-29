package com.example.recipesharing;

import java.util.List;

public class Messages
{
    String userId;
    String nameUser;
    List<String> messages;

    public Messages(String nameUser, List<String> messages, String userId)
    {
        this.nameUser = nameUser;
        this.messages = messages;
        this.userId = userId;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public String getNameUser()
    {
        return nameUser;
    }

    public void setNameUser(String nameUser)
    {
        this.nameUser = nameUser;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    public void setMessages(List<String> messages)
    {
        this.messages = messages;
    }
}
