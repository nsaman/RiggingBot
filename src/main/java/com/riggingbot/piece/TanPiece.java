package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class TanPiece extends StandardPiece
{
    public static final TanPiece INSTANCE = new TanPiece();

    private TanPiece()
    {
        super(new Color(255, 249 , 225));
    }
}
