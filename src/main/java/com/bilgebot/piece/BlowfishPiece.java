package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/13/2015.
 */
public class BlowfishPiece extends Piece
{
    public static final BlowfishPiece INSTANCE = new BlowfishPiece();

    private BlowfishPiece()
    {
        super(new Color(250, 242, 68), new Color(100, 142, 124));
    }

}
