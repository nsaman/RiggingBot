package com.riggingbot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class BrownPiece extends StandardPiece
{
    public static final BrownPiece INSTANCE = new BrownPiece();

    private BrownPiece()
    {
        super(new Color(201, 150 , 24));
    }
}
