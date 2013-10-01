package com.balancedbytes.games.ffb.server.util;

/**
 * 
 * @author Kalimar
 */
public class UtilDb {
  
  public static int convertRollToInt(int[] pRoll) {
    int convertedRoll = 0;
    if (pRoll != null) {
      if (pRoll.length == 1) {
        convertedRoll = pRoll[0];
      }
      if (pRoll.length == 2) {
        convertedRoll = (pRoll[0] * 10) + pRoll[1];
      }
      if (pRoll.length == 3) {
        convertedRoll = (pRoll[0] * 100) + (pRoll[1] * 10) + pRoll[2];
      }
    }
    return convertedRoll;
  }
  
  public static int[] convertIntToRoll(int pInt) {
    int[] convertedRoll;
    if (pInt >= 100) {
      convertedRoll = new int[3];
      convertedRoll[0] = pInt / 100;
      convertedRoll[1] = (pInt % 100) / 10;
      convertedRoll[2] = (pInt % 100) % 10;
    } else if (pInt >= 10) {
      convertedRoll = new int[2];
      convertedRoll[0] = pInt / 10;
      convertedRoll[1] = pInt % 10;
    } else if (pInt > 0) {
      convertedRoll = new int[1];
      convertedRoll[0] = pInt;
    } else {
      convertedRoll = null;
    }
    return convertedRoll;
  }

}
