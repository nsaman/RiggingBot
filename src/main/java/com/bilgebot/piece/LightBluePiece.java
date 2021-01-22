package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class LightBluePiece extends StandardPiece
{
    public static final LightBluePiece INSTANCE = new LightBluePiece();

    private LightBluePiece()
    {
        super(new Color(66, 182 , 224));
    }
}
