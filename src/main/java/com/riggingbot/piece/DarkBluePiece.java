package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class DarkBluePiece extends StandardPiece
{
    public static final DarkBluePiece INSTANCE = new DarkBluePiece();

    private DarkBluePiece()
    {
        super(new Color(41, 15 , 186));
    }
}
