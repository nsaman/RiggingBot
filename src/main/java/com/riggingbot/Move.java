package com.riggingbot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Move {
    private Direction direction;
    private int row;
    private int moveIndex;

    public Move(int moveIndex) {
        Move move = Board.getMoveByIndex(moveIndex);
        this.direction = move.direction;
        this.row = move.row;
        this.moveIndex = move.moveIndex;
    }
}
