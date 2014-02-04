package com.balancedbytes.rpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class D6Statistics {
  
  public void show(int pExtraDice) {
    
    List<int[]> rolls = new ArrayList<int[]>();
    
    int nrOfD6 = 2 + Math.abs(pExtraDice);
    
    int[] lastRoll = new int[nrOfD6];
    lastRoll[0] = 0;
    for (int i = 1; i < lastRoll.length; i++) {
      lastRoll[i] = 1;
    }

    int nrOfCombinations = (int) Math.pow(6.0, (double)nrOfD6);

    for (int i = 0; i < nrOfCombinations; i++) {
      int[] newRoll = inc(lastRoll);
      rolls.add(newRoll);
      lastRoll = newRoll;
    }

    for (int i = 2; i <= 12; i++) {
      int count = (i > 2) ? count2D6GreaterOrEqual(rolls, i, (pExtraDice >= 0)) : nrOfCombinations;
      System.out.println("roll " + i + " or higher " + count + " of " + nrOfCombinations + " times or with " + (double)Math.round((double)count*10000/(double)nrOfCombinations)/100 + "% probability");
    }
    System.out.println("average roll is " + (double)Math.round(averageResultOn2D6(rolls, (pExtraDice >= 0))*100)/100);
  }
  
  private int[] inc(int[] pRoll) {
    int[] result = copy(pRoll);
    int incPos = 0;
    while ((incPos >= 0) && (incPos < result.length)) {
      result[incPos] += 1;
      if (result[incPos] > 6) {
        result[incPos] = 1;
        incPos++;
      } else {
        incPos = -1;
      }
    }
    return result;
  }
  
  private int[] copy(int[] pSource) {
    int[] dest = new int[pSource.length];
    System.arraycopy(pSource, 0, dest, 0, pSource.length);
    return dest;
  }
  
  private int count2D6GreaterOrEqual(List<int[]> pRolls, int pTarget, boolean pTakeHighest) {
    int count = 0;
    for (int i = 0; i < pRolls.size(); i++) {
      int[] roll = copy(pRolls.get(i));
      Arrays.sort(roll);
      if ((pTakeHighest && (roll[roll.length - 1] + roll[roll.length - 2] >= pTarget))
        || (!pTakeHighest && (roll[0] + roll[1] >= pTarget))) {
        count++;
      }
    }
    return count;
  }

  private double averageResultOn2D6(List<int[]> pRolls, boolean pTakeHighest) {
    int sum = 0;
    for (int i = 0; i < pRolls.size(); i++) {
      int[] roll = copy(pRolls.get(i));
      Arrays.sort(roll);
      if (pTakeHighest) {
        sum += roll[roll.length - 1] + roll[roll.length - 2];
      } else {
        sum += roll[0] + roll[1];
      }
    }
    return (double) sum / (double) pRolls.size();
  }
  
  public static void main(String[] args) {

    D6Statistics statistics = new D6Statistics();
    
    System.out.println("2D6");
    statistics.show(0);
    
    System.out.println();
    System.out.println("2D6 with 1 bonus die");
    statistics.show(1);

    System.out.println();
    System.out.println("2D6 with 2 bonus dice");
    statistics.show(2);

    System.out.println();
    System.out.println("2D6 with 3 bonus dice");
    statistics.show(3);

    System.out.println();
    System.out.println("2D6 with 1 penalty die");
    statistics.show(-1);

    System.out.println();
    System.out.println("2D6 with 2 penalty dice");
    statistics.show(-2);

    System.out.println();
    System.out.println("2D6 with 3 penalty dice");
    statistics.show(-3);

  }
  
}
