package com.bilgebot;

import com.bilgebot.piece.*;
import com.bilgebot.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 7/13/2015.
 */
public class ScoreSearch
{

    public static Solution searchAndRemove(Piece[][] board, int swapX, int swapY)
    {
        List<IntTuple> removes = new ArrayList<>();

        int yMin = 0;
        int yMax = board.length - 1;
        if(swapY != -1) {
            yMin = Math.max(yMin, swapY - 2);
            yMax = Math.min(yMax, swapY + 2);
        }

        int xMin = 0;
        int xMax = board[0].length - 1;
        if(swapX != -1) {
            xMin = Math.max(xMin, swapX - 2);
            xMax = Math.min(xMax, swapX + 3);
        }

        List<Integer> combos = new ArrayList<>();

        int clearedValue = 0;
        // for each horizontal
        for (int y = yMin; y < yMax + 1; y++) {
            Piece previousPieceType = null;
            int previousPieceCount = 0;
            for (int x = xMin; x < xMax + 1; x++) {
            Piece currentPiece = board[y][x];
                // if the previous piece did not match, reset the counts
                if(previousPieceType == null || currentPiece == NullPiece.INSTANCE ||
                        currentPiece != previousPieceType) {
                    if(previousPieceCount >= 3) {
                        combos.add(previousPieceCount);
                        for (int i = 1; i <= previousPieceCount; i++) {
                            removes.add(new IntTuple(y, x - i));
                        }
                        clearedValue += previousPieceCount==3 ? 3 : previousPieceCount==4 ? 5 : 7;
                    }

                    previousPieceType = currentPiece;
                    previousPieceCount = 1;

                    // short circuit if we cannot make a combo anymore
                    if (board[0].length - x < 3)
                        break;
                }
                // else add to the count
                else {
                    previousPieceCount +=1;
                }
            }
            // add the combo at the end
            if(previousPieceCount >= 3) {
                combos.add(previousPieceCount);
                for (int i = 1; i <= previousPieceCount; i++) {
                    removes.add(new IntTuple(y, board[0].length - i));
                }
                clearedValue += previousPieceCount==3 ? 3 : previousPieceCount==4 ? 5 : 7;
            }
        }

        // for each vertical
        for (int x = xMin; x < xMax + 1; x++) {
            Piece previousPieceType = null;
            int previousPieceCount = 0;
            for (int y = yMin; y < yMax + 1; y++) {
                Piece currentPiece = board[y][x];
                // if the previous piece did not match, reset the counts
                if(previousPieceType == null || currentPiece == NullPiece.INSTANCE ||
                        currentPiece != previousPieceType) {
                    if(previousPieceCount >= 3) {
                        combos.add(previousPieceCount);
                        for (int i = 1; i <= previousPieceCount; i++) {
                            removes.add(new IntTuple(y - i, x));
                        }
                        clearedValue += previousPieceCount==3 ? 3 : previousPieceCount==4 ? 5 : 7;
                    }

                    previousPieceType = currentPiece;
                    previousPieceCount = 1;

                    // short circuit if we cannot make a combo anymore
                    if (board.length - y < 3)
                        break;
                }
                // else add to the count
                else {
                    previousPieceCount +=1;
                }
            }
            // add the combo at the end
            if(previousPieceCount >= 3) {
                combos.add(previousPieceCount);
                for (int i = 1; i <= previousPieceCount; i++) {
                    removes.add(new IntTuple(board.length - i, x));
                }
                clearedValue += previousPieceCount==3 ? 3 : previousPieceCount==4 ? 5 : 7;
            }
        }

        removes.forEach(tuple -> {
            board[tuple.y][tuple.x] = FuturePiece.INSTANCE;
        });

        // https://yppedia.puzzlepirates.com/Bilge_scoring
        return new Solution(combos.size() == 0 ? 0 : (int)Math.pow((double)2, combos.size() - 1) * clearedValue, combos);
    }

    private static class IntTuple {
        private int y;
        private int x;

        public IntTuple(int y, int x) {
            this.y = y;
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }
    }
}
