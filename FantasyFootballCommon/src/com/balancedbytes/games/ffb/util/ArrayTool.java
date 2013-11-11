package com.balancedbytes.games.ffb.util;

import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class ArrayTool {
  
  public static boolean isProvided(boolean[] pArray) {
    return ((pArray != null) && (pArray.length > 0));
  }

  public static boolean isProvided(char[] pArray) {
    return ((pArray != null) && (pArray.length > 0));
  }
  
  public static boolean isProvided(byte[] pArray) {
    return ((pArray != null) && (pArray.length > 0));
  }

  public static boolean isProvided(int[] pArray) {
    return ((pArray != null) && (pArray.length > 0));
  }
  
  public static boolean isProvided(Object[] pArray) {
    return ((pArray != null) && (pArray.length > 0));
  }

  public static int total(int[] pArray) {
    int total = 0;
    if (isProvided(pArray)) {
      for (int i = 0; i < pArray.length; i++) {
        total += pArray[i];
      }
    }
    return total;
  }

  public static String join(String[] pArray, String pJoin) {
    String result = null;
    if (pArray != null) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < pArray.length; i++) {
        if (i > 0) {
          buffer.append(pJoin);
        }
        buffer.append(pArray[i]);
      }
      result = buffer.toString();
    }
    return result;
  }
  
  public static String join(byte[] pArray, String pJoin) {
    String result = null;
    if (pArray != null) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < pArray.length; i++) {
        if (i > 0) {
          buffer.append(pJoin);
        }
        buffer.append(pArray[i]);
      }
      result = buffer.toString();
    }
    return result;
  }

  public static String join(int[] pArray, String pJoin) {
    String result = null;
    if (pArray != null) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < pArray.length; i++) {
        if (i > 0) {
          buffer.append(pJoin);
        }
        buffer.append(pArray[i]);
      }
      result = buffer.toString();
    }
    return result;
  }

  public static String join(boolean[] pArray, String pJoin) {
    String result = null;
    if (pArray != null) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < pArray.length; i++) {
        if (i > 0) {
          buffer.append(pJoin);
        }
        buffer.append(pArray[i]);
      }
      result = buffer.toString();
    }
    return result;
  }

  public static boolean isEqual(Object[] pArray1, Object[] pArray2) {
    boolean isEqual = false;
    if (isProvided(pArray1) && isProvided(pArray2)) {
      isEqual = (pArray1.length == pArray2.length);
      for (int i = 0; isEqual && (i < pArray1.length); i++) {
        isEqual = pArray1[i].equals(pArray2[i]);
      }
    } else {
      isEqual = (!isProvided(pArray1) && !isProvided(pArray2));
    }
    return isEqual;
  }

  public static boolean isEqual(int[] pArray1, int[] pArray2) {
    boolean isEqual = false;
    if (isProvided(pArray1) && isProvided(pArray2)) {
      isEqual = (pArray1.length == pArray2.length);
      for (int i = 0; isEqual && (i < pArray1.length); i++) {
        isEqual = pArray1[i] == pArray2[i];
      }
    } else {
      isEqual = (!isProvided(pArray1) && !isProvided(pArray2));
    }
    return isEqual;
  }
  
  public static int[] toIntArray(List<Integer> pIntegerList) {
    int[] result = new int[0];
    if ((pIntegerList != null) && (pIntegerList.size() > 0)) {
      result = new int[pIntegerList.size()];
      for (int i = 0; i < pIntegerList.size(); i++) {
        result[i] = pIntegerList.get(i);
      }
    }
    return result;
  }
  
  public static boolean[] toBooleanArray(List<Boolean> pBooleanList) {
    boolean[] result = new boolean[0];
    if ((pBooleanList != null) && (pBooleanList.size() > 0)) {
      result = new boolean[pBooleanList.size()];
      for (int i = 0; i < pBooleanList.size(); i++) {
        result[i] = pBooleanList.get(i);
      }
    }
    return result;
  }
  
  public static String firstElement(String[] pArray) {
    if (!isProvided(pArray)) {
      return null;
    } else {
      return pArray[0];
    }
  }

}
