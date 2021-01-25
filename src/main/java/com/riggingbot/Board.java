package com.riggingbot;

import com.riggingbot.piece.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

import static com.riggingbot.PieceSearch.*;

@Data
@AllArgsConstructor
public class Board {
    public static final int MOVES_PER_DIRECTION = 52;
    public static final int TOTAL_MOVES = MOVES_PER_DIRECTION * 3;

    private int activeRig;
    private Piece[][] pieces;

    //sloppy, used to find tars
    Piece clearingPiece = null;

    public Board() {
        pieces = new Piece[9][];
        for(int y = 0; y < NUM_ROWS; y++) {
            int pieceCountInRow = CENTER_ROW_COUNT - Math.abs(ROW_OFFSET - y);
            pieces[y] = new Piece[pieceCountInRow];
        }
    }

    public Board clone() {
        Piece[][] pieces = Arrays.stream(this.pieces).map(Piece[]::clone).toArray(Piece[][]::new);
        return new Board(activeRig, pieces, null);
    }

    // sets all used pieces to future piece and returns count
    public int doRig() {
        // recursively set each piece to FuturePiece if it touches the activeRig
        IntTuple activeRigCoords = getActiveRigCoordinates();
        if(pieces[activeRigCoords.y][activeRigCoords.x] instanceof SpecialPiece)
            return 0;

        Map<Integer, Set<Integer>> clearedPieces = new HashMap<>();
        Map<Integer, Set<Integer>> newClearedPieces = new HashMap<>();
        safeAdd(clearedPieces, activeRigCoords.y, activeRigCoords.x);
        safeAdd(newClearedPieces, activeRigCoords.y, activeRigCoords.x);
        Piece searchPiece = pieces[activeRigCoords.y][activeRigCoords.x];
        while (newClearedPieces.size() > 0) {
            Map<Integer, Set<Integer>> thisPieces = new HashMap<>();

            newClearedPieces.forEach((y, xSet) -> {
                xSet.forEach(x -> {
                    Set<IntTuple> currentNearby = nearbyMatches(y, x, searchPiece);
                    currentNearby.stream()
                            .filter(i -> !safeContains(clearedPieces, i.y, i.x))
                            .forEach(i -> {
                                safeAdd(clearedPieces, i.y, i.x);
                                safeAdd(thisPieces, i.y, i.x);
                            });
                });
            });
            newClearedPieces = thisPieces;
        }

        int score = clearedPieces.values().stream().map(Set::size).reduce(0, Integer::sum);

        if(score >= 3) {
            if(score >= 5) {
                clearingPiece = searchPiece;
            }
            for (int y : clearedPieces.keySet()) {
                Set<Integer> xPieces = clearedPieces.get(y);
                xPieces.forEach(x -> pieces[y][x] = FuturePiece.INSTANCE);
            }
        } else {
            score = 0;
        }

        return score;
    }

    // sets all futures to null, and handles looped pieces
    public int doClear() {
        int bonusScore = 0;
        // todo add looped score

        //valuing tar
        int leftOverPieces = 0;

        for (int y = 0; y < pieces.length; y++) {
            for (int x = 0; x < pieces[y].length; x++){
                if(pieces[y][x] == FuturePiece.INSTANCE)
                    pieces[y][x] = NullPiece.INSTANCE;
                else if(pieces[y][x] == clearingPiece)
                    leftOverPieces += 1;
            }
        }

        if(clearingPiece != null && leftOverPieces == 0)
            bonusScore+=10;

        return bonusScore;
    }

    public void makeMove(int moveIndex) {
        Move move = getMoveByIndex(moveIndex);

        switch (move.getDirection()) {
            case Horizontal -> shiftHorizontally(move.getRow(), move.getMoveIndex());
            case DownRight -> shiftDownRight(move.getRow(), move.getMoveIndex());
            case DownLeft -> shiftDownLeft(move.getRow(), move.getMoveIndex());
        }
    }

    public static Move getMoveByIndex(int moveIndex) {
        assert(moveIndex >= 0 && moveIndex < Board.TOTAL_MOVES);

        Direction direction;
        if(moveIndex / MOVES_PER_DIRECTION == 0)
            direction = Direction.Horizontal;
        else if(moveIndex / MOVES_PER_DIRECTION == 1)
            direction = Direction.DownRight;
        else
            direction = Direction.DownLeft;

        int directionIndex = moveIndex % MOVES_PER_DIRECTION;

        int row;
        int rowIndex;
        if (directionIndex <= 3) {
            row = 0;
            rowIndex = directionIndex;
        }
        else if(directionIndex <= 8) {
            row = 1;
            rowIndex = directionIndex - 4;
        }
        else if(directionIndex <= 14) {
            row = 2;
            rowIndex = directionIndex - 9;
        }
        else if(directionIndex <= 21) {
            row = 3;
            rowIndex = directionIndex - 15;
        }
        else if(directionIndex <= 29) {
            row = 4;
            rowIndex = directionIndex - 22;
        }
        else if(directionIndex <= 36) {
            row = 5;
            rowIndex = directionIndex - 30;
        }
        else if(directionIndex <= 42) {
            row = 6;
            rowIndex = directionIndex - 37;
        }
        else if(directionIndex <= 47) {
            row = 7;
            rowIndex = directionIndex - 43;
        }
        else {
            row = 8;
            rowIndex = directionIndex - 48;
        }

        return new Move(direction, row, rowIndex);
    }

    private void shiftHorizontally(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        // using a buffer but I really don't like it
        Piece[] copy = pieces[row].clone();
        for(int i = 0; i < copy.length; i++) {
            pieces[row][(i + amount) % copy.length] = copy[i];
        }
    }

    private void shiftDownRight(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        int startingRow = Math.max(0, row - 4);
        Piece[] copy = new Piece[pieces[row].length];
        // copy to buffer with move offset
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = ((i - amount) + pieces[row].length) % pieces[row].length + startingRow;
            copy[i] = pieces[sourceRow]
                    [sourceRow > 4 ? pieces.length - 1 - row : pieces[sourceRow].length - 1 - row];
        }
        // apply buffer to board
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = startingRow + i;
            pieces[sourceRow]
                  [sourceRow > 4 ? pieces.length - 1 - row : pieces[sourceRow].length - 1 - row]
                    = copy[i];
        }
    }

    private void shiftDownLeft(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
        int startingRow = Math.max(0, row - 4);
        Piece[] copy = new Piece[pieces[row].length];
        // copy to buffer with move offset
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = ((i - amount) + pieces[row].length) % pieces[row].length + startingRow;
            copy[i] = pieces[sourceRow]
                    [sourceRow <= 4 ? row : row - (sourceRow - 4)];
        }
        // apply buffer to board
        for(int i = 0; i < pieces[row].length; i++){
            int sourceRow = startingRow + i;
            pieces[sourceRow]
                    [sourceRow <= 4 ? row : row - (sourceRow - 4)]
                    = copy[i];
        }
    }

    private IntTuple getActiveRigCoordinates() {
        return switch (activeRig) {
            case 0 -> topRig;
            case 1 -> topRightRig;
            case 2 -> bottomtopRightRigRig;
            case 3 -> bottomRig;
            case 4 -> bottomLeftRig;
            case 5 -> topLeftRig;
            default -> throw new IllegalStateException("Found active rig = " + activeRig);
        };
    }

    //visibile for testing
    static class IntTuple {
        private final int y;
        private final int x;

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

    private static final IntTuple topRig = new IntTuple(0, 2);
    private static final IntTuple topRightRig = new IntTuple(2, 6);
    private static final IntTuple bottomtopRightRigRig = new IntTuple(6, 6);
    private static final IntTuple bottomRig = new IntTuple(8, 2);
    private static final IntTuple bottomLeftRig = new IntTuple(6, 0);
    private static final IntTuple topLeftRig = new IntTuple(2, 0);

    private void safeAdd(Map<Integer, Set<Integer>> map, Integer y, Integer x) {
        if(!map.containsKey(y))
            map.put(y, new HashSet<>());

        map.get(y).add(x);
    }

    private boolean safeContains(Map<Integer, Set<Integer>> map, Integer y, Integer x) {
        return map.containsKey(y) && map.get(y).contains(x);
    }

    //visible for testing
    Set<IntTuple> nearbyMatches(int y, int x, Piece piece) {
        Set<IntTuple> matches = new HashSet<>();
        // aboves
        if(y > 0) {
            int bottomHalfOffset = y < 5 ? 0 : -1;
            if(x > bottomHalfOffset && (piece == pieces[y - 1][x - 1 - bottomHalfOffset] || pieces[y - 1][x - 1 - bottomHalfOffset] instanceof WildPiece))
                matches.add(new IntTuple(y - 1, x - 1 - bottomHalfOffset));
            if(x < pieces[y - 1].length + bottomHalfOffset && (piece == pieces[y - 1][x - bottomHalfOffset] || pieces[y - 1][x - bottomHalfOffset] instanceof WildPiece))
                matches.add(new IntTuple(y - 1, x - bottomHalfOffset));
        }
        // horizontals
        if(x > 0 && (piece == pieces[y][x - 1] || pieces[y][x - 1] instanceof WildPiece))
            matches.add(new IntTuple(y, x - 1));
        if(x < pieces[y].length - 1 && (piece == pieces[y][x + 1] || pieces[y][x + 1] instanceof WildPiece))
            matches.add(new IntTuple(y, x + 1));
        //belows
        if(y < pieces.length - 1) {
            int bottomHalfOffset = y < 4 ? -1 : 0;
            if(x > bottomHalfOffset && (piece == pieces[y + 1][x - 1 - bottomHalfOffset] || pieces[y + 1][x - 1 - bottomHalfOffset] instanceof WildPiece))
                matches.add(new IntTuple(y + 1, x - 1 - bottomHalfOffset));
            if(x < pieces[y + 1].length + bottomHalfOffset && (piece == pieces[y + 1][x - bottomHalfOffset] || pieces[y + 1][x - bottomHalfOffset] instanceof WildPiece))
                matches.add(new IntTuple(y + 1, x - bottomHalfOffset));
        }

        return matches;
    }
}
