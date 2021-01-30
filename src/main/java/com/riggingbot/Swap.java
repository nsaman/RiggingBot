package com.riggingbot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Jacob on 7/13/2015.
 */
@Getter
@AllArgsConstructor
public class Swap
{
    private int moveIndex;
    private int points;

    @Override
    public String toString()
    {
        Move move = new Move(moveIndex);

        return String.format("%d (%s, row=%s, moves=%d : %d)", points, move.getDirection().name(), move.getRow(), move.getMoveIndex() + 1, moveIndex);
    }

}
