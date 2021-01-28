package com.riggingbot;

import com.riggingbot.piece.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

public class BoardTest {

    Board board;

    @Before
    public void setup() throws Exception {
        URL selectionCornerUrl = this.getClass().getClassLoader().getResource("puzzle-corner.png");
        BufferedImage selectionCorner = ImageIO.read(selectionCornerUrl);

        URL boardUrl = this.getClass().getClassLoader().getResource("1Star.PNG");
        BufferedImage puzzleCapture = ImageIO.read(boardUrl);
        ImageSearch imageSearch = new ImageSearch(puzzleCapture, selectionCorner);
        Point puzzleCoords = imageSearch.search();

        puzzleCapture = puzzleCapture.getSubimage(puzzleCoords.x, puzzleCoords.y, puzzleCapture.getWidth() - puzzleCoords.x, puzzleCapture.getHeight() - puzzleCoords.y);

        board = PieceSearch.searchPieces(puzzleCapture);
    }

    @Test
    public void test_down_right_diagonal() {
        board.makeMove(53); // move 2
        board.makeMove(59); // move 4
        board.makeMove(62); // move 2
        board.makeMove(73); // move 7
        board.makeMove(81); // move 8
        board.makeMove(82); // move 1
        board.makeMove(94); // move 6
        board.makeMove(97); // move 3
        board.makeMove(103); // move 4

        // row 0
        // move 2
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][4]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[1][5]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[2][6]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[3][7]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[4][8]);

        // row 1
        // move 4
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][3]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[1][4]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][5]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[3][6]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[4][7]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[5][7]);

        // row 2
        // move 2
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[1][3]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[2][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[3][5]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[4][6]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][6]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[6][6]);

        // row 3
        // move 7
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][1]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[1][2]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][3]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][4]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[4][5]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][5]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[6][5]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[7][5]);

        // row 4
        // move 8
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][0]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[1][1]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[2][2]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[3][3]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[4][4]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][4]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[6][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[7][4]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[8][4]);

        // row 5
        // move 1
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[1][0]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[3][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[4][3]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[5][3]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[6][3]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[7][3]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[8][3]);

        // row 6
        // move 6
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[2][0]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[3][1]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[4][2]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[5][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[6][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[7][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[8][2]);

        // row 7
        // move 3
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][0]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[4][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[5][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[6][1]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[7][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[8][1]);

        // row 8
        // move 4
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[4][0]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][0]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[6][0]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[7][0]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[8][0]);
    }

    @Test
    public void test_down_left_diagonal() {
        board.makeMove(105); // move 2
        board.makeMove(111); // move 4
        board.makeMove(114); // move 2
        board.makeMove(125); // move 7
        board.makeMove(133); // move 8
        board.makeMove(134); // move 1
        board.makeMove(146); // move 6
        board.makeMove(149); // move 3
        board.makeMove(155); // move 4

        // row 0
        // move 2
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][0]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[1][0]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][0]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[3][0]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[4][0]);

        // row 1
        // move 4
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[1][1]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[2][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[4][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[5][0]);

        // row 2
        // move 2
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[1][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[2][2]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][2]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[4][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[6][0]);

        // row 3
        // move 7
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[0][3]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[1][3]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[2][3]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[3][3]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[4][3]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[5][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[6][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[7][0]);

        // row 4
        // move 8
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[1][4]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[3][4]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[4][4]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[5][3]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[6][2]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[7][1]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[8][0]);

        // row 5
        // move 1
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[1][5]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[2][5]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][5]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[4][5]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[5][4]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[6][3]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[7][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[8][1]);

        // row 6
        // move 6
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[2][6]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[3][6]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[4][6]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][5]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[6][4]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[7][3]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[8][2]);

        // row 7
        // move 3
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[3][7]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[4][7]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[5][6]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[6][5]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[7][4]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[8][3]);

        // row 8
        // move 4
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[4][8]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[5][7]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[6][6]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[7][5]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[8][4]);
    }

    @Test
    public void test_nearbyMatches_top_left(){
        setBoardToBrown(board);
        board.getPieces()[0][1] = RainbowPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(0,0, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_top_right(){
        setBoardToBrown(board);
        board.getPieces()[0][4] = RainbowPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(0,4, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_left(){
        setBoardToBrown(board);
        board.getPieces()[3][0] = RainbowPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(4,0, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_right(){
        setBoardToBrown(board);
        board.getPieces()[3][7] = RainbowPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(4,8, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_bottom_left(){
        setBoardToBrown(board);
        board.getPieces()[7][0] = RainbowPiece.INSTANCE;
        board.getPieces()[7][1] = RainbowPiece.INSTANCE;
        board.getPieces()[8][0] = RainbowPiece.INSTANCE;
        board.getPieces()[8][1] = RainbowPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(8,0, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_bottom_right(){
        setBoardToBrown(board);

        Set<Board.IntTuple> matches = board.nearbyMatches(8,4, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches_middle(){
        setBoardToBrown(board);
        board.getPieces()[4][3] = BlackPiece.INSTANCE;
        board.getPieces()[3][4] = BlackPiece.INSTANCE;
        board.getPieces()[5][4] = BlackPiece.INSTANCE;

        Set<Board.IntTuple> matches = board.nearbyMatches(4,4, BrownPiece.INSTANCE);
        Assert.assertEquals(3, matches.size());
    }

    @Test
    public void test_nearbyMatches(){
        setBoardToBrown(board);
        board.nearbyMatches(5,7, BrownPiece.INSTANCE);
        board.nearbyMatches(4,6, BrownPiece.INSTANCE);
        board.nearbyMatches(3,5, BrownPiece.INSTANCE);
        board.nearbyMatches(2,4, BrownPiece.INSTANCE);
    }

    @Test
    public void test_shiftHorizontally(){
        board.shiftHorizontally(0, 0);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[0][0]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][1]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[0][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[0][3]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[0][4]);

        board.shiftHorizontally(1, 1);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[1][0]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[1][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[1][2]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[1][3]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[1][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[1][5]);

        board.shiftHorizontally(2, 2);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[2][0]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[2][1]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][2]);
        Assert.assertEquals(YellowPiece.INSTANCE, board.getPieces()[2][3]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[2][4]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[2][5]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[2][6]);

        board.shiftHorizontally(3, 3);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[3][0]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[3][1]);
        Assert.assertEquals(BrownPiece.INSTANCE, board.getPieces()[3][2]);
        Assert.assertEquals(DarkBluePiece.INSTANCE, board.getPieces()[3][3]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][4]);
        Assert.assertEquals(LightBluePiece.INSTANCE, board.getPieces()[3][5]);
        Assert.assertEquals(GrayPiece.INSTANCE, board.getPieces()[3][6]);
        Assert.assertEquals(BlackPiece.INSTANCE, board.getPieces()[3][7]);
    }

    private void setBoardToBrown(Board board) {
        Arrays.stream(board.getPieces()).forEach(row ->
            Arrays.fill(row, BrownPiece.INSTANCE)
        );
    }
}