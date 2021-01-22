package com.bilgebot;

import com.bilgebot.piece.Piece;
import lombok.Data;

import static com.bilgebot.PieceSearch.*;

@Data
public class Board {
    private int activeRig;
    private Piece[][] pieces;

    public Board() {
        pieces = new Piece[9][];
        for(int y = 0; y < NUM_ROWS; y++) {
            int pieceCountInRow = CENTER_ROW_COUNT - Math.abs(ROW_OFFSET - y);
            pieces[y] = new Piece[pieceCountInRow];
        }
    }
}
