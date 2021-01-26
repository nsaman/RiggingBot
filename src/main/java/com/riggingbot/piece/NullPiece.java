package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/16/2015.
 */
public class NullPiece extends SpecialPiece
{
    public static final NullPiece INSTANCE = new NullPiece();

    private NullPiece()
    {
        super(new Color(10, 99, 99));
    }
}
