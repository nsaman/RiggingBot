package com.riggingbot;

import com.riggingbot.piece.*;
import lombok.Data;

import java.util.*;

import static com.riggingbot.PieceSearch.*;

@Data
public class Board {
    public static final int MOVES_PER_DIRECTION = 52;
    public static final int TOTAL_MOVES = MOVES_PER_DIRECTION * 3;

    private int activeRig;
    private Piece[][] pieces;
    private IntTuple splice = null;
    private Set<IntTuple> possibleGaffs = new HashSet<>();
    private Set<IntTuple> gaffs;
    private Set<Integer> impactedRows = new HashSet<>();

    //sloppy, used to find tars
    Piece clearingPiece = null;

    public Board() {
        pieces = new Piece[9][];
        for(int y = 0; y < NUM_ROWS; y++) {
            int pieceCountInRow = CENTER_ROW_COUNT - Math.abs(ROW_OFFSET - y);
            pieces[y] = new Piece[pieceCountInRow];
        }
    }

    public Board(int activeRig, Piece[][] pieces) {
        this.activeRig = activeRig;
        this.pieces = pieces;
    }

    public Board clone() {
        Piece[][] pieces = Arrays.stream(this.pieces).map(Piece[]::clone).toArray(Piece[][]::new);
        return new Board(activeRig, pieces);
    }

    // sets all used pieces to future piece and returns count
    public int doRig() {
        // recursively set each piece to FuturePiece if it touches the activeRig
        IntTuple activeRigCoords = getActiveRigCoordinates();
        if(pieces[activeRigCoords.y][activeRigCoords.x] instanceof SpecialPiece)
            return 0;

        Map<Integer, Set<Integer>> clearedPieces = new HashMap<>();
        safeAdd(clearedPieces, activeRigCoords.y, activeRigCoords.x);
        Piece searchPiece = getChainedPieces(clearedPieces, new IntTuple(activeRigCoords.y, activeRigCoords.x));

        // initial clear before splice
        int cleared = clearedPieces.values().stream().map(Set::size).reduce(0, Integer::sum);
        int score;

        if((cleared - (splice == null ? 0 : 1) >= 3) || (cleared > 0 && possibleGaffs.size() > 0)) {
            gaffs = possibleGaffs;
            possibleGaffs = new HashSet<>();

            if(cleared - (splice == null ? 0 : 1) >= 5) {
                clearingPiece = searchPiece;
            }

            // todo check if chain of standard in to wild into splice work
            // calculate the splice
            if (splice != null) {
                Piece splicePiece = pieces[splice.y][splice.x];
                if(splicePiece == SpliceHorizontalPiece.INSTANCE) {
                    if(splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                        if(searchPiece != pieces[splice.y][splice.x - 1] && pieces[splice.y][splice.x - 1] instanceof StandardPiece) {
                            getChainedPieces(clearedPieces, new IntTuple(splice.y, splice.x - 1));
                        } else if(searchPiece != pieces[splice.y][splice.x + 1] && pieces[splice.y][splice.x + 1] instanceof StandardPiece) {
                            getChainedPieces(clearedPieces, new IntTuple(splice.y, splice.x + 1));
                        }
                    }
                } else if(splice.y > 0 && splice.y < pieces.length - 1) {
                     if(splicePiece == SpliceDownRightPiece.INSTANCE ) {
                         if (splice.y < 4 && splice.x > 0) {
                             if(searchPiece != pieces[splice.y - 1][splice.x - 1] && pieces[splice.y - 1][splice.x - 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x - 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x + 1] && pieces[splice.y + 1][splice.x + 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x + 1));
                             }
                         } else if(splice.y == 4 && splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x - 1] && pieces[splice.y - 1][splice.x - 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x - 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x] && pieces[splice.y + 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         }
                         else if (splice.y > 4 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x] && pieces[splice.y - 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x] && pieces[splice.y + 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         }
                     } else { //downleft
                         if (splice.y < 4 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x] && pieces[splice.y - 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x] && pieces[splice.y + 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x));
                             }
                         } else if(splice.y == 4 && splice.x > 0 && splice.x < pieces[splice.y].length - 1) {
                             if(searchPiece != pieces[splice.y - 1][splice.x] && pieces[splice.y - 1][splice.x] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x - 1] && pieces[splice.y + 1][splice.x - 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x - 1));
                             }
                         }
                         else if (splice.y > 4 && splice.x > 0) {
                             if(searchPiece != pieces[splice.y - 1][splice.x + 1] && pieces[splice.y - 1][splice.x + 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y - 1, splice.x + 1));
                             } else if(searchPiece != pieces[splice.y + 1][splice.x - 1] && pieces[splice.y + 1][splice.x - 1] instanceof StandardPiece) {
                                 getChainedPieces(clearedPieces, new IntTuple(splice.y + 1, splice.x - 1));
                             }
                         }
                     }
                }

                gaffs.addAll(possibleGaffs);
            }

            // chain before gaff
            cleared = clearedPieces.values().stream().map(Set::size).reduce(0, Integer::sum);
            score = (int)((cleared * cleared * .1) /2) + cleared;

            score += applyGaffs();

            for (int y : clearedPieces.keySet()) {
                Set<Integer> xPieces = clearedPieces.get(y);
                xPieces.forEach(x -> pieces[y][x] = FuturePiece.INSTANCE);
                impactedRows.add(y);
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

    private int applyGaffs() {
        int gaffedPieces = 0;
        for(IntTuple gaff : gaffs) {
            //aboves
            if(gaff.y < 5 && gaff.y > 0) {
                if(gaff.x > 0 && pieces[gaff.y - 1][gaff.x - 1] != NullPiece.INSTANCE && pieces[gaff.y - 1][gaff.x - 1] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y - 1][gaff.x - 1] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y - 1);
                }
                if(gaff.x < pieces[gaff.y - 1].length && pieces[gaff.y - 1][gaff.x] != NullPiece.INSTANCE && pieces[gaff.y - 1][gaff.x] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y - 1][gaff.x] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y - 1);
                }
            }
            else if(gaff.y >= 5) {
                if(pieces[gaff.y - 1][gaff.x] != NullPiece.INSTANCE && pieces[gaff.y - 1][gaff.x] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y - 1][gaff.x] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y - 1);
                }
                if(pieces[gaff.y - 1][gaff.x + 1] != NullPiece.INSTANCE && pieces[gaff.y - 1][gaff.x + 1] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y - 1][gaff.x + 1] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y - 1);
                }
            }

            // horizontals
            if(gaff.x > 0 && pieces[gaff.y][gaff.x - 1] != NullPiece.INSTANCE && pieces[gaff.y][gaff.x - 1] != FuturePiece.INSTANCE) {
                gaffedPieces += 1;
                pieces[gaff.y][gaff.x - 1] = FuturePiece.INSTANCE;
                impactedRows.add(gaff.y);
            }
            if(gaff.x < pieces[gaff.y].length - 1 && pieces[gaff.y][gaff.x + 1] != NullPiece.INSTANCE && pieces[gaff.y][gaff.x + 1] != FuturePiece.INSTANCE) {
                gaffedPieces += 1;
                pieces[gaff.y][gaff.x + 1] = FuturePiece.INSTANCE;
                impactedRows.add(gaff.y);
            }

            //belows
            if(gaff.y < 4) {
                if(pieces[gaff.y + 1][gaff.x] != NullPiece.INSTANCE && pieces[gaff.y + 1][gaff.x] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y + 1][gaff.x] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y + 1);
                }
                if(pieces[gaff.y + 1][gaff.x + 1] != NullPiece.INSTANCE && pieces[gaff.y + 1][gaff.x + 1] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y + 1][gaff.x + 1] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y + 1);
                }
            }
            else if(gaff.y < pieces.length - 1) {
                if(gaff.x > 0 && pieces[gaff.y + 1][gaff.x - 1] != NullPiece.INSTANCE && pieces[gaff.y + 1][gaff.x - 1] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y + 1][gaff.x - 1] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y + 1);
                }
                if(gaff.x < pieces[gaff.y + 1].length && pieces[gaff.y + 1][gaff.x] != NullPiece.INSTANCE && pieces[gaff.y + 1][gaff.x] != FuturePiece.INSTANCE) {
                    gaffedPieces += 1;
                    pieces[gaff.y + 1][gaff.x] = FuturePiece.INSTANCE;
                    impactedRows.add(gaff.y + 1);
                }
            }
        }
        return gaffedPieces;
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

        Map<Integer, Map<Integer, List<IntTuple>>>  possibleLooped = new HashMap<>();
        Map<Integer, Set<Integer>> leftOverPieces = new HashMap<>();

        // todo refactor tars out of clear board as they prevent limiting range limiting
        // todo tar to indexed
        for (int y = 0; y < pieces.length; y++) {
            for (int x = 0; x < pieces[y].length; x++) {
                if (pieces[y][x] == FuturePiece.INSTANCE)
                    pieces[y][x] = NullPiece.INSTANCE;
                else if (pieces[y][x] == clearingPiece)
                    safeAdd(leftOverPieces, y, x);
                // check for loops
                if (pieces[y][x] != NullPiece.INSTANCE && y > 0) {

                    // top side
                    if (y < 5 && x > 0) {
                        // end. if we're at the end and it isn't a future piece, cut the loop!
                        if (x == pieces[y].length - 1) {
                            clearAllLooped(possibleLooped, y, x - 1);
                        }
                        // possible loop
                        else if ( // left piece
                                (pieces[y][x - 1] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y, x - 1)) &&
                                        // UpLeft piece
                                        (pieces[y - 1][x - 1] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y - 1, x - 1)) &&
                                        // UpRight piece
                                        (pieces[y - 1][x] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y - 1, x))) {
                            List<IntTuple> loopList = safeMapGetList(possibleLooped, y, x - 1);
                            loopList = joinLooped(possibleLooped, loopList, safeMapGetList(possibleLooped, y - 1, x - 1));
                            loopList = joinLooped(possibleLooped, loopList, safeMapGetList(possibleLooped, y - 1, x));

                            if (loopList == null)
                                loopList = new ArrayList<>();

                            loopList.add(new IntTuple(y, x));
                            safeMapPut(possibleLooped, y, x, loopList);
                        }
                        // not loop
                        else {
                            clearAllLooped(possibleLooped, y, x - 1);
                            clearAllLooped(possibleLooped, y - 1, x - 1);
                            clearAllLooped(possibleLooped, y - 1, x);
                        }
                    }
                    else if (y < 8) {
                        if (x == 0) {
                            clearAllLooped(possibleLooped, y - 1, x + 1);
                        }
                        // end. if we're at the end and it isn't a future piece, cut the loop!
                        else if (x == pieces[y].length - 1) {
                            clearAllLooped(possibleLooped, y, x - 1);
                            clearAllLooped(possibleLooped, y - 1, x);
                        }
                        // possible loop
                        else if ( // left piece
                                (pieces[y][x - 1] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y, x - 1)) &&
                                        // UpLeft piece
                                        (pieces[y - 1][x] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y - 1, x)) &&
                                        // UpRight piece
                                        (pieces[y - 1][x + 1] == NullPiece.INSTANCE || safeMapContains(possibleLooped, y - 1, x + 1))) {
                            List<IntTuple> loopList = safeMapGetList(possibleLooped, y, x - 1);
                            loopList = joinLooped(possibleLooped, loopList, safeMapGetList(possibleLooped, y - 1, x));
                            loopList = joinLooped(possibleLooped, loopList, safeMapGetList(possibleLooped, y - 1, x + 1));

                            if (loopList == null)
                                loopList = new ArrayList<>();

                            loopList.add(new IntTuple(y, x));
                            safeMapPut(possibleLooped, y, x, loopList);
                        }
                        // not loop
                        else {
                            clearAllLooped(possibleLooped, y, x - 1);
                            clearAllLooped(possibleLooped, y - 1, x);
                            clearAllLooped(possibleLooped, y - 1, x + 1);
                        }
                    }
                    // bottom row
                    else  {
                        clearAllLooped(possibleLooped, y - 1, x);
                        clearAllLooped(possibleLooped, y - 1, x + 1);
                    }

                }
            }
        }

        // the fully streamed version of this is too hard
        Set<List<IntTuple>> loops = new HashSet<>();
        possibleLooped.values().forEach(yMap -> loops.addAll(yMap.values()));
        bonusScore += loops.size() * 5;
        bonusScore += loops.stream().reduce(0, (a, b) -> a + b.size(), Integer::sum) * 2;

        if(clearingPiece != null &&
                leftOverPieces.entrySet().stream().allMatch(yEntry -> yEntry.getValue().stream().allMatch(x -> safeMapContains(possibleLooped, yEntry.getKey(), x))))
            bonusScore+=10;

        return bonusScore;
    }

    private void clearAllLooped(Map<Integer, Map<Integer, List<IntTuple>>> possibleLooped, int y, int x) {
        if(safeMapContains(possibleLooped, y, x)) {
            List<IntTuple> tuples = possibleLooped.get(y).get(x);
            for(IntTuple tuple : tuples)
                possibleLooped.get(tuple.y).remove(tuple.x);
        }
    }

    private List<IntTuple> joinLooped(Map<Integer, Map<Integer, List<IntTuple>>> possibleLooped, List<IntTuple> tuples1, List<IntTuple> tuples2) {
        if(tuples1 == tuples2 || tuples2 == null)
            return tuples1;
        if(tuples1 == null)
            return tuples2;
        tuples1.addAll(tuples2);
        for(IntTuple tuple : tuples2)
            possibleLooped.get(tuple.y).put(tuple.x, tuples1);

        return tuples1;
    }

    public void makeMove(Move move) {

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
        Piece[] arr =  pieces[row];

        //https://stackoverflow.com/questions/876293/fastest-algorithm-for-circle-shift-n-sized-array-for-m-position
        int i, j, k;
        Piece tmp;
        int gcd = gcd(amount, arr.length);

        for(i = 0; i < gcd; i++) {
            // start cycle at i
            tmp = arr[i];
            for(j = i; true; j = k) {
                k = j-amount;
                if(k < 0) k += arr.length; // wrap around if we go outside array
                if(k == i) break; // end of cycle
                arr[j] = arr[k];
            }
            arr[j] = tmp;
        }
    }

    //https://en.wikipedia.org/wiki/Binary_GCD_algorithm
    int gcd(int u, int v)
    {
        if(u == 1 || v == 5 || v == 7)
            return 1;
        if(u == v)
            return 1;
        if(v == 6) {
            if(u == 2 || u == 4)
                return 2;
            if(u == 3)
                return 3;
            else
                return 1;
        }
        if (v == 8) {
            if(u == 2 || u == 6)
                return 2;
            if(u == 4)
                return 4;
            else
                return 1;
        }
        if (v == 9) {
            if(u == 3 || u == 6)
                return 3;
            else
                return 1;
        }

        throw new IllegalArgumentException("Yarrr, just use the ranges on a rigging board u=" + u + " v=" + v);
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

    private boolean safeMapContains(Map<Integer, Map<Integer, List<IntTuple>>> map, Integer y, Integer x) {
        return map.containsKey(y) && map.get(y).containsKey(x);
    }

    private List<IntTuple> safeMapGetList(Map<Integer, Map<Integer, List<IntTuple>>> map, Integer y, Integer x) {
        if (map.containsKey(y) && map.get(y).containsKey(x))
            return map.get(y).get(x);
        else
            return null;
    }

    private void safeMapPut(Map<Integer, Map<Integer, List<IntTuple>>> map, Integer y, Integer x, List<IntTuple> loopList) {
        if(!map.containsKey(y))
            map.put(y, new HashMap<>());
        map.get(y).put(x, loopList);
    }

    //visible for testing
    Set<IntTuple> nearbyMatches(int y, int x, Piece piece) {
        Set<IntTuple> matches = new HashSet<>();

        //aboves
        if(y < 5 && y > 0) {
            if(x > 0 && (pieces[y - 1][x - 1] == piece || pieces[y - 1][x - 1] instanceof WildPiece || pieces[y - 1][x - 1] instanceof SpliceDownRightPiece)) {
                IntTuple target = new IntTuple(y - 1,x - 1);
                matches.add(target);
                if(pieces[y - 1][x - 1] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y - 1][x - 1] == SpliceDownRightPiece.INSTANCE)
                    splice = target;
            }
            if(x < pieces[y - 1].length && (pieces[y - 1][x] == piece || pieces[y - 1][x] instanceof WildPiece || pieces[y - 1][x] instanceof SpliceDownLeftPiece)) {
                IntTuple target = new IntTuple(y - 1, x);
                matches.add(target);
                if(pieces[y - 1][x] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y - 1][x] == SpliceDownLeftPiece.INSTANCE)
                    splice = target;
            }
        }
        else if(y >= 5) {
            if(pieces[y - 1][x] == piece || pieces[y - 1][x] instanceof WildPiece || pieces[y - 1][x] instanceof SpliceDownRightPiece) {
                IntTuple target = new IntTuple(y - 1, x);
                matches.add(target);
                if(pieces[y - 1][x] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y - 1][x] == SpliceDownRightPiece.INSTANCE)
                    splice = target;
            }
            if(pieces[y - 1][x + 1] == piece || pieces[y - 1][x + 1] instanceof WildPiece || pieces[y - 1][x + 1] instanceof SpliceDownLeftPiece) {
                IntTuple target = new IntTuple(y - 1, x + 1);
                matches.add(target);
                if(pieces[y - 1][x + 1] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y - 1][x + 1] == SpliceDownLeftPiece.INSTANCE)
                    splice = target;
            }
        }

        // horizontals
        if(x > 0 && (pieces[y][x - 1] == piece || pieces[y][x - 1] instanceof WildPiece || pieces[y][x - 1] instanceof SpliceHorizontalPiece)) {
            IntTuple target = new IntTuple(y, x - 1);
            matches.add(target);
            if(pieces[y][x - 1] == GaffPiece.INSTANCE)
                possibleGaffs.add(target);
            else if(pieces[y][x - 1] == SpliceHorizontalPiece.INSTANCE)
                splice = target;
        }
        if(x < pieces[y].length - 1 && (pieces[y][x + 1] == piece || pieces[y][x + 1] instanceof WildPiece || pieces[y][x + 1] instanceof SpliceHorizontalPiece)) {
            IntTuple target = new IntTuple(y, x + 1);
            matches.add(target);
            if(pieces[y][x + 1] == GaffPiece.INSTANCE)
                possibleGaffs.add(target);
            else if(pieces[y][x + 1] == SpliceHorizontalPiece.INSTANCE)
                splice = target;
        }

        //belows
        if(y < 4) {
            if(pieces[y + 1][x] == piece || pieces[y + 1][x] instanceof WildPiece || pieces[y + 1][x] instanceof SpliceDownLeftPiece) {
                IntTuple target = new IntTuple(y + 1, x);
                matches.add(target);
                if(pieces[y + 1][x] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y + 1][x] == SpliceDownLeftPiece.INSTANCE)
                    splice = target;
            }
            if(pieces[y + 1][x + 1] == piece || pieces[y + 1][x + 1] instanceof WildPiece || pieces[y + 1][x + 1] instanceof SpliceDownRightPiece) {
                IntTuple target = new IntTuple(y + 1, x + 1);
                matches.add(target);
                if(pieces[y + 1][x + 1] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y + 1][x + 1] == SpliceDownRightPiece.INSTANCE)
                    splice = target;
            }
        }
        else if(y < pieces.length - 1) {
            if(x > 0 && (pieces[y + 1][x - 1] == piece || pieces[y + 1][x - 1] instanceof WildPiece || pieces[y + 1][x - 1] instanceof SpliceDownLeftPiece)) {
                IntTuple target = new IntTuple(y + 1, x - 1);
                matches.add(target);
                if(pieces[y + 1][x - 1] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y + 1][x - 1] == SpliceDownLeftPiece.INSTANCE)
                    splice = target;
            }
            if(x < pieces[y + 1].length && (pieces[y + 1][x] == piece || pieces[y + 1][x] instanceof WildPiece || pieces[y + 1][x] instanceof SpliceDownRightPiece)) {
                IntTuple target = new IntTuple(y + 1, x);
                matches.add(target);
                if(pieces[y + 1][x] == GaffPiece.INSTANCE)
                    possibleGaffs.add(target);
                else if(pieces[y + 1][x] == SpliceDownRightPiece.INSTANCE)
                    splice = target;
            }
        }

        return matches;
    }
}
