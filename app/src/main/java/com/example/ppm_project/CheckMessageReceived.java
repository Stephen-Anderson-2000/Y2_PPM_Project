package com.example.ppm_project;

public class CheckMessageReceived implements Runnable{
    Carer currentCarer;

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
                    // display the popup
                }
                Thread.sleep(500);
            }
            catch (InterruptedException e) { }
        }
    }
}
