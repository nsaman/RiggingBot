package com.riggingbot;

import java.util.ArrayList;
import java.util.List;

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

    public List<Swap> searchDepth(int depth, Move previousMove)
    {
        List<Swap> bestSwap = null;
        int bestSwapScore = -1;

        for (int i = startIndex; i < endIndex; i++)
        {
            Move move = Board.getMoveByIndex(i);

            if(previousMove != null && previousMove.getDirection() == move.getDirection()) {
                // don't move the same row twice
                if (previousMove.getRow() == move.getRow())
                    continue;
                // don't duplicate move pairs
                else if (previousMove.getRow() < move.getRow() && !previousMove.movesOnRig((board.getActiveRig() + 5) % 6)) {
                    continue;
                }
            }

            Board copyBoard = board.clone();

            copyBoard.makeMove(move);

            int score = copyBoard.doRig();

            List<Swap> swaps;

            if(score > 0) {
                // todo do not run doClear if loops are impossible
                // todo remove all logic that is not necessary for first score search

                Swap thisSwap = new Swap(i, score);
                swaps = new ArrayList<>();
                swaps.add(0, thisSwap);

            } else {
                Swap thisSwap = new Swap(i, score);
                if(depth > 1) {
                    copyBoard.setActiveRig((copyBoard.getActiveRig() + 1) % 6);
                    swaps = findBestChildSwap(copyBoard, depth - 1, move);
                } else {
                    swaps = new ArrayList<>();
                }
                swaps.add(0, thisSwap);
                score = sumSwapScores(swaps);
            }

            if(bestSwap == null || score > bestSwapScore) {
                bestSwapScore = score;
                bestSwap = swaps;
            }
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

    private List<Swap> findBestChildSwap(Board sourceBoard, int depth, Move previousMove){

        SolutionSearch childSearcher = new SolutionSearch(sourceBoard, 0, Board.TOTAL_MOVES);

        return childSearcher.searchDepth(depth, previousMove);
    }

    private static int sumSwapScores(List<Swap> swaps)
    {
        int sum = 0;
        for (int i = 0; i < swaps.size(); i++) {
            sum += swaps.get(i).getPoints() * Math.pow(.8,i);
        }
        return sum;
    }
}
