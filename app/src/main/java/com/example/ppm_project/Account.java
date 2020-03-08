package com.example.ppm_project;

import androidx.appcompat.app.AppCompatActivity;

public class Account{
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    //private int MobileNumber;
    private String UserID;
    private Boolean IsCarer;
    private String ProfileURL;

    public Account(){

    }

    public void setFirstName(String firstName) { FirstName = firstName; }

    public void setLastName(String lastName) { LastName = lastName; }

    public void setEmailAddress(String emailAddress) { EmailAddress = emailAddress; }

    //public void setMobileNumber(int mobileNumber) { MobileNumber = mobileNumber; }

    public void setUserID(String userID) { UserID = userID; }

    public void setIsCarer(Boolean isCarer){IsCarer = isCarer;}

    public void setProfileURL(String profileURL) { ProfileURL = profileURL; }

    public String getFirstName() { return FirstName; }

    public String getLastName() { return LastName; }

    public String getEmailAddress() { return EmailAddress; }

    //public int getMobileNumber() { return MobileNumber; }

    public String getUserID() { return UserID; }

    public Boolean getIsCarer(){return IsCarer;}
}
