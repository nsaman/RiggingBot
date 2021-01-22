package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/16/2015.
 */
public class NullPiece extends Piece
{
    public static final NullPiece INSTANCE = new NullPiece();

    private NullPiece()
    {
        super(Color.BLACK);
    }
}
