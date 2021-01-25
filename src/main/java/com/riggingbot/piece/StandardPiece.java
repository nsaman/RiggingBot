package com.riggingbot.piece;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jacob on 7/12/2015.
 */
public abstract class StandardPiece extends Piece
{
    public static ArrayList<StandardPiece> pieces = populatePieces();

    public StandardPiece(Color centerColor)
    {
        super(centerColor);
    }

    protected static ArrayList<StandardPiece> populatePieces()
    {
        ArrayList<StandardPiece> pieces = new ArrayList<>();
        pieces.add(BlackPiece.INSTANCE);
        pieces.add(BluePiece.INSTANCE);
        pieces.add(BrownPiece.INSTANCE);
        pieces.add(DarkBluePiece.INSTANCE);
        pieces.add(GrayPiece.INSTANCE);
        pieces.add(LightBluePiece.INSTANCE);
        pieces.add(TanPiece.INSTANCE);
        pieces.add(YellowPiece.INSTANCE);
        return pieces;
    }
}
