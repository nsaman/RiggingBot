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

    public boolean movesOnRig(int rigIndex) {
        switch (rigIndex) {
            case 0: return (direction == Direction.Horizontal && row == 0) ||
                    (direction == Direction.DownRight && row == 2) ||
                    (direction == Direction.DownLeft && row == 2);
            case 1: return (direction == Direction.Horizontal && row == 2) ||
                    (direction == Direction.DownRight && row == 0) ||
                    (direction == Direction.DownLeft && row == 6);
            case 2: return (direction == Direction.Horizontal && row == 6) ||
                    (direction == Direction.DownRight && row == 2) ||
                    (direction == Direction.DownLeft && row == 8);
            case 3: return (direction == Direction.Horizontal && row == 8) ||
                    (direction == Direction.DownRight && row == 6) ||
                    (direction == Direction.DownLeft && row == 6);
            case 4: return (direction == Direction.Horizontal && row == 6) ||
                    (direction == Direction.DownRight && row == 8) ||
                    (direction == Direction.DownLeft && row == 2);
            case 5: return (direction == Direction.Horizontal && row == 2) ||
                    (direction == Direction.DownRight && row == 6) ||
                    (direction == Direction.DownLeft && row == 0);
        }
        throw new IllegalArgumentException("invalid rig=" + rigIndex);
    }
}
