package com.bilgebot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.bilgebot.Board.MOVES_PER_DIRECTION;

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
        Direction direction;
        if(moveIndex / MOVES_PER_DIRECTION == 0)
            direction = Direction.Horizontal;
        else if(moveIndex / MOVES_PER_DIRECTION == 1)
            direction = Direction.DownRight;
        else
            direction = Direction.DownLeft;

        return String.format("%d (%s, %d)", points, direction.name(), moveIndex);
    }

}
