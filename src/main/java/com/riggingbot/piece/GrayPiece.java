package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class GrayPiece extends StandardPiece
{
    public static final GrayPiece INSTANCE = new GrayPiece();

    private GrayPiece()
    {
        super(new Color(233, 233 , 233));
    }
}
