package com.bilgebot;

import com.bilgebot.piece.*;
import com.bilgebot.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bilgebot.Board.MOVES_PER_DIRECTION;

/**
 * Created by Jacob on 7/13/2015.
 */
public class SolutionSearch
{
    private Board board;
    private Piece[][] cleanBoard;
    private int startIndex;
    private int endIndex;
    SolutionSearch childSearcher;

    public SolutionSearch(final Board board, int startIndex, int endIndex)
    {
        this.board = board;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void resetWith(final Board board, int startIndex, int endIndex) {
        this.board = board;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<Swap> searchDepth(int depth)
    {
        List<Swap> bestSwap = null;

        for (int i = startIndex; i < endIndex; i++)
        {
            Direction direction;
            if(i / MOVES_PER_DIRECTION == 0)
                direction = Direction.Horizontal;
            else if(i / MOVES_PER_DIRECTION == 1)
                direction = Direction.DownRight;
            else
                direction = Direction.DownLeft;

            int moveIndex = i % MOVES_PER_DIRECTION;

            Board copyBoard = board.clone();

            copyBoard.makeMove(moveIndex, direction);



            // two normal pieces
//            if (board[y][x] instanceof StandardPiece && board[y][x + 1] instanceof StandardPiece)
//            {
//                swapAdjacent(x, y);
//
//                int totalScore = 0;
//                Solution solution;
//                // quick check if there are any series
//                if (
//                    // left horizontal
//                    (x > 1 && board[y][x] == board[y][x - 1] && board[y][x] == board[y][x - 2]) ||
//                    // right horizontal
//                    (x < (board[0].length - 3) && board[y][x + 1] == board[y][x + 2] && board[y][x + 1] == board[y][x + 3]) ||
//                    // two above vertical left
//                    (y > 1 && board[y][x] == board[y - 1][x] && board[y][x] == board[y - 2][x]) ||
//                    // one above, one below vertical left
//                    (y > 0 && y < (board.length - 1) && board[y][x] == board[y - 1][x] && board[y][x] == board[y + 1][x]) ||
//                    // two below vertical left
//                    (y < (board.length - 2) && board[y][x] == board[y + 1][x] && board[y][x] == board[y + 2][x]) ||
//                    // two above vertical right
//                    (y > 1 && board[y][x + 1] == board[y - 1][x + 1] && board[y][x + 1] == board[y - 2][x + 1]) ||
//                    // one above, one below vertical right
//                    (y > 0 && y < (board.length - 1) && board[y][x + 1] == board[y - 1][x + 1] && board[y][x + 1] == board[y + 1][x + 1]) ||
//                    // two below vertical right
//                    (y < (board.length - 2) && board[y][x + 1] == board[y + 1][x + 1] && board[y][x + 1] == board[y + 2][x + 1])
//                ) {
//                    copyToCleanBoard(board);
//
//                    solution = ScoreSearch.searchAndRemove(cleanBoard,x,y);
//                    if (solution.getScore() > 0) {
//                        int crabPoints = SolutionSearch.tickBoard(cleanBoard, waterLevel);
//                        solution.setScore(solution.getScore() + handleBoardClearing(cleanBoard) + crabPoints);
//                    }
//                    bestSwap = findBestChildSwap(cleanBoard, bestSwap, x, y, solution, depth, true);
//                } else {
//                    solution = new Solution(0, new ArrayList<>());
//                    bestSwap = findBestChildSwap(board, bestSwap, x, y, solution, depth, true);
//                }
//
//                swapAdjacent(x, y);
//            }
        }

//        return bestSwap;

            return null;
    }

    public List<Swap> searchDepthThreads(int numThreads, int depth)
    {
        int segmentSize = Board.TOTAL_MOVES / numThreads;

        SolutionSearchThread[] threads = new SolutionSearchThread[numThreads];

        for (int i = 0; i < numThreads; i++)
        {
            SolutionSearch solutionSearch;
            if(i < numThreads - 1)
            {
                solutionSearch = new SolutionSearch(board, i * segmentSize, (i + 1) * segmentSize);
            }
            else
            {
                solutionSearch = new SolutionSearch(board, i * segmentSize, Board.TOTAL_MOVES);
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
        Piece[][] currentBoard = workingBoard;
        Solution tempSolution;
        int totalScore = 0;
        //Keep summing score until board is clean
        do
        {
            tempSolution = ScoreSearch.searchAndRemove(currentBoard,-1,-1);
            if(tempSolution.getScore() > 0) {
                totalScore += Math.min(tempSolution.getScore(), 7) / 3;
                totalScore += SolutionSearch.tickBoard(cleanBoard);
                currentBoard = cleanBoard;
            }
        } while (tempSolution.getScore() > 0);
        return totalScore;
    }

    private List<Swap> findBestChildSwap(Piece[][] sourceBoard, List<Swap> bestSwap, int x, int y, Solution solution, int depth, boolean wasSwap){

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
            if(childSearcher == null)
                childSearcher = new SolutionSearch(board, 0, 72);
            else
                childSearcher.resetWith(board, 0, 72);
            currentSwaps = childSearcher.searchDepth(depth - 1);
        }

        currentSwaps.add(0, new Swap(x,y,solution));
        // if better than current swap
        if (sumSwapScores(currentSwaps) > sumSwapScores(bestSwap))
        {
            bestSwap = currentSwaps;
        }

        return bestSwap;
    }

    private static int sumSwapScores(List<Swap> swaps)
    {
        int sum = 0;
        for(int i = 0; i < swaps.size(); i++)
        {
            sum += swaps.get(i).getScore() * (1 - (.075 * i));
        }
        return sum;
    }

    private void swapAdjacent(int xPos, int yPos)
    {
        StandardPiece tempPiece = (StandardPiece) board.getPieces()[yPos][xPos]; //Swap
        board.getPieces()[yPos][xPos] = board.getPieces()[yPos][xPos + 1];
        board.getPieces()[yPos][xPos + 1] = tempPiece;
    }

    public static int tickBoard(Piece[][] board)
    {
        int vOffset = 0;
        int totalScore = 0;
        for (int x = 0; x < board[0].length; x++)
        {
            for (int y = 0; y < board.length; y++)
            {
                if (board[y][x] == FuturePiece.INSTANCE)
                {
                    vOffset++;
                }
                else
                {
                    board[y - vOffset][x] = board[y][x];
                }
                if(y >= board.length - vOffset)
                {
                    board[y][x] = NullPiece.INSTANCE;
                }
            }
            vOffset = 0;
        }

        return totalScore;
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
