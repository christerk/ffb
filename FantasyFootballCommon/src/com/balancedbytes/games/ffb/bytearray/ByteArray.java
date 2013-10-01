package com.balancedbytes.games.ffb.bytearray;

import java.io.UnsupportedEncodingException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;

/**
 * 
 * @author Kalimar
 */
public class ByteArray {
  
  private static final boolean _TRACE = false;
  
  private int fPosition;
  private byte[] fByteArray;
  
  public ByteArray(byte[] pByteArray) {
    fByteArray = pByteArray;
    setPosition(0);
  }
  
  public byte getByte() {
    byte aByte = getByte(getPosition());
    advancePosition(1);
    return aByte;
  }
  
  public byte getByte(int pPosition) {
    byte aByte = getByteIntern(pPosition);
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> byte ").append(aByte);
      System.out.println(trace);
    }
    return aByte;
  }
  
  private byte getByteIntern(int pPosition) {
    if ((pPosition >= 0) && (pPosition < fByteArray.length)) {
      return fByteArray[pPosition];
    } else {
      return 0;
    }
  }

  public Boolean getBoolean() {
    Boolean aBoolean = getBoolean(getPosition());
    advancePosition(1);
    return aBoolean;
  }
  
  public Boolean getBoolean(int pPosition) {
    Boolean aBoolean;
    byte booleanByte = getByteIntern(pPosition);
    if (booleanByte > 0) {
      aBoolean = true;
    } else if (booleanByte < 0) {
      aBoolean = false;
    } else {
      aBoolean = null;
    }
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> Boolean ").append(aBoolean);
      System.out.println(trace);
    }
    return aBoolean;
  }
  
  public int getInt() {
    int anInt = getInt(getPosition());
    advancePosition(4);
    return anInt;
  }
  
  public int getInt(int pPosition) {
    int anInt = getIntIntern(pPosition);
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> int ").append(anInt);
      System.out.println(trace);
    }
    return anInt;
  }
  
  private int getIntIntern(int pPosition) {
    int anInt = 0;
    if ((pPosition >= 0) && (pPosition + 3 < fByteArray.length)) {
      anInt = convertByteArrayToInt(fByteArray, pPosition);
    }
    return anInt;
  }

  public long getLong() {
    long aLong = getLong(getPosition());
    advancePosition(8);
    return aLong;
  }
  
  public long getLong(int pPosition) {
    long aLong = 0;
    if ((pPosition >= 0) && (pPosition + 7 < fByteArray.length)) {
      aLong = convertByteArrayToLong(fByteArray, pPosition);
    }
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> long ").append(aLong);
      System.out.println(trace);
    }
    return aLong;
  }

  public int getSmallInt() {
    int anInt = getSmallInt(getPosition());
    advancePosition(2);
    return anInt;
  }
  
  public int getSmallInt(int pPosition) {
    int anInt = getSmallIntIntern(pPosition);
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> smallInt ").append(anInt);
      System.out.println(trace);
    }
    return anInt;
  }
  
  private int getSmallIntIntern(int pPosition) {
    int anInt = 0;
    if ((pPosition >= 0) && (pPosition + 1 < fByteArray.length)) {
      anInt = convertByteArrayToSmallInt(fByteArray, pPosition);
    }
    return anInt;
  }

  public String getString() {
    return getString(getPosition(), true);
  }

  public String getString(int pPosition) {
    return getString(pPosition, false);
  }

  private String getString(int pPosition, boolean pAdvancePosition) {
    String aString = null;
    int length = 0;
    if ((pPosition >= 0) && (pPosition < fByteArray.length)) {
      int stringEnd = pPosition;
      while (stringEnd < fByteArray.length) {
        if (fByteArray[stringEnd] == 0) {
          break;
        } else {
          stringEnd++;
        }
      }
      length = stringEnd - pPosition;
      try {
        aString = new String(fByteArray, pPosition, length, "UTF-8");
      } catch (UnsupportedEncodingException uee) {
        throw new FantasyFootballException(uee);
      }
    }
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> String ").append(aString);
      System.out.println(trace);
    }
    if (pAdvancePosition) {
      advancePosition(length + 1);
    }
    return aString;
  }

  public String[] getStringArray() {
    String[] aStringArray = getStringArray(getPosition());
    int length = 1;
    for (int i = 0; i < aStringArray.length; i++) {
      if (aStringArray[i] != null) {
        length += aStringArray[i].length() + 1;
      } else {
        length++;
      }
    }
    advancePosition(length);
    return aStringArray;
  }
  
  public String[] getStringArray(int pPosition) {
    String[] aStringArray = null;
    if ((pPosition >= 0) && (pPosition < fByteArray.length)) {
      int position = pPosition; 
      int length = getByteIntern(position++);
      if (_TRACE) {
        StringBuilder trace = new StringBuilder();
        trace.append(getPosition()).append("-> String[").append(length).append("] ");
        System.out.println(trace);
      }
      aStringArray = new String[length];
      for (int i = 0; i < length; i++) {
        aStringArray[i] = getString(position++);
        if (aStringArray[i] != null) {
          position += aStringArray[i].length();
        }
      }
    }
    return aStringArray;
  }
  
  public FieldCoordinate getFieldCoordinate() {
    FieldCoordinate fieldCoordinate = getFieldCoordinate(getPosition());
    advancePosition(2);
    return fieldCoordinate;
  }
  
  public FieldCoordinate getFieldCoordinate(int pPosition) {
    FieldCoordinate fieldCoordinate = null;
    int x = getByteIntern(pPosition);
    int y = getByteIntern(pPosition + 1);
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(getPosition()).append("-> FieldCoordinate ").append("(").append(x).append(",").append(y).append(")");
      System.out.println(trace);
    }
    if ((x != -1) || (y != -1)) {
      fieldCoordinate = new FieldCoordinate(x, y);
    }
    return fieldCoordinate;
  }
  
  public byte[] getByteArray() {
    byte[] aByteArray = getByteArray(getPosition());
    advancePosition(aByteArray.length + 1);
    return aByteArray;
  }
  
  public byte[] getByteArray(int pPosition) {
    byte[] aByteArray = new byte[0];
    if ((pPosition >= 0) && (pPosition < fByteArray.length)) {
      int length = getByteIntern(pPosition);
      if (_TRACE) {
        StringBuilder trace = new StringBuilder();
        trace.append(getPosition()).append("-> byte[").append(length).append("] ");
        System.out.println(trace);
      }
      aByteArray = new byte[length];
      for (int i = 0; i < length; i++) {
        aByteArray[i] = getByteIntern(pPosition + 1 + i);
      }
    }
    return aByteArray;
  }

  public int[] getByteArrayAsIntArray() {
    int[] anIntArray = getByteArrayAsIntArray(getPosition());
    advancePosition(anIntArray.length + 1);
    return anIntArray;
  }
  
  public int[] getByteArrayAsIntArray(int pPosition) {
    byte[] aByteArray = getByteArray(pPosition);
    int[] anIntArray = new int[aByteArray.length];
    for (int i = 0; i < aByteArray.length; i++) {
      anIntArray[i] = aByteArray[i];
    }
    return anIntArray;
  }
  
  public boolean[] getBooleanArray() {
    boolean[] aBooleanArray = getBooleanArray(getPosition());
    advancePosition(aBooleanArray.length + 1);
    return aBooleanArray;
  }
  
  public boolean[] getBooleanArray(int pPosition) {
    byte[] aByteArray = getByteArray(pPosition);
    boolean[] aBooleanArray = new boolean[aByteArray.length];
    for (int i = 0; i < aByteArray.length; i++) {
      aBooleanArray[i] = (aByteArray[i] > 0);
    }
    return aBooleanArray;
  }

  
  
  public int[] getSmallIntArrayAsIntArray() {
    int[] anIntArray = getSmallIntArrayAsIntArray(getPosition());
    advancePosition((anIntArray.length * 2) + 1);
    return anIntArray;
  }
  
  public int[] getSmallIntArrayAsIntArray(int pPosition) {
    int[] anIntArray = new int[0];
    if ((pPosition >= 0) && (pPosition < fByteArray.length)) {
      int length = getByteIntern(pPosition);
      if (_TRACE) {
        StringBuilder trace = new StringBuilder();
        trace.append(getPosition()).append("-> smallInt[").append(length).append("] ");
        System.out.println(trace);
      }
      anIntArray = new int[length];
      for (int i = 0; i < length; i++) {
        anIntArray[i] = getSmallIntIntern(pPosition + 1 + (i * 2));
      }
    }
    return anIntArray;
  }
  
  public int size() {
    return fByteArray.length;
  }
  
  public byte[] toBytes() {
    return fByteArray;
  }
  
  public static long convertByteArrayToLong(byte[] pByteArray, int pPosition) {
    return (
      (long) (pByteArray[pPosition] << 56)
      + ((long) (pByteArray[pPosition + 1] & 0xFF) << 48)
      + ((long) (pByteArray[pPosition + 2] & 0xFF) << 40)
      + ((long) (pByteArray[pPosition + 3] & 0xFF) << 32)
      + ((long) (pByteArray[pPosition + 4] & 0xFF) << 24)
      + ((long) (pByteArray[pPosition + 5] & 0xFF) << 16)
      + ((long) (pByteArray[pPosition + 6] & 0xFF) << 8)
      + (long) (pByteArray[pPosition + 7] & 0xFF)
    );   
  }

  public static int convertByteArrayToInt(byte[] pByteArray, int pPosition) {
    return (
      (pByteArray[pPosition] << 24)
      + ((pByteArray[pPosition + 1] & 0xFF) << 16)
      + ((pByteArray[pPosition + 2] & 0xFF) << 8)
      + (pByteArray[pPosition + 3] & 0xFF)
    );   
  }
  
  public static int convertByteArrayToSmallInt(byte[] pByteArray, int pPosition) {
    return (
      (pByteArray[pPosition] << 8)
      + (pByteArray[pPosition + 1] & 0xFF)
    );   
  }

  private void advancePosition(int pSteps) {
    fPosition = fPosition + pSteps;
    if (fPosition > fByteArray.length) {
      fPosition = fByteArray.length;
    }
  }
  
  public void setPosition(int pPosition) {
    fPosition = pPosition;
  }
  
  public int getPosition() {
    return fPosition;
  }
  
}
