package com.knox.bilgebot;

import com.knox.bilgebot.piece.FuturePiece;
import com.knox.bilgebot.piece.NullPiece;
import com.knox.bilgebot.piece.Piece;
import com.knox.bilgebot.piece.StandardPiece;
import com.knox.bilgebot.solution.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 7/13/2015.
 */
public class ScoreSearch
{
    private Piece[][] board;

    public ScoreSearch(Piece[][] board)
    {
        this.board = board;
    }

    public Solution search(int swapX, int swapY)
    {
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

        // for each horizontal
        for (int y = yMin; y < yMax + 1; y++) {
            Piece previosPieceType = null;
            int previousPieceCount = 0;
            for (int x = xMin; x < xMax + 1; x++) {
                // new pieces drawn seen as null
                if(board[y][x] == null) {
                    if(previousPieceCount >= 3)
                        combos.add(previousPieceCount);
                    previosPieceType = null;
                    previousPieceCount = 0;
                } else {
                    Piece currentPiece = board[y][x];
                    // if the previous piece did not match, reset the counts
                    if(currentPiece == null || currentPiece == NullPiece.INSTANCE ||
                            previosPieceType == null || previosPieceType == NullPiece.INSTANCE ||
                            currentPiece.getClass() != previosPieceType.getClass()) {
                        if(previousPieceCount >= 3)
                            combos.add(previousPieceCount);

                        previosPieceType = currentPiece;
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
            }
            // add the combo at the end
            if(previousPieceCount >= 3)
                combos.add(previousPieceCount);
        }

        // for each vertical
        for (int x = xMin; x < xMax + 1; x++) {
            Piece previousPieceType = null;
            int previousPieceCount = 0;
            for (int y = yMin; y < yMax + 1; y++) {
                // new pieces drawn seen as null
                if(board[y][x] == null) {
                    if(previousPieceCount >= 3)
                        combos.add(previousPieceCount);
                    previousPieceType = null;
                    previousPieceCount = 0;
                } else {
                    Piece currentPiece = board[y][x];
                    // if the previous piece did not match, reset the counts
                    if(currentPiece == null || currentPiece == NullPiece.INSTANCE ||
                            previousPieceType == null || previousPieceType == NullPiece.INSTANCE ||
                            currentPiece.getClass() != previousPieceType.getClass()) {
                        if(previousPieceCount >= 3)
                            combos.add(previousPieceCount);

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
            }
            // add the combo at the end
            if(previousPieceCount >= 3)
                combos.add(previousPieceCount);
        }

        return new Solution(combos.size() == 0 ? 0 : combos.size() * combos.stream().reduce(0, (a, b) -> a + b), combos);
    }

    public static Piece[][] searchAndRemove(Piece[][] board)
    {

        Class<StandardPiece> prevPiece = null;
        int prevPieces = 0;

        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[0].length; x++)
            {
                if (board[y][x] == null)
                {
                    if (prevPieces >= 3)
                    {
                        for (int i = x - prevPieces; i < x; i++)
                        {
                            board[y][i] = FuturePiece.INSTANCE;
                        }
                    }
                    prevPiece = null;
                    prevPieces = 0;
                    continue;
                }
                if (!(board[y][x] instanceof StandardPiece))
                {
                    if (prevPieces >= 3)
                    {
                        for (int i = x - prevPieces; i < x; i++)
                        {
                            board[y][i] = FuturePiece.INSTANCE;
                        }
                    }
                    prevPieces = 0;
                    prevPiece = null;
                } else //Standard Piece
                {
                    if (prevPiece == null || !prevPiece.equals(board[y][x].getClass())) //Different piece than previous
                    {
                        if (prevPieces >= 3)
                        {
                            for (int i = x - prevPieces; i < x; i++)
                            {
                                board[y][i] = FuturePiece.INSTANCE;
                            }
                        }
                        prevPiece = (Class<StandardPiece>) board[y][x].getClass();
                        prevPieces = 1;
                    } else
                    {
                        prevPieces++;
                    }
                }
            }
            if (prevPieces >= 3)
            {
                for (int i = board[0].length - prevPieces; i < board[0].length; i++) //TODO: verify
                {
                    board[y][i] = FuturePiece.INSTANCE;
                }
            }
            prevPiece = null;
            prevPieces = 0;
        }

        prevPiece = null;
        prevPieces = 0;

        for (int x = 0; x < board[0].length; x++)
        {
            for (int y = 0; y < board.length; y++)
            {
                if (board[y][x] == null)
                {
                    for (int i = y - prevPieces; i < y; i++)
                    {
                        board[i][x] = FuturePiece.INSTANCE;
                    }
                    prevPiece = null;
                    prevPieces = 0;
                    continue;
                }
                if (!(board[y][x] instanceof StandardPiece))
                {
                    if (prevPieces >= 3)
                    {
                        for (int i = y - prevPieces; i < y; i++)
                        {
                            board[i][x] = FuturePiece.INSTANCE;
                        }
                    }
                    prevPiece = (Class<StandardPiece>) board[y][x].getClass();
                    prevPieces = 0;
                } else //Standard Piece
                {
                    if (prevPiece == null || !prevPiece.equals(board[y][x].getClass()))
                    {
                        if (prevPieces >= 3)
                        {
                            for (int i = y - prevPieces; i < y; i++)
                            {
                                board[i][x] = FuturePiece.INSTANCE;
                            }
                        }
                        prevPiece = (Class<StandardPiece>) board[y][x].getClass();
                        prevPieces = 1;
                    } else
                    {
                        prevPieces++;
                    }
                }
            }
            if (prevPieces >= 3)
            {
                for (int i = board.length - prevPieces; i < board.length; i++)
                {
                    board[i][x] = FuturePiece.INSTANCE;
                }
            }
            prevPiece = null;
            prevPieces = 0;
        }

        return board;
    }
}
