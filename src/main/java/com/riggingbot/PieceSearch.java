package com.riggingbot;

import com.riggingbot.piece.Piece;
import com.riggingbot.piece.SpliceDownLeftPiece;
import com.riggingbot.piece.SpliceDownRightPiece;
import com.riggingbot.piece.SpliceHorizontalPiece;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Created by Jacob on 7/12/2015.
 */
public class PieceSearch
{
    public final static int CENTER_ROW_COUNT = 9;
    public final static int NUM_ROWS = 9;
    public final static int ROW_OFFSET = 4;
    public final static int PIECE_WIDTH = 44;
    public final static int PIECE_HEIGHT = 38;
    public final static int X_BOARD_OFFSET = 39;
    public final static int Y_BOARD_OFFSET = 57;

    public static Board searchPieces(BufferedImage screenCapture)
    {
        Board board = new Board();

        board.setActiveRig(findActiveRig(screenCapture));

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

                for (int i = 0; i < Piece.pieces.size(); i++)
                {
                    if(Piece.pieces.get(i).isColorPiece(color))
                    {
                        Piece piece = Piece.pieces.get(i);
                        board.getPieces()[y][x] = piece;
                        Map<Piece, Integer> pieceCounts = board.getPieceCounts();
                        if(!pieceCounts.containsKey(piece))
                            pieceCounts.put(piece, 1);
                        else
                            pieceCounts.put(piece, pieceCounts.get(piece) + 1);
                    }
                }
                if(SpliceDownLeftPiece.INSTANCE.isColorPiece(color)) {
                    if(!(new Color(screenCapture.getRGB(xPos - 5, yPos)))
                            .equals(new Color(170, 146 , 116))) {
                        board.getPieces()[y][x] = SpliceDownRightPiece.INSTANCE;
                        System.out.println("Splice down right found at y=" + y + " x=" + x);
                    } else if(!(new Color(screenCapture.getRGB(xPos + 5, yPos)))
                            .equals(new Color(207, 183 , 153))) {
                        board.getPieces()[y][x] = SpliceDownLeftPiece.INSTANCE;
                        System.out.println("Splice down left found at y=" + y + " x=" + x);
                    } else {
                        board.getPieces()[y][x] = SpliceHorizontalPiece.INSTANCE;
                        System.out.println("Splice horizontal found at y=" + y + " x=" + x);
                    }
                }
            }
        }
        return board;
    }

    private static int findActiveRig(BufferedImage screenCapture) {
        Color highlighted = new Color(253, 244 , 149);
        Color whiteHighlighted = new Color(254, 254 , 254);
        Color topColor = new Color(screenCapture.getRGB(231, 39));
        Color topRightColor = new Color(screenCapture.getRGB(380, 142));
        Color bottomRightColor = new Color(screenCapture.getRGB(367, 343));
        Color bottomColor = new Color(screenCapture.getRGB(196, 404));
        Color bottomLeftColor = new Color(screenCapture.getRGB(29, 307));
        Color topLeftColor = new Color(screenCapture.getRGB(55, 96));
        if(withinX(highlighted, topColor, 10) || withinX(whiteHighlighted, topColor, 10))
            return 0;
        else if(withinX(highlighted, topRightColor, 10) || withinX(whiteHighlighted, topRightColor, 10))
            return 1;
        else if(withinX(highlighted, bottomRightColor, 10) || withinX(whiteHighlighted, bottomRightColor, 10))
            return 2;
        else if(withinX(highlighted, bottomColor, 10) || withinX(whiteHighlighted, bottomColor, 10))
            return 3;
        else if(withinX(highlighted, bottomLeftColor, 10) || withinX(whiteHighlighted, bottomLeftColor, 10))
            return 4;
        else if(withinX(highlighted, topLeftColor, 10) || withinX(whiteHighlighted, topLeftColor, 10))
            return 5;
        else
            return -1;
    }
    
    private static boolean withinX(Color color1, Color color2, int maxDiff) {
        return color1.getBlue() <= color2.getBlue() + maxDiff && color1.getBlue() >= color2.getBlue() - maxDiff &&
                color1.getRed() <= color2.getRed() + maxDiff && color1.getRed() >= color2.getRed() - maxDiff &&
                color1.getGreen() <= color2.getGreen() + maxDiff && color1.getGreen() >= color2.getGreen() - maxDiff;
    }
}
