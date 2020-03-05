package com.example.ppm_project;

import android.location.Location;

public class HelpMessage{
    private Location senderLocation;
    private Carer recipient;
    private Patient sender;

    public void setSenderLocation(Location senderLocation) { this.senderLocation = senderLocation; }

    public void setRecipient(Carer recipient) { this.recipient = recipient; }

    public void setSender(Patient sender) { this.sender = sender; }

    public Location getSenderLocation() { return senderLocation; }

    public Carer getRecipient() { return recipient; }

    public Patient getSender() { return sender; }

    public void sendHelpMessage()
    {
        recipient.receiveMessage(this);

    }
}
