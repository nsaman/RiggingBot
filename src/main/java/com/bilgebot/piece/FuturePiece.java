package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/13/2015.
 */
public class FuturePiece extends Piece
{
    public static final FuturePiece INSTANCE = new FuturePiece();

    private FuturePiece()
    {
        super(new Color(0, 0, 0));
    }
}
