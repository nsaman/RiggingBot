package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class BlackPiece extends StandardPiece
{
    public static final BlackPiece INSTANCE = new BlackPiece();

    private BlackPiece()
    {
        super(new Color(97, 97 , 97));
    }
}
