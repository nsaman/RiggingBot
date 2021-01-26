package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class GaffPiece extends WildPiece
{
    public static final GaffPiece INSTANCE = new GaffPiece();

    private GaffPiece()
    {
        super(new Color(48, 48 , 48));
    }
}
