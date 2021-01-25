package com.bilgebot.piece;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jacob on 7/12/2015.
 */
public abstract class SpecialPiece extends Piece
{
    public static ArrayList<SpecialPiece> pieces = populatePieces();

    public SpecialPiece(Color centerColor)
    {
        super(centerColor);
    }

    protected static ArrayList<SpecialPiece> populatePieces()
    {
        ArrayList<SpecialPiece> pieces = new ArrayList<>();
        pieces.add(FuturePiece.INSTANCE);
        pieces.add(NullPiece.INSTANCE);
        pieces.addAll(WildPiece.populatePieces());
        return pieces;
    }
}
