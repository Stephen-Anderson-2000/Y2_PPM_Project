package com.example.ppm_project;

import java.net.URL;

public class HelpMessage{
    private String senderLocation;
    private Carer recipient;
    private Patient sender;

    public void setSenderLocation(String senderLocation) { this.senderLocation = senderLocation; }

    public void setRecipient(Carer recipient) { this.recipient = recipient; }

    public void setSender(Patient sender) { this.sender = sender; }

    public String getSenderLocation() { return senderLocation; }

    public Carer getRecipient() { return recipient; }

    public Patient getSender() { return sender; }

    public void sendHelpMessage()
    {
      //  recipient.receiveMessage(this);
    }
}
