package me.artuto.endless.management;

import java.util.TimerTask;

public class Optimizer extends TimerTask
{
    public void run()
    {
        //iDroid fucking idiot.
        System.gc();
    }

    public static void shutdown() throws InterruptedException
    {
        Thread.sleep(5000);
    }
}
