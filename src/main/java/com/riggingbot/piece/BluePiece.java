package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class BluePiece extends StandardPiece
{
    public static final BluePiece INSTANCE = new BluePiece();

    private BluePiece()
    {
        super(new Color(33, 99 , 242));
    }
}
