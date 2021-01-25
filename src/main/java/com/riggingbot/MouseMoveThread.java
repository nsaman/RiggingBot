package com.riggingbot;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Created by Jacob on 7/14/2015.
 */
public class MouseMoveThread extends Thread
{
    private Robot robot;
    private boolean operable = true;
    private boolean clicked = false;
    private long initMoveTime;
    private long totalMoveTime;

    //y=a(x-h)^2+k
    private double _a;
    private double _h;
    private double _k;

    private int targetX;
    private int targetY;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int initX;
    private int initY;
    private double netDistance;

    private int prevMoveX = -1;
    private int prevMoveY = -1;

    private boolean hasMove = false;

    public static MouseMoveThread INSTANCE;

    public MouseMoveThread()
    {
        super("Mouse Move Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MAX_PRIORITY);
        try
        {
            robot = new Robot();
        } catch (AWTException e)
        {
            Status.I.log("Could not create MouseMoveThread robot", Status.Severity.ERROR);
            e.printStackTrace();
            operable = false;
        }

        INSTANCE = this;
    }

    public void shutdown()
    {
        operable = false;
    }

    @Override
    public void run()
    {
        while (operable)
        {
            try //Wait for a move
            {
                sleep(10);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            if(hasMove())
            {
                initMoveTime = System.currentTimeMillis();
            }

            while (hasMove && operable)
            {
                if(!MouseInfo.getPointerInfo().getLocation().equals(new Point(prevMoveX, prevMoveY)))
                {
                    System.out.println("mouse interrupted, resetting");
                    try
                    {
                        sleep(5000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    setTargetDestination(startX, startY);
                    clicked = false;
                    initMoveTime = System.currentTimeMillis();
                    continue;
                }

                Point point = calculateMousePosition(System.currentTimeMillis() - initMoveTime);

                robot.mouseMove(point.x, point.y);
                prevMoveX = point.x;
                prevMoveY = point.y;

                if(point.x == targetX && point.y == targetY)
                {
                    if(!clicked) {
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        clicked = true;
                        try
                        {
                            System.out.println("sleeping after down click");
                            sleep(100);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        initMoveTime = System.currentTimeMillis();
                        setTargetDestination(endX, endY);
                    } else {
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        try
                        {
                            sleep(100);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        hasMove = false;
                        clicked = false;
                    }

                }
            }
        }
    }

    public long setStartDestination(int x, int y) {
        startX = x;
        startY = y;

        System.out.println("setting start at x="+x+" y="+y);

        return setTargetDestination(x, y);
    }

    public long setTargetDestination(int x, int y) {
        targetX = x;
        targetY = y;

        System.out.println("setting target at x="+x+" y="+y);

        initX = MouseInfo.getPointerInfo().getLocation().x;
        initY = MouseInfo.getPointerInfo().getLocation().y;

        prevMoveX = initX;
        prevMoveY = initY;

        _h = initX;
        _k = initY;

        // a=(y-k)/(x-h)^2
        _a = (y - _k) / (Math.pow(x - _h, 2));

        netDistance = Math.sqrt(Math.pow(targetX - initX, 2) + Math.pow(targetY - initY, 2));
        totalMoveTime = (long) (netDistance*2 + 300);
        hasMove = true;

        return totalMoveTime;
    }

    public void setEndDestination(int x, int y)
    {
        endX = x;
        endY = y;
    }

    private Point calculateMousePosition(long deltaTime)
    {
        if(deltaTime > totalMoveTime)
        {
            return new Point(targetX, targetY);
        }

        double percentDone = ((double) deltaTime) / totalMoveTime;
        int x = (int) (percentDone * (targetX - initX)) + initX;
        int y = (int) (((percentDone * (targetY - initY)) + initY)/2 + (_a * Math.pow(x - _h, 2) + _k)/2);

        return new Point(x, y);
    }

    public boolean hasMove()
    {
        return hasMove;
    }
}
