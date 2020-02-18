package com.example.ppm_project;

public class HelpMessage {
    private LocationData senderLocation;
    private Carer recipient;
    private Patient sender;

    public void setSenderLocation(LocationData senderLocation) { this.senderLocation = senderLocation; }

    public void setRecipient(Carer recipient) { this.recipient = recipient; }

    public void setSender(Patient sender) { this.sender = sender; }

    public LocationData getSenderLocation() { return senderLocation; }

    public Carer getRecipient() { return recipient; }

    public Patient getSender() { return sender; }
}
