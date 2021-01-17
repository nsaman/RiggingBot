package com.knox.bilgebot.piece;

import java.awt.*;

/**
 * Created by Jacob on 7/12/2015.
 */
public class ShellPiece extends StandardPiece
{
    public static final ShellPiece INSTANCE = new ShellPiece();

    private ShellPiece()
    {
        super(new Color(136, 226, 197), new Color(54, 135, 176));
    }
}
