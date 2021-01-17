package com.knox.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/15/2015.
 */
public class PentagonPiece extends StandardPiece
{
    public static final PentagonPiece INSTANCE = new PentagonPiece();

    private PentagonPiece()
    {
        super(new Color(87, 189, 235), new Color(35, 121, 191));
    }
}
