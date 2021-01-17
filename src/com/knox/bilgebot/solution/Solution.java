package com.knox.bilgebot.solution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jacob on 7/16/2015.
 */
public class Solution implements Comparable
{
    private int score;
    List<Integer> combos;

    public Solution(int score, List<Integer> combos)
    {
        this.score = score;
        this.combos = combos;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public int compareTo(Object o)
    {
        Solution solution = (Solution) o;

        return this.getScore() - solution.getScore();
    }

    @Override
    public String toString() {
        if(combos == null || combos.size() == 0)
            return "No combo";
       return combos.stream().map(Object::toString).collect(Collectors.joining("x"));
    }
}
