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
    private IntTuple splice = null;

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
        return new Board(activeRig, pieces, null, null);
    }

    // sets all used pieces to future piece and returns count
    public int doRig() {
        // recursively set each piece to FuturePiece if it touches the activeRig
        IntTuple activeRigCoords = getActiveRigCoordinates();
        if(pieces[activeRigCoords.y][activeRigCoords.x] instanceof SpecialPiece)
            return 0;

        //todo support gaff clearing

        Map<Integer, Set<Integer>> clearedPieces = new HashMap<>();
        safeAdd(clearedPieces, activeRigCoords.y, activeRigCoords.x);
        Piece searchPiece = getChainedPieces(clearedPieces, new IntTuple(activeRigCoords.y, activeRigCoords.x));

        // initial clear before splice
        int cleared = clearedPieces.values().stream().map(Set::size).reduce(0, Integer::sum);
        int score;

        if(cleared - (splice == null ? 0 : 1) >= 3) {
            if(cleared - (splice == null ? 0 : 1) >= 5) {
                clearingPiece = searchPiece;
            }

            if (splice != null) {
                Piece splicePiece = pieces[splice.y][splice.x];
                if(splicePiece == SpliceHorizontalPiece.INSTANCE) {
                    if(splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                        if(searchPiece != pieces[splice.y][splice.x - 1]) {
                            getChainedPieces(clearedPieces, new IntTuple(splice.y, splice.x - 1));
                        } else if(searchPiece != pieces[splice.y][splice.x + 1]) {
                            getChainedPieces(clearedPieces, new IntTuple(splice.y, splice.x + 1));
                        }
                    }
                } else if(splice.y > 0 && splice.y < pieces.length - 1) {
                     if(splicePiece == SpliceDownRightPiece.INSTANCE ) {
                         if (splice.y < 4 && splice.x > 0) {
                             if(searchPiece != pieces[splice.y - 1][splice.x - 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x - 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x + 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x + 1));
                             }
                         } else if(splice.y == 4 && splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x - 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x - 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         }
                         else if (splice.y > 4 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         }
                     } else {
                         if (splice.y < 4 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         } else if(splice.y == 4 && splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x - 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x - 1));
                             }
                         }
                         else if (splice.y > 4 && splice.x > 0) {
                             if(searchPiece != pieces[splice.y - 1][splice.x + 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x + 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x - 1]) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x - 1));
                             }
                         }
                     }
                }
            }

            // chain before gaff
            cleared = clearedPieces.values().stream().map(Set::size).reduce(0, Integer::sum);
            score = (int)((cleared * cleared * .1) /2) + cleared;

            for (int y : clearedPieces.keySet()) {
                Set<Integer> xPieces = clearedPieces.get(y);
                xPieces.forEach(x -> pieces[y][x] = FuturePiece.INSTANCE);
            }
        } else {
            score = 0;
        }

        boolean spliceFlag = false;
        IntTuple plusTwoRig = getRigCoordinates((activeRig + 2) % 6);
        if(clearedPieces.containsKey(plusTwoRig.y) && clearedPieces.get(plusTwoRig.y).contains(plusTwoRig.x)) {
            spliceFlag = true;
            score += 5;
        }
        IntTuple plusThreeRig = getRigCoordinates((activeRig + 3) % 6);
        if(clearedPieces.containsKey(plusThreeRig.y) && clearedPieces.get(plusThreeRig.y).contains(plusThreeRig.x)) {
            spliceFlag = true;
            score += 5;
        }
        IntTuple plusFourRig = getRigCoordinates((activeRig + 4) % 6);
        if(clearedPieces.containsKey(plusFourRig.y) && clearedPieces.get(plusFourRig.y).contains(plusFourRig.x)) {
            spliceFlag = true;
            score += 5;
        }
        if (spliceFlag) {
            score += 5;
            IntTuple plusOneRig = getRigCoordinates((activeRig + 1) % 6);
            if(clearedPieces.containsKey(plusOneRig.y) && clearedPieces.get(plusOneRig.y).contains(plusOneRig.x)) {
                score += 5;
            }
            IntTuple plusFiveRig = getRigCoordinates((activeRig + 5) % 6);
            if(clearedPieces.containsKey(plusFiveRig.y) && clearedPieces.get(plusFiveRig.y).contains(plusFiveRig.x)) {
                score += 5;
            }
        }

        return score;
    }

    private Piece getChainedPieces(Map<Integer, Set<Integer>> clearedPieces, IntTuple startingCoords) {
        Map<Integer, Set<Integer>> newClearedPieces = new HashMap<>();
        safeAdd(newClearedPieces, startingCoords.y, startingCoords.x);
        Piece searchPiece = pieces[startingCoords.y][startingCoords.x];
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

        return searchPiece;
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

    // visible for testing
    void shiftHorizontally(int row, int moveIdentity) {
        int amount = moveIdentity + 1;
//        Piece[] arr =  pieces[row];
//         using a buffer but I really don't like it
        Piece[] copy = pieces[row].clone();
        for(int i = 0; i < copy.length; i++) {
            pieces[row][(i + amount) % copy.length] = copy[i];
        }

        //https://stackoverflow.com/questions/876293/fastest-algorithm-for-circle-shift-n-sized-array-for-m-position
//        int i, j, k;
//        Piece tmp;
//        int gcd = gcd(arr.length, amount);
//
//        for(i = 0; i < gcd; i++) {
//            // start cycle at i
//            tmp = arr[i];
//            for(j = i; true; j = k) {
//                k = j+amount;
//                if(k >= arr.length) k -= arr.length; // wrap around if we go outside array
//                if(k == i) break; // end of cycle
//                arr[j] = arr[k];
//            }
//            arr[j] = tmp;
//        }
    }

    //https://en.wikipedia.org/wiki/Binary_GCD_algorithm
    int gcd(int u, int v)
    {
        // Base cases
        //  gcd(n, n) = n
        if (u == v)
            return u;

        //  Identity 1: gcd(0, n) = gcd(n, 0) = n
        if (u == 0)
            return v;
        if (v == 0)
            return u;

        if (u % 2 == 0) { // u is even
            if (v % 2 == 1) // v is odd
                return gcd(u/2, v); // Identity 3
            else // both u and v are even
                return 2 * gcd(u/2, v/2); // Identity 2

        } else { // u is odd
            if (v % 2 == 0) // v is even
                return gcd(u, v/2); // Identity 3

            // Identities 4 and 3 (u and v are odd, so u-v and v-u are known to be even)
            if (u > v)
                return gcd((u - v)/2, v);
            else
                return gcd((v - u)/2, u);
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
        return getRigCoordinates(activeRig);
    }

    private IntTuple getRigCoordinates(int rigId) {
        return switch (rigId) {
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
            if(x > bottomHalfOffset) {
                if(piece == pieces[y - 1][x - 1 - bottomHalfOffset] || pieces[y - 1][x - 1 - bottomHalfOffset] instanceof WildPiece)
                    matches.add(new IntTuple(y - 1, x - 1 - bottomHalfOffset));
                else if(pieces[y - 1][x - 1 - bottomHalfOffset] instanceof SpliceDownLeftPiece) {
                    splice = new IntTuple(y - 1, x - 1 - bottomHalfOffset);
                    matches.add(splice);
                }
            }
            if(x < pieces[y - 1].length + bottomHalfOffset) {
                if (piece == pieces[y - 1][x - bottomHalfOffset] || pieces[y - 1][x - bottomHalfOffset] instanceof WildPiece)
                    matches.add(new IntTuple(y - 1, x - bottomHalfOffset));
                else if(pieces[y - 1][x - bottomHalfOffset] instanceof SpliceDownRightPiece) {
                    splice = new IntTuple(y - 1, x - bottomHalfOffset);
                    matches.add(splice);
                }
            }
        }
        // horizontals
        if(x > 0) {
            if (piece == pieces[y][x - 1] || pieces[y][x - 1] instanceof WildPiece)
                matches.add(new IntTuple(y, x - 1));
            else if(pieces[y][x - 1] instanceof SpliceHorizontalPiece) {
                splice = new IntTuple(y, x - 1);
                matches.add(splice);
            }
        }
        if(x < pieces[y].length - 1) {
            if (piece == pieces[y][x + 1] || pieces[y][x + 1] instanceof WildPiece)
                matches.add(new IntTuple(y, x + 1));
            else if(pieces[y][x + 1] instanceof SpliceHorizontalPiece) {
                splice = new IntTuple(y, x + 1);
                matches.add(splice);
            }
        }
        //belows
        if(y < pieces.length - 1) {
            int bottomHalfOffset = y < 4 ? -1 : 0;
            if (x > bottomHalfOffset ) {
                if (piece == pieces[y + 1][x - 1 - bottomHalfOffset] || pieces[y + 1][x - 1 - bottomHalfOffset] instanceof WildPiece)
                    matches.add(new IntTuple(y + 1, x - 1 - bottomHalfOffset));
                else if (pieces[y + 1][x - 1 - bottomHalfOffset] instanceof SpliceDownLeftPiece) {
                    splice = new IntTuple(y + 1, x  - 1 - bottomHalfOffset);
                    matches.add(splice);
                }
            }
            if(x < pieces[y + 1].length + bottomHalfOffset) {
                if ((piece == pieces[y + 1][x - bottomHalfOffset] || pieces[y + 1][x - bottomHalfOffset] instanceof WildPiece)) {
                    matches.add(new IntTuple(y + 1, x - bottomHalfOffset));
                }
                else if (pieces[y + 1][x - bottomHalfOffset] instanceof SpliceDownRightPiece) {
                    splice = new IntTuple(y + 1, x - bottomHalfOffset);
                    matches.add(splice);
                }
            }
        }

        return matches;
    }
}