package com.example.ppm_project;

import java.util.ArrayList;

public class Carer {
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String MobileNumber;
    private String UserID;
    private String ProfileURL;
    private String CloudID;

    public void setFirstName(String firstName) { FirstName = firstName; }

    public void setLastName(String lastName) { LastName = lastName; }

    public void setEmailAddress(String emailAddress) { EmailAddress = emailAddress; }

    public void setMobileNumber(String mobileNumber) { MobileNumber = mobileNumber; }

    public void setUserID(String userID) { UserID = userID; }

    public void setCloudID(String cloudID){CloudID = cloudID;}

    public void setProfileURL(String profileURL) { ProfileURL = profileURL; }


    public String getFirstName() { return FirstName; }

    public String getLastName() { return LastName; }

    public String getEmailAddress() { return EmailAddress; }

    public String getMobileNumber() { return MobileNumber; }

    public String getUserID() { return UserID; }

    public String getCloudID(){return CloudID;};

}
