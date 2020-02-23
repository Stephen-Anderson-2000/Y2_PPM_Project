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
                if (currentCarer.getTheReceivedMessage() != null)
                {
                    System.out.println("Received the message");
                    messageIsReceived = true;
                    // display the popup
                }
                Thread.sleep(1000);
            }
            catch (InterruptedException e) { }
        }
    }
}
