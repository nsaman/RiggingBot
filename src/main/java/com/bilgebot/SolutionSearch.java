package com.bilgebot;

import java.util.List;

import static com.bilgebot.Board.MOVES_PER_DIRECTION;

/**
 * Created by Jacob on 7/13/2015.
 */
public class SolutionSearch
{
    private Board board;
    private int startIndex;
    private int endIndex;

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

            int matchingPieceScore = copyBoard.doRig();

            int loopedPieceScore = copyBoard.doClear();

            Swap thisSwap = new Swap(i, matchingPieceScore);

            List<Swap> swaps = findBestChildSwap(copyBoard, depth - 1);
            swaps.add(0, thisSwap);

            if(bestSwap == null || sumSwapScores(swaps) > sumSwapScores(bestSwap))
                bestSwap = swaps;
        }

        return bestSwap;
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

    private List<Swap> findBestChildSwap(Board sourceBoard, int depth){

        SolutionSearch childSearcher = new SolutionSearch(sourceBoard, 0, 72);

        return childSearcher.searchDepth(depth);
    }

    private static int sumSwapScores(List<Swap> swaps)
    {
        int sum = 0;
        for (Swap swap : swaps) {
            sum += swap.getPoints();
        }
        return sum;
    }
}
