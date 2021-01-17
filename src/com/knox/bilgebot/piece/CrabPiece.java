package com.knox.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/15/2015.
 */
public class CrabPiece extends Piece
{
    public static final CrabPiece INSTANCE = new CrabPiece();

    private CrabPiece()
    {
        super(new Color(83, 100, 97), new Color(83, 100, 97));
    }
}
