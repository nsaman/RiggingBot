package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class TarPiece extends WildPiece
{
    public static final TarPiece INSTANCE = new TarPiece();

    private TarPiece()
    {
        super(new Color(96, 96 , 100));
    }
}
