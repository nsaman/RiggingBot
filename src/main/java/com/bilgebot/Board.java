package com.bilgebot;

import com.bilgebot.piece.Piece;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

import static com.bilgebot.PieceSearch.*;

@Data
@AllArgsConstructor
public class Board {
    public static final int MOVES_PER_DIRECTION = 52;
    public static final int TOTAL_MOVES = MOVES_PER_DIRECTION * 3;

    private int activeRig;
    private Piece[][] pieces;

    public Board() {
        pieces = new Piece[9][];
        for(int y = 0; y < NUM_ROWS; y++) {
            int pieceCountInRow = CENTER_ROW_COUNT - Math.abs(ROW_OFFSET - y);
            pieces[y] = new Piece[pieceCountInRow];
        }
    }

    public Board clone() {
        Piece[][] pieces = Arrays.stream(this.pieces).map(Piece[]::clone).toArray(Piece[][]::new);
        return new Board(activeRig, pieces);
    }

    public void makeMove(int moveIndex, Direction direction) {
        assert(moveIndex >= 0 && moveIndex < Board.MOVES_PER_DIRECTION);

        int row;
        int rowIndex;
        if (moveIndex <= 3) {
            row = 0;
            rowIndex = moveIndex;
        }
        else if(moveIndex <= 8) {
            row = 1;
            rowIndex = moveIndex - 4;
        }
        else if(moveIndex <= 14) {
            row = 2;
            rowIndex = moveIndex - 9;
        }
        else if(moveIndex <= 21) {
            row = 3;
            rowIndex = moveIndex - 15;
        }
        else if(moveIndex <= 29) {
            row = 4;
            rowIndex = moveIndex - 22;
        }
        else if(moveIndex <= 36) {
            row = 5;
            rowIndex = moveIndex - 30;
        }
        else if(moveIndex <= 42) {
            row = 6;
            rowIndex = moveIndex - 37;
        }
        else if(moveIndex <= 47) {
            row = 7;
            rowIndex = moveIndex - 43;
        }
        else {
            row = 8;
            rowIndex = moveIndex - 48;
        }
        switch (direction) {
            case Horizontal -> shiftHorizontally(row, rowIndex);
            case DownRight -> shiftDownRight(row, rowIndex);
            case DownLeft -> shiftDownLeft(row, rowIndex);
        }
    }

    private void shiftHorizontally(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        // using a buffer but I really don't like it
        Piece[] copy = pieces[row].clone();
        for(int i = 0; i < copy.length; i++) {
            pieces[row][i + amount % copy.length] = copy[i];
        }
    }

    private void shiftDownRight(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        int startingRow = Math.max(0, row - 4);
        Piece[] copy = new Piece[pieces[row].length];
        // copy to buffer with move offset
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = ((i - amount) + pieces[row].length) % pieces[row].length + startingRow;
            copy[i] = pieces[sourceRow]
                    [sourceRow > 4 ? pieces.length - 1 - row : pieces[sourceRow].length - 1 - row];
        }
        // apply buffer to board
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = startingRow + i;
            pieces[sourceRow]
                  [sourceRow > 4 ? pieces.length - 1 - row : pieces[sourceRow].length - 1 - row]
                    = copy[i];
        }
    }

    private void shiftDownLeft(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        int startingRow = Math.max(0, row - 4);
        Piece[] copy = new Piece[pieces[row].length];
        // copy to buffer with move offset
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = ((i - amount) + pieces[row].length) % pieces[row].length + startingRow;
            copy[i] = pieces[sourceRow]
                    [sourceRow <= 4 ? row : row - (sourceRow - 4)];
        }
        // apply buffer to board
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = startingRow + i;
            pieces[sourceRow]
                    [sourceRow <= 4 ? row : row - (sourceRow - 4)]
                    = copy[i];
        }
    }
}
