package com.balancedbytes.games.ffb.bytearray;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;

/**
 * 
 * @author Kalimar
 */
public class ByteList {
  
  private static final boolean _TRACE = false;
  
  private List<Byte> fByteList;
  
  public ByteList() {
    fByteList = new LinkedList<Byte>();    
  }
  
  public void clear() {
    fByteList.clear();
  }
  
  public void addByte(byte pByte) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- byte ").append(pByte);
      System.out.println(trace);
    }
    fByteList.add(pByte);
  }
  
  public void addBoolean(Boolean pBoolean) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- Boolean ").append(pBoolean);
      System.out.println(trace);
    }
    if (pBoolean != null) {
      if (pBoolean) {
        fByteList.add((byte) 1);
      } else {
        fByteList.add((byte) -1);
      }
    } else {
      fByteList.add((byte) 0);
    }
  }

  public void addLong(long pLong) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- long ").append(pLong);
      System.out.println(trace);
    }
    byte[] bytes = convertLongToByteArray(pLong);
    for (int i = 0; i < bytes.length; i++) {
      fByteList.add(bytes[i]);
    }
  }

  public void addInt(int pInt) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- int ").append(pInt);
      System.out.println(trace);
    }
    byte[] bytes = convertIntToByteArray(pInt);
    for (int i = 0; i < bytes.length; i++) {
      fByteList.add(bytes[i]);
    }
  }
  
  public void addSmallInt(int pInt) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- smallInt ").append(pInt);
      System.out.println(trace);
    }
    byte[] bytes = convertSmallIntToByteArray(pInt);
    for (int i = 0; i < bytes.length; i++) {
      fByteList.add(bytes[i]);
    }
  }
  
  public void addString(String pString) {
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- String ").append(pString);
      System.out.println(trace);
    }
    if (pString != null) {
      try {
        byte[] stringBytes = pString.getBytes("UTF-8");
        for (int i = 0; i < stringBytes.length; i++) {
          fByteList.add(stringBytes[i]);
        }
      } catch (UnsupportedEncodingException uee) {
        throw new FantasyFootballException(uee);
      }
    }
    fByteList.add((byte) 0);
  }
  
  public void addStringArray(String[] pStringArray) {
    int length = (pStringArray != null) ? pStringArray.length : 0;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- String[").append(length).append("]");
      System.out.println(trace);
    }
    fByteList.add((byte) length);
    for (int i = 0; i < length; i++) {
      addString(pStringArray[i]);
    }
  }
  
  public void addByteArray(byte[] pByteArray) {
    int length = (pByteArray != null) ? pByteArray.length : 0;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- byte[").append(length).append("]");
      System.out.println(trace);
    }
    addByte((byte) length);
    for (int i = 0; i < length; i++) {
      addByte(pByteArray[i]);
    }
  }

  public void addByteArray(int[] pIntArray) {
    int length = (pIntArray != null) ? pIntArray.length : 0;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- byte[").append(length).append("]");
      System.out.println(trace);
    }
    addByte((byte) length);
    for (int i = 0; i < length; i++) {
      addByte((byte) pIntArray[i]);
    }
  }
  
  public void addBooleanArray(boolean[] pBooleanArray) {
    int length = (pBooleanArray != null) ? pBooleanArray.length : 0;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- boolean[").append(length).append("]");
      System.out.println(trace);
    }
    addByte((byte) length);
    for (int i = 0; i < length; i++) {
      addBoolean(pBooleanArray[i]);
    }
  }
  
  public void addSmallIntArray(int[] pIntArray) {
    int length = (pIntArray != null) ? pIntArray.length : 0;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- smallInt[").append(length).append("]");
      System.out.println(trace);
    }
    addByte((byte) length);
    for (int i = 0; i < length; i++) {
      addSmallInt(pIntArray[i]);
    }
  }

  public void addFieldCoordinate(FieldCoordinate pFieldCoordinate) {
    int x = (pFieldCoordinate != null) ? pFieldCoordinate.getX() : -1;
    int y = (pFieldCoordinate != null) ? pFieldCoordinate.getY() : -1;
    if (_TRACE) {
      StringBuilder trace = new StringBuilder();
      trace.append(fByteList.size()).append("<- FieldCoordinate (").append(x).append(",").append(y).append(")");
      System.out.println(trace);
    }
    fByteList.add((byte) x);
    fByteList.add((byte) y);
  }
  
  public byte[] toBytes() {
    byte[] bytes = new byte[fByteList.size()];
    int pos = 0;
    Iterator<Byte> byteIterator = fByteList.iterator();
    while (byteIterator.hasNext()) {
      bytes[pos++] = byteIterator.next();
    }
    return bytes;
  }

  public static byte[] convertLongToByteArray(long pLong) {
    return new byte[] {
      (byte) (pLong >>> 56),
      (byte) (pLong >>> 48),
      (byte) (pLong >>> 40),
      (byte) (pLong >>> 32),
      (byte) (pLong >>> 24),
      (byte) (pLong >>> 16),
      (byte) (pLong >>> 8),
      (byte) pLong
    };
  }
  
  public static byte[] convertIntToByteArray(int pInt) {
    return new byte[] {
      (byte) (pInt >>> 24),
      (byte) (pInt >>> 16),
      (byte) (pInt >>> 8),
      (byte) pInt
    };
  }
  
  public static byte[] convertSmallIntToByteArray(int pInt) {
    return new byte[] {
      (byte) (pInt >>> 8),
      (byte) pInt
    };
  }

  public int size() {
    return fByteList.size();
  }

}
