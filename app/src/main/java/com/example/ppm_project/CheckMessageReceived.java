package com.example.ppm_project;

public class CheckMessageReceived implements Runnable{

    Carer currentCarer;
    private boolean messageIsReceived = false;

    public CheckMessageReceived(Carer theCarer)
    {
        currentCarer = theCarer;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                HelpMessage receivedMessage = currentCarer.getTheReceivedMessage();
                if (receivedMessage != null)
                {
                    System.out.println("Received the help message from " + receivedMessage.getSender().getFirstName());
                    messageIsReceived = true;
                    // display the popup
                    break;
                }
                Thread.sleep(1000);
            }
            catch (InterruptedException e) { }
        }
    }
}
