package com.example.ppm_project;

public class Account {
    private String firstName;
    private String lastName;
    private String emailAddress;
    //private int mobileNumber;
    private int userID;
    private Boolean isCarer;
    private String profileURL;

    public Account(){

    }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    //public void setMobileNumber(int mobileNumber) { this.mobileNumber = mobileNumber; }

    public void setUserID(int userID) { this.userID = userID; }

    public void setIsCarer(Boolean isCarer){this.isCarer = isCarer;}

    public void setProfileURL(String profileURL) { this.profileURL = profileURL; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmailAddress() { return emailAddress; }

    //public int getMobileNumber() { return mobileNumber; }

    public int getUserID() { return userID; }

    public Boolean getIsCarer(){return isCarer;}
}
