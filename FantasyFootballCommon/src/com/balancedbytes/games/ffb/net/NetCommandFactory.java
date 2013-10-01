package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.bytearray.ByteArray;


/**
 * 
 * @author Kalimar
 */
public class NetCommandFactory {

  private static final NetCommandFactory _INSTANCE = new NetCommandFactory();

  public static NetCommandFactory getInstance() {
    return _INSTANCE;
  }

  private NetCommandFactory() {
    super();
  }

  public byte[] nextCommandBytes(byte[] pBytes, int pStart, int pEnd) {
    byte[] nextCommandBytes = null;
    int bytesLeft = pEnd - pStart;
    if (bytesLeft >= 3) {
      int nextCommandSize = ByteArray.convertByteArrayToSmallInt(pBytes, pStart + 2);
      if (bytesLeft >= nextCommandSize) {
        nextCommandBytes = new byte[nextCommandSize];
        System.arraycopy(pBytes, pStart, nextCommandBytes, 0, nextCommandSize);
      }
    }
    return nextCommandBytes;
  }
  
  public NetCommand fromBytes(byte[] pBytes) {
    NetCommand netCommand = null;
    if ((pBytes != null) && (pBytes.length > 1)) {
      NetCommandId netCommandId = NetCommandId.fromId(((pBytes[0] & 0xff) * 256) + (pBytes[1] & 0xff));
      if (netCommandId != null) {
        netCommand = netCommandId.createNetCommand();
        if (netCommand != null) {
          netCommand.initFrom(pBytes);
        }
      }
    }
    return netCommand;
  }

}
