package com.knox.bilgebot;

import com.knox.bilgebot.piece.FuturePiece;
import com.knox.bilgebot.piece.NullPiece;
import com.knox.bilgebot.piece.Piece;
import com.knox.bilgebot.piece.StandardPiece;
import com.knox.bilgebot.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 7/13/2015.
 */
public class SolutionSearch
{
    private Piece[][] board;
    private Piece[][] cleanBoard;
    private int depth;
    private int startIndex;
    private int endIndex; //exclusive

    public SolutionSearch(final Piece[][] board, int depth, int startIndex, int endIndex)
    {
        this.board = new Piece[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) //Copy array
        {
            for (int j = 0; j < board[0].length; j++)
            {
                this.board[i][j] = board[i][j];
            }
        }
        cleanBoard = new Piece[board.length][board[0].length];
        this.depth = depth;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<Swap> searchDepth(int depth)
    {
        List<Swap> bestSwap = null;
        for(int k = 0; k < board.length; k++)
        {
            System.arraycopy(board[k], 0, cleanBoard[k], 0, board[0].length);
        }

        for (int i = startIndex; i < endIndex; i++)
        {
            int y = i / board[0].length;
            int x = i % board[0].length;

            if (x == 5)
            {
                continue;
            }

            if (board[y][x] != null && board[y][x] instanceof StandardPiece && board[y][x + 1] != null && board[y][x + 1] instanceof StandardPiece)
            {
                if(board[y][x].equals(board[y][x + 1]))
                {
                    continue;
                }
                swapAdjacent(x, y);
                for(int k = 0; k < board.length; k++)
                {
                    System.arraycopy(board[k], 0, cleanBoard[k], 0, board[0].length);
                }

                int totalScore = 0;
                Solution solution;
                // quick check if there are any series
                if (
                    // left horizontal
                    (x > 1 && board[y][x] == board[y][x - 1] && board[y][x] == board[y][x - 2]) ||
                    // right horizontal
                    (x < (board[0].length - 3) && board[y][x + 1] == board[y][x + 2] && board[y][x + 1] == board[y][x + 3]) ||
                    // two above vertical left
                    (y > 1 && board[y][x] == board[y - 1][x] && board[y][x] == board[y - 2][x]) ||
                    // one above, one below vertical left
                    (y > 0 && y < (board.length - 1) && board[y][x] == board[y - 1][x] && board[y][x] == board[y + 1][x]) ||
                    // two below vertical left
                    (y < (board.length - 2) && board[y][x] == board[y + 1][x] && board[y][x] == board[y + 2][x]) ||
                    // two above vertical right
                    (y > 1 && board[y][x + 1] == board[y - 1][x + 1] && board[y][x + 1] == board[y - 2][x + 1]) ||
                    // one above, one below vertical right
                    (y > 0 && y < (board.length - 1) && board[y][x + 1] == board[y - 1][x + 1] && board[y][x + 1] == board[y + 1][x + 1]) ||
                    // two below vertical right
                    (y < (board.length - 2) && board[y][x + 1] == board[y + 1][x + 1] && board[y][x + 1] == board[y + 2][x + 1])
                ) {

                    ScoreSearch scoreSearch = new ScoreSearch(board);
                    solution = scoreSearch.search(x,y);
                    solution.setScore(solution.getScore() + handleBoardClearing(board));
                } else {
                    solution = new Solution(0, new ArrayList<>());
                }

                // if current best swap is null, initialize with this
                if (bestSwap == null)
                {
                    bestSwap = new ArrayList<>();
                    bestSwap.add(new Swap(x, y, solution));
                }

                List<Swap> currentSwaps = new ArrayList<>();
                // if not at leaves, find the best swaps of children
                if (depth > 1)
                {
                    SolutionSearch solDepthSearch = new SolutionSearch(cleanBoard, depth - 1, 0, 72);
                    currentSwaps = solDepthSearch.searchDepth(depth - 1);
                }

                currentSwaps.add(0, new Swap(x,y,solution));
                // if better than current swap
                if (sumSwapScores(currentSwaps) > sumSwapScores(bestSwap))
                {
                    bestSwap = currentSwaps;
                }

                swapAdjacent(x, y);
            }
        }

        return bestSwap;
    }

    public List<Swap> searchDepthThreads(int numThreads, int depth)
    {
        int segmentSize = (board.length * board[0].length) / numThreads;

        SolutionSearchThread[] threads = new SolutionSearchThread[numThreads];

        for (int i = 0; i < numThreads; i++)
        {
            SolutionSearch solutionSearch;
            if(i == numThreads - 1)
            {
                solutionSearch = new SolutionSearch(board, 0, i * segmentSize, board.length * board[0].length);
            }
            else
            {
                solutionSearch = new SolutionSearch(board, 0, i * segmentSize, (i + 1) * segmentSize);
            }
            threads[i] = new SolutionSearchThread(solutionSearch, depth);
            threads[i].start();
        }

        List<Swap> bestSwaps = null;
        for (int i = 0; i < numThreads; i++)
        {
            try
            {
                threads[i].join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            List<Swap> solSwaps = threads[i].getSwaps();
            if (bestSwaps == null || sumSwapScores(solSwaps) > sumSwapScores(bestSwaps))
            {
                bestSwaps = solSwaps;
            }
        }

        return bestSwaps;
    }

    private int handleBoardClearing(Piece[][] workingBoard){
        ScoreSearch scoreSearch = new ScoreSearch(workingBoard);
        Solution tempSolution;
        int totalScore = 0;
        while ((tempSolution = scoreSearch.search(-1, -1)).getScore() > 0) //Keep summing score until board is clean
        {
            totalScore += Math.min(tempSolution.getScore(), 7);
            cleanBoard = ScoreSearch.searchAndRemove(cleanBoard);
            cleanBoard = SolutionSearch.tickBoard(cleanBoard);
            scoreSearch = new ScoreSearch(cleanBoard);
        }
        totalScore /= 3;
        return totalScore;
    }

    private static int sumSwapScores(List<Swap> swaps)
    {
        int sum = 0;
        for(int i = 0; i < swaps.size(); i++)
        {
            sum += swaps.get(i).getScore() * (1 - (.10 * i));
        }
        return sum;
    }

    private void swapAdjacent(int xPos, int yPos)
    {
        StandardPiece tempPiece = (StandardPiece) board[yPos][xPos]; //Swap
        board[yPos][xPos] = board[yPos][xPos + 1];
        board[yPos][xPos + 1] = tempPiece;
    }

    public static Piece[][] tickBoard(Piece[][] board)
    {
        int vOffset = 0;
        for (int x = 0; x < board[0].length; x++)
        {
            for (int y = 0; y < board.length; y++)
            {
                if (board[y][x] instanceof FuturePiece)
                {
                    vOffset++;
                }
                else
                {
                    board[y - vOffset][x] = board[y][x];
                }
                if(y >= board.length - vOffset) //TODO: verify correct?
                {
                    board[y][x] = NullPiece.INSTANCE;
                }
            }
            vOffset = 0;
        }


        return board;
    }

    public static void printBoard(Piece[][] board)
    {
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[0].length; j++)
            {
                if (board[i][j] != null)
                {
                    System.out.printf("%d ", StandardPiece.pieces.indexOf(board[i][j]));
                } else
                {
                    System.out.printf("N ");
                }
            }
            System.out.println();
        }
        System.out.println("========");
    }
}
