package com.bilgebot;

/**
 * Created by Jacob on 7/20/2015.
 */
public class InitThread extends Thread
{
    private final RiggingBot riggingBot;
    private boolean overlay;
    private int depth;
    private boolean auto;

    public InitThread(RiggingBot riggingBot, int depth, boolean auto, boolean overlay)
    {
        super("Bilge Bot init thread");
        this.setDaemon(true);
        this.riggingBot = riggingBot;
        this.depth = depth;
        this.auto = auto;
        this.overlay = overlay;
    }

    @Override
    public void run()
    {
        riggingBot.init(depth, auto, overlay);
    }
}
