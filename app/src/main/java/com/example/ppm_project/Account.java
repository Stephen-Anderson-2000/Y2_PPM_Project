package com.example.ppm_project;

import androidx.appcompat.app.AppCompatActivity;

public class Account{
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String MobileNumber;
    private String UserID;
    private Boolean IsCarer;
    private String ProfileURL;
    private String CloudID;

    public Account(){

    }

    public void setFirstName(String firstName) { FirstName = firstName; }

    public void setLastName(String lastName) { LastName = lastName; }

    public void setEmailAddress(String emailAddress) { EmailAddress = emailAddress; }

    public void setMobileNumber(String mobileNumber) { MobileNumber = mobileNumber; }

    public void setUserID(String userID) { UserID = userID; }

    public void setIsCarer(Boolean isCarer){IsCarer = isCarer;}

    public void setProfileURL(String profileURL) { ProfileURL = profileURL; }

    public void setCloudID(String cloudID){CloudID = cloudID;}

    public String getFirstName() { return FirstName; }

    public String getLastName() { return LastName; }

    public String getEmailAddress() { return EmailAddress; }

    public String getMobileNumber() { return MobileNumber; }

    public String getUserID() { return UserID; }

    public Boolean getIsCarer(){return IsCarer;}

    public String getCloudID(){return CloudID;};

}
