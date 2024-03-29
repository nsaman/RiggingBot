package com.riggingbot.piece;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jacob on 7/12/2015.
 */
public abstract class WildPiece extends SpecialPiece
{
    public static ArrayList<SpecialPiece> pieces = populatePieces();

    public WildPiece(Color centerColor)
    {
        super(centerColor);
    }

    protected static ArrayList<SpecialPiece> populatePieces()
    {
        ArrayList<SpecialPiece> pieces = new ArrayList<>();
        pieces.add(GaffPiece.INSTANCE);
        pieces.add(TarPiece.INSTANCE);
        pieces.add(RainbowPiece.INSTANCE);
        return pieces;
    }
}
