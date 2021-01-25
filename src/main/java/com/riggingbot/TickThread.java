package com.riggingbot;

/**
 * Created by Jacob on 7/12/2015.
 */
public class TickThread extends Thread
{
    private RiggingBot riggingBot;
    private boolean shouldRun;

    public TickThread(RiggingBot riggingBot)
    {
        super("Bot Tick Thread");
        this.setDaemon(true);
        this.riggingBot = riggingBot;
        shouldRun = true;
    }

    @Override
    public void run()
    {
        while (shouldRun)
        {
            try
            {
                sleep(10);
            } catch (InterruptedException e)
            {
                //do nothing
            }
            long initTickTime = System.currentTimeMillis();
            riggingBot.tick();
            long tickTime = System.currentTimeMillis() - initTickTime;
            if (tickTime > 30)
            {
                System.out.println("Tick time: " + (System.currentTimeMillis() - initTickTime));
            }
        }

        riggingBot.getStatus().log("Tick thread shutting down");
    }

    public void shutdown()
    {
        shouldRun = false;
    }
}
