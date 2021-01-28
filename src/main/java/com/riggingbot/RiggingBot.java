package com.riggingbot;

import com.riggingbot.gui.OverlayFrame;
import com.riggingbot.gui.StatusFrame;
import com.riggingbot.piece.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.riggingbot.PieceSearch.*;

/**
 * The base of the bot that decides what action needs to be done
 */
public class RiggingBot
{
    private static final boolean SKIP_IF_UNKNOWN_PIECE = true;
    private static final int PIECE_LENGTH = 45;

    private Status status;

    private TickThread tickThread;
    private OverlayFrame overlayFrame;
    private StatusFrame statusFrame;

    private ExternalWindowManager exWinMan;
    private Robot robot;

    private boolean operable = true;
    private Point puzzlePosition;

    private BufferedImage puzzleCorner;
    private BufferedImage selectionCorner;
    private BufferedImage selectionCornerWater;

    private List<Swap> swapQueue = new ArrayList<>();
    private long lastSwapTime = 0;

    private boolean isRunning;

    private MouseMoveThread mouseMoveThread = new MouseMoveThread();

    private boolean autoMode;
    private int depth;
    private int numThreads;

    /**
     * Loads necessary components and launches the StatusFrame to wait for user instruction
     */
    public RiggingBot()
    {
        this.statusFrame = new StatusFrame(this);
        statusFrame.setVisible(true);
        status = new Status(statusFrame);

        status.log("Rigging Bot initializing...");
        status.setStatus("Initializing");

        numThreads = Runtime.getRuntime().availableProcessors();
//        numThreads = 1;
        status.log("Found " + numThreads + " processors; will run " + numThreads + " threads");

        try
        {
            robot = new Robot();
            status.log("Robot created");
        } catch (AWTException e)
        {
            status.log("Couldn't create robot instance: " + e.getMessage(), Status.Severity.ERROR);
            e.printStackTrace();
            operable = false;
            return;
        }

        status.setStatus("Initializing: loading images");

        URL puzzleCornerUrl = this.getClass().getClassLoader().getResource("puzzle-corner.png");
        if(puzzleCornerUrl == null)
        {
            status.log("Failed to load puzzle-corner.png from JAR", Status.Severity.ERROR);
        }
        else
        {
            try
            {
                puzzleCorner = ImageIO.read(puzzleCornerUrl);
            } catch (IOException e)
            {
                status.log("Failed to load puzzle-corner.png: " + e.getMessage());
                operable = false;
                e.printStackTrace();
                return;
            }
        }

        URL selectionCornerUrl = this.getClass().getClassLoader().getResource("selection-corner.png");
        if(selectionCornerUrl == null)
        {
            status.log("Failed to load selection-corner.png from JAR", Status.Severity.ERROR);
        }
        else
        {
            try
            {
                selectionCorner = ImageIO.read(selectionCornerUrl);
            } catch (IOException e)
            {
                status.log("Failed to load selection-corner.png: " + e.getMessage());
                operable = false;
                e.printStackTrace();
                return;
            }
        }
        status.log("Done initializing");
        status.setStatus("Waiting to start");

    }

    /**
     * Stops the bot's threads, shutting down the bot
     */
    public void stop()
    {
        if(mouseMoveThread != null)
        {
            mouseMoveThread.shutdown();
        }
        if(tickThread != null)
        {
            tickThread.shutdown();
        }
        if(swapQueue != null) {
            swapQueue.clear();
        }
        if(overlayFrame != null)
        {
            overlayFrame.setVisible(false);
        }
        isRunning = false;
        status.setStatus("Stopped");
    }

    /**
     * Prepares the bot to run by waiting on the PP window and Rigging puzzle, then launches the threads
     */
    public void init(int depth, boolean auto, boolean overlay)
    {
        this.depth = depth;
        this.autoMode = auto;

        if(!operable)
        {
            return;
        }

        isRunning = true;

        exWinMan = new ExternalWindowManager();
        status.log("External window manager created");

        status.log("Waiting on PP window...");
        status.setStatus("Waiting for Puzzle Pirates window");

        while(!exWinMan.isWindowAvailable())
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
            }
            if(!isRunning)
            {
                return;
            }
        }

        status.log("Window found. Focusing window");
        exWinMan.restoreWindow();

        status.log("Waiting on rigging puzzle...");
        status.setStatus("Waiting for rigging puzzle");

        boolean foundPuzzle = false;
        Point puzzleCoords = null;
        while (!foundPuzzle)
        {
            BufferedImage screenCapture = robot.createScreenCapture(exWinMan.getWindowBounds());

            ImageSearch imageSearch = new ImageSearch(screenCapture, puzzleCorner);
            puzzleCoords = imageSearch.search();

            if(puzzleCoords != null)
            {
                foundPuzzle = true;
            }
            else
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                }
                if(!isRunning)
                {
                    return;
                }
            }
        }
        status.log("Rigging puzzle found");

        overlayFrame = new OverlayFrame(puzzleCoords.x, puzzleCoords.y, exWinMan);
        overlayFrame.setLocation(statusFrame.getX(), statusFrame.getY() + statusFrame.getHeight());
        overlayFrame.setVisible(overlay);

        int adjustedX = exWinMan.getWindowBounds().x + puzzleCoords.x;
        int adjustedY = exWinMan.getWindowBounds().y + puzzleCoords.y;
        overlayFrame.setImage(robot.createScreenCapture(new Rectangle(adjustedX, adjustedY, 429, 530)));

        puzzlePosition = puzzleCoords;

        status.log("Starting tick thread");
        status.setStatus("Running");

        tickThread = new TickThread(this);
        tickThread.start();

        if(auto)
        {
            mouseMoveThread = new MouseMoveThread();
            mouseMoveThread.start();
        }


    }

    /**
     * Searches for the pieces and the corresponding solution, then schedules the mouse move
     */
    public void tick()
    {
        if(!operable)
        {
            throw new RuntimeException("tick() was called, but the bot is inoperable");
        }

        int adjustedX = exWinMan.getWindowBounds().x + puzzlePosition.x;
        int adjustedY = exWinMan.getWindowBounds().y + puzzlePosition.y;

        BufferedImage puzzleCapture = robot.createScreenCapture(new Rectangle(adjustedX, adjustedY, 429, 530));
        ImageSearch imageSearch = new ImageSearch(puzzleCapture, selectionCorner);
        Point selectionPos = imageSearch.search();

        Board board = PieceSearch.searchPieces(puzzleCapture);

        overlayFrame.setBoard(board);
        overlayFrame.setSelectorPosition(selectionPos);
        overlayFrame.setImage(puzzleCapture);

        if(isAnyNull(board)) //Prevents the bot from making moves while the board isn't settled
        {
            status.setStatus("Waiting for board to clear");
            return;
        }

        //Automode swapping
        if(autoMode && System.currentTimeMillis() - lastSwapTime > 250 && ! mouseMoveThread.hasMove())
        {

            if (true) //swapQueue.isEmpty()) //Finds a move if one is needed
            {
                overlayFrame.setSolution(null);
                System.out.println("Searching for new swaps...");
                status.setStatus("Searching for new swaps");
                SolutionSearch solutionSearch = new SolutionSearch(board, 0, Board.TOTAL_MOVES);
                long searchTime = System.currentTimeMillis();
                swapQueue = solutionSearch.searchDepthThreads(numThreads, depth);
                status.log("Search time: " + (System.currentTimeMillis() - searchTime));
                System.out.println("Search time: " + (System.currentTimeMillis() - searchTime));
                String swapString = "rig: " + board.getActiveRig() + " ";
                for (Swap swap : swapQueue)
                {
                    swapString += "=> ";
                    swapString += swap;
                    swapString += " ";
                }
                status.log("Swap String: " + swapString);
                System.out.println(swapString);
            }

            if(!isAnyNull(board))
            {
                Swap swap = swapQueue.remove(0);
                System.out.println("Executing swap: " + swap);
                status.setStatus("Performing swap: " + swap.toString());
                Move move = new Move(swap.getMoveIndex());

                if(move.getDirection() == Direction.Horizontal) {

                    int rowIdent = (CENTER_ROW_COUNT - board.getPieces()[move.getRow()].length) / 2;
                    int isEvenPieceOffset = board.getPieces()[move.getRow()].length % 2 == 1 ? 0 : PIECE_WIDTH / 2;
                    int xPos = X_BOARD_OFFSET + rowIdent * PIECE_WIDTH + isEvenPieceOffset;
                    int yPos = Y_BOARD_OFFSET + PIECE_HEIGHT * move.getRow();

                    mouseMoveThread.setStartDestination((int) (adjustedX + xPos + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + Math.random() * PIECE_LENGTH / 4));

                    mouseMoveThread.setEndDestination((int) (adjustedX + xPos + PIECE_WIDTH * (move.getMoveIndex() + 1) + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + Math.random() * PIECE_LENGTH / 4));
                }

                else if(move.getDirection() == Direction.DownRight) {

                    int rowIdent = (CENTER_ROW_COUNT - board.getPieces()[4].length) / 2;
                    int isEvenPieceOffset = board.getPieces()[4].length % 2 == 1 ? 0 : PIECE_WIDTH / 2;
                    int xPos = X_BOARD_OFFSET + rowIdent * PIECE_WIDTH + isEvenPieceOffset + PIECE_WIDTH * (8 - move.getRow());
                    int yPos = Y_BOARD_OFFSET + PIECE_HEIGHT * 4;

                    mouseMoveThread.setStartDestination((int) (adjustedX + xPos + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + Math.random() * PIECE_LENGTH / 4));

                    mouseMoveThread.setEndDestination((int) (adjustedX + xPos + (PIECE_WIDTH * (move.getMoveIndex() + 1))/2 + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + (PIECE_HEIGHT * (move.getMoveIndex() + 1)) + Math.random() * PIECE_LENGTH / 4));
                }

                else if(move.getDirection() == Direction.DownLeft) {

                    int rowIdent = (CENTER_ROW_COUNT - board.getPieces()[4].length) / 2;
                    int isEvenPieceOffset = board.getPieces()[4].length % 2 == 1 ? 0 : PIECE_WIDTH / 2;
                    int xPos = X_BOARD_OFFSET + rowIdent * PIECE_WIDTH + isEvenPieceOffset + PIECE_WIDTH * move.getRow();
                    int yPos = Y_BOARD_OFFSET + PIECE_HEIGHT * 4;

                    mouseMoveThread.setStartDestination((int) (adjustedX + xPos + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + Math.random() * PIECE_LENGTH / 4));

                    mouseMoveThread.setEndDestination((int) (adjustedX + xPos - (PIECE_WIDTH * (move.getMoveIndex() + 1))/2 + Math.random() * PIECE_LENGTH / 4),
                            (int) (adjustedY + yPos + (PIECE_HEIGHT * (move.getMoveIndex() + 1)) + Math.random() * PIECE_LENGTH / 4));
                }

                lastSwapTime = System.currentTimeMillis();
            }
        }
    }


    /**
     * Tells if the board has an unknown piece
     * @param board the board
     * @return is any of the board null (unknown)
     */
    private static boolean isAnyNull(Board board)
    {
        if(SKIP_IF_UNKNOWN_PIECE)
        {
            if(board.getActiveRig() == -1)
                return true;

            for (Piece[] row : board.getPieces()) {
                for (int j = 0; j < row.length; j++) {
                    if (row[j] == null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets whether the bot is actively running or not
     * @return whether bot is running or not
     */
    public boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Status getter - for logging messages
     * @return the bot's Status
     */
    public Status getStatus()
    {
        return status;
    }
}
