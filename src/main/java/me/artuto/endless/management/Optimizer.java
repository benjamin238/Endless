package me.artuto.endless.management;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.TimerTask;

public class Optimizer extends TimerTask
{
    public void run()
    {
        SimpleLog.getLog("Optimizer").info("Running optimizer...");
        //iDroid fucking idiot.
        System.gc();
        SimpleLog.getLog("Optimizer").info("Executed!");
    }
}
