package com.knox.bilgebot.piece;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jacob on 7/12/2015.
 */
public abstract class StandardPiece extends Piece
{
    public static ArrayList<StandardPiece> pieces = populatePieces();

    public StandardPiece(Color centerColor, Color centerColorWater)
    {
        super(centerColor, centerColorWater);
    }

    protected static ArrayList<StandardPiece> populatePieces()
    {
        ArrayList<StandardPiece> pieces = new ArrayList<>();
        pieces.add(BlueBrickPiece.INSTANCE);
        pieces.add(CyanMarblePiece.INSTANCE);
        pieces.add(ShellPiece.INSTANCE);
        pieces.add(TealMarblePiece.INSTANCE);
        pieces.add(WaveBrickPiece.INSTANCE);
        pieces.add(CyanBrickPiece.INSTANCE);
        pieces.add(PentagonPiece.INSTANCE);
        return pieces;
    }
}
