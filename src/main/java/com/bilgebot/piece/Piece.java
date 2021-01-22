package com.bilgebot.piece;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jacob on 7/13/2015.
 */
public abstract class Piece
{
    private Color centerColor;

    public static ArrayList<Piece> pieces = populatePieces();

    private static ArrayList<Piece> populatePieces()
    {
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(0, NullPiece.INSTANCE);
        pieces.addAll(StandardPiece.populatePieces());
        return pieces;
    }

    public Piece(Color centerColor)
    {
        this.centerColor = centerColor;
    }

    public static byte getPieceIndex(Piece piece)
    {
        if(piece == null)
        {
            return 0;
        }

        return (byte) pieces.indexOf(piece);
    }

    public boolean isColorPiece(Color color)
    {
        return color.equals(centerColor);
    }

    public Color getCenterColor()
    {
        return centerColor;
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass().equals(this.getClass());
    }
}
