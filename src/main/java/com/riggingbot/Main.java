package com.riggingbot;

import javax.swing.*;

public class Main
{

    /**
     * Program entry point. Creates a RiggingBot
     * @param args unused
     */
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e)
        {
        } catch (InstantiationException e)
        {
        } catch (IllegalAccessException e)
        {
        } catch (UnsupportedLookAndFeelException e)
        {
        }

        new RiggingBot();
    }

}


