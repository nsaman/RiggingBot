package com.knox.bilgebot;

import com.knox.bilgebot.piece.*;
import com.knox.bilgebot.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jacob on 7/13/2015.
 */
public class SolutionSearch
{
    private Piece[][] board;
    private Piece[][] cleanBoard;
    private int startIndex;
    private int endIndex;
    private int waterLevel;
    SolutionSearch childSearcher;

    public SolutionSearch(final Piece[][] board, int startIndex, int endIndex, int waterLevel)
    {
        this.board = Arrays.stream(board).map(Piece[]::clone).toArray(Piece[][]::new);
        cleanBoard = new Piece[board.length][board[0].length];
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.waterLevel = waterLevel;
    }

    public void resetWith(final Piece[][] board, int depth, int startIndex, int endIndex, int waterLevel) {
        this.board = board;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.waterLevel = waterLevel;
    }

    public List<Swap> searchDepth(int depth)
    {
        List<Swap> bestSwap = null;
        copyToCleanBoard(board);

        for (int i = startIndex; i < endIndex; i++)
        {
            int y = i / board[0].length;
            int x = i % board[0].length;

            if (x == 5 ||
                    board[y][x] == null || board[y][x + 1] == null ||
                    board[y][x] == CrabPiece.INSTANCE || board[y][x + 1] == CrabPiece.INSTANCE ||
                    board[y][x] == NullPiece.INSTANCE || board[y][x + 1] == NullPiece.INSTANCE ||
                    board[y][x] == board[y][x + 1]
            ){
                continue;
            }

            // two normal pieces
            if (board[y][x] instanceof StandardPiece && board[y][x + 1] instanceof StandardPiece)
            {
                swapAdjacent(x, y);

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
                    copyToCleanBoard(board);

                    solution = ScoreSearch.searchAndRemove(cleanBoard,x,y);
                    if (solution.getScore() > 0) {
                        int crabPoints = SolutionSearch.tickBoard(cleanBoard, waterLevel);
                        solution.setScore(solution.getScore() + handleBoardClearing(cleanBoard) + crabPoints);
                    }
                    bestSwap = findBestChildSwap(cleanBoard, bestSwap, x, y, solution, depth);
                } else {
                    solution = new Solution(0, new ArrayList<>());
                    bestSwap = findBestChildSwap(board, bestSwap, x, y, solution, depth);
                }


                swapAdjacent(x, y);
            }
            // pufferfish
            else if (board[y][x] == BlowfishPiece.INSTANCE || board[y][x + 1] == BlowfishPiece.INSTANCE) {
                copyToCleanBoard(board);

                boolean isBlowFishLeft = cleanBoard[y][x] == BlowfishPiece.INSTANCE;
                boolean isBlowFishRight = cleanBoard[y][x + 1] == BlowfishPiece.INSTANCE;

                int initialScore = 0;
                if(isBlowFishLeft) {
                    for(int currentY = y - 1; currentY <= y + 1; currentY++)
                        for(int currentX = x - 1; currentX <= x + 1; currentX++)
                            if(currentY > 0 && currentY < cleanBoard.length - 1 &&
                                    currentX > 0 && currentX < cleanBoard[0].length - 1 &&
                                    cleanBoard[currentY][currentX] != null &&
                                    cleanBoard[currentY][currentX] != FuturePiece.INSTANCE
                            ) {
                                cleanBoard[currentY][currentX] = FuturePiece.INSTANCE;
                                initialScore += 1;
                            }
                }
                if(isBlowFishRight) {
                    for(int currentY = y - 1; currentY <= y + 1; currentY++)
                        for(int currentX = x; currentX <= x + 2; currentX++)
                            if(currentY > 0 && currentY < cleanBoard.length - 1 &&
                                    currentX > 0 && currentX < cleanBoard[0].length - 1 &&
                                    cleanBoard[currentY][currentX] != null &&
                                    cleanBoard[currentY][currentX] != FuturePiece.INSTANCE
                            ) {
                                cleanBoard[currentY][currentX] = FuturePiece.INSTANCE;
                                initialScore += 1;
                            }
                }
                Solution solution = new Solution(initialScore / 2, new ArrayList<>());

                int crabPoints = tickBoard(cleanBoard, waterLevel);
                solution.setScore(solution.getScore() + handleBoardClearing(cleanBoard) + crabPoints);

                bestSwap = findBestChildSwap(cleanBoard, bestSwap, x, y, solution, depth);
            }
            // Jellyfish
            else if (board[y][x] == JellyfishPiece.INSTANCE || board[y][x + 1] == JellyfishPiece.INSTANCE) {
                copyToCleanBoard(board);

                boolean isLeft = cleanBoard[y][x] == JellyfishPiece.INSTANCE;
                Piece clearedPiece = isLeft ? cleanBoard[y][x + 1] : cleanBoard[y][x];

                int initialScore = 0;
                for(int currentY = 0; currentY < board.length; currentY++)
                    for(int currentX = 0; currentX < board[0].length; currentX++)
                        if(cleanBoard[currentY][currentX] == clearedPiece) {
                            initialScore+=1;
                            cleanBoard[currentY][currentX] = FuturePiece.INSTANCE;
                        }

                Solution solution = new Solution(initialScore / 2, new ArrayList<>());

                int crabPoints = tickBoard(cleanBoard, waterLevel);
                solution.setScore(solution.getScore() + handleBoardClearing(cleanBoard) + crabPoints);

                bestSwap = findBestChildSwap(cleanBoard, bestSwap, x, y, solution, depth);
            }
        }

        return bestSwap;
    }

    public List<Swap> searchDepthThreads(int numThreads, int depth, int waterLevel)
    {
        int segmentSize = (board.length * board[0].length) / numThreads;

        SolutionSearchThread[] threads = new SolutionSearchThread[numThreads];

        for (int i = 0; i < numThreads; i++)
        {
            SolutionSearch solutionSearch;
            if(i == numThreads - 1)
            {
                solutionSearch = new SolutionSearch(board, i * segmentSize, board.length * board[0].length, waterLevel);
            }
            else
            {
                solutionSearch = new SolutionSearch(board, i * segmentSize, (i + 1) * segmentSize, waterLevel);
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

    private void copyToCleanBoard(Piece[][] board) {
        // though this is creatig a new array[][] every time it has tested to be faster. Java limitation
        cleanBoard = Arrays.stream(board).map(Piece[]::clone).toArray(Piece[][]::new);
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
                totalScore += SolutionSearch.tickBoard(cleanBoard, waterLevel);
                currentBoard = cleanBoard;
            }
        } while (tempSolution.getScore() > 0);
        return totalScore;
    }

    private List<Swap> findBestChildSwap(Piece[][] sourceBoard, List<Swap> bestSwap, int x, int y, Solution solution, int depth){

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
                childSearcher = new SolutionSearch(sourceBoard, 0, 72, waterLevel);
            else
                childSearcher.resetWith(sourceBoard, depth - 1, 0, 72, waterLevel);
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
        StandardPiece tempPiece = (StandardPiece) board[yPos][xPos]; //Swap
        board[yPos][xPos] = board[yPos][xPos + 1];
        board[yPos][xPos + 1] = tempPiece;
    }

    public static int tickBoard(Piece[][] board, int waterLevel)
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
                else if (board[y][x] == CrabPiece.INSTANCE && y < waterLevel)
                {
                    totalScore += 5;
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
