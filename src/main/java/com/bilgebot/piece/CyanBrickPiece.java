package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/13/2015.
 */
public class CyanBrickPiece extends StandardPiece
{
    public static final CyanBrickPiece INSTANCE = new CyanBrickPiece();

    private CyanBrickPiece()
    {
        super(new Color(42, 169, 208), new Color(17, 113, 180));
    }
}
