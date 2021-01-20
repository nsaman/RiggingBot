package com.knox.bilgebot;

import com.knox.bilgebot.piece.*;
import com.knox.bilgebot.solution.Solution;

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

        for (int y = yMin; y < yMax; y++) {
            for (int x = xMin; x < xMax; x++) {
                Piece piece = board[y][x];
                if(piece instanceof StandardPiece) {
                    // if horizontal
                    if(x == 0 || piece != board[y][x - 1]) {
                        if(x + 2 <= xMax &&
                                piece == board[y][x + 1] &&
                                piece == board[y][x + 2]
                        ) {
                            removes.add(new IntTuple(y,x));
                            removes.add(new IntTuple(y,x + 1));
                            removes.add(new IntTuple(y,x + 2));

                            int xIter = 3;
                            while(x + xIter < xMax && piece == board[y][x + xIter]) {
                                removes.add(new IntTuple(y,x + xIter));
                                xIter++;
                            }
                            clearedValue += 3 + (xIter - 3) * 2;
                            combos.add(xIter);
                        }
                    }
                    // if vertical
                    if(y == 0 || piece != board[y - 1][x]) {
                        if (y + 2 <= yMax &&
                                piece == board[y + 1][x] &&
                                piece == board[y + 2][x]
                        ) {
                            removes.add(new IntTuple(y, x));
                            removes.add(new IntTuple(y + 1, x));
                            removes.add(new IntTuple(y + 2, x));

                            int yIter = 3;
                            while (y + yIter < xMax && piece == board[y + yIter][x]) {
                                removes.add(new IntTuple(y + yIter, x));
                                yIter++;
                            }
                            clearedValue += 3 + (yIter - 3) * 2;
                            combos.add(yIter);
                        }
                    }
                }
            }
        }

        removes.forEach(tuple -> {
            board[tuple.y][tuple.x] = FuturePiece.INSTANCE;
        });

        // https://yppedia.puzzlepirates.com/Bilge_scoring
        return new Solution(combos.size() == 0 ? 0 : combos.size() * clearedValue, combos);
    }

    public static boolean nearbyRun(Piece[][] board, int x, int y) {

        return  // left horizontal
                (x > 1 && board[y][x] == board[y][x - 1] && board[y][x] == board[y][x - 2]) ||
                // mid horizontal
                (x > 0 && x < (board[0].length - 1) && board[y][x] == board[y][x - 1] && board[y][x] == board[y][x + 1]) ||
                // right horizontal
                (x < (board[0].length - 2) && board[y][x] == board[y][x + 1] && board[y][x] == board[y][x + 2]) ||
                // two above vertical left
                (y > 1 && board[y][x] == board[y - 1][x] && board[y][x] == board[y - 2][x]) ||
                // one above, one below vertical left
                (y > 0 && y < (board.length - 1) && board[y][x] == board[y - 1][x] && board[y][x] == board[y + 1][x]) ||
                // two below vertical left
                (y < (board.length - 2) && board[y][x] == board[y + 1][x] && board[y][x] == board[y + 2][x]);
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
