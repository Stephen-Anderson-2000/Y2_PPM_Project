package com.example.ppm_project;

public class Carer {
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String UserID;
    private String CloudID;

    public void setFirstName(String firstName) { FirstName = firstName; }

    public void setLastName(String lastName) { LastName = lastName; }

    public void setEmailAddress(String emailAddress) { EmailAddress = emailAddress; }

    public void setUserID(String userID) { UserID = userID; }

    public void setCloudID(String cloudID){CloudID = cloudID;}

    public String getFirstName() { return FirstName; }

    public String getUserID() { return UserID; }

    public String getCloudID(){return CloudID;};

}
