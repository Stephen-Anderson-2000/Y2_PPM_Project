package com.example.ppm_project;

public class CurrentUserID {
    private static int theUser;

    private static AccountList theAccounts;

    public void setTheUser(int theUserID)
    {
        theUser = theUserID;
    }

    public int getTheUser() { return theUser; }
}
