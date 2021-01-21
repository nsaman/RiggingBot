package com.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/15/2015.
 */
public class JellyfishPiece extends Piece
{
    public static final JellyfishPiece INSTANCE = new JellyfishPiece();

    private JellyfishPiece()
    {
        super(new Color(255, 129, 217), new Color(102, 97, 184));
    }
}
