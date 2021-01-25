package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class YellowPiece extends StandardPiece
{
    public static final YellowPiece INSTANCE = new YellowPiece();

    private YellowPiece()
    {
        super(new Color(225, 226 , 90));
    }
}
