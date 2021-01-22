package com.bilgebot;

import com.bilgebot.piece.Piece;
import com.bilgebot.piece.StandardPiece;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Jacob on 7/12/2015.
 */
public class PieceSearch
{

    public static Board searchPieces(BufferedImage screenCapture)
    {
        Board board = new Board();

        for(int y = 0; y < NUM_ROWS; y++)
        {
            int pieceCountInRow = CENTER_ROW_COUNT - Math.abs(ROW_OFFSET - y);
            int yPos = Y_BOARD_OFFSET + PIECE_HEIGHT * y;
            for (int x = 0; x < pieceCountInRow; x++)
            {
                int rowIdent = (CENTER_ROW_COUNT - pieceCountInRow) / 2;
                int isEvenPieceOffset = pieceCountInRow % 2 == 1 ? 0 : PIECE_WIDTH / 2;

                int xPos = X_BOARD_OFFSET + rowIdent * PIECE_WIDTH + isEvenPieceOffset + PIECE_WIDTH * x;
                Color color = new Color(screenCapture.getRGB(xPos, yPos));

                for (int i = 0; i < StandardPiece.pieces.size(); i++)
                {
                    if(StandardPiece.pieces.get(i).isColorPiece(color))
                    {
                        Piece piece = StandardPiece.pieces.get(i);
                        board.getPieces()[y][x] = piece;
                    }
                }
            }
        }
        return board;
    }

    public final static int CENTER_ROW_COUNT = 9;
    public final static int PIECES_PER_ROW = 6;
    public final static int NUM_ROWS = 9;
    public final static int ROW_OFFSET = 4;
    public final static int PIECE_WIDTH = 44;
    public final static int PIECE_HEIGHT = 38;
    public final static int X_BOARD_OFFSET = 39;
    public final static int Y_BOARD_OFFSET = 74;
}
