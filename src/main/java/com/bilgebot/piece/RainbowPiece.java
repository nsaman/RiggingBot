package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class RainbowPiece extends WildPiece
{
    public static final RainbowPiece INSTANCE = new RainbowPiece();

    private RainbowPiece()
    {
        super(new Color(180, 180 , 180));
    }
}
