package com.balancedbytes.games.ffb.net.commands;

import java.nio.channels.SocketChannel;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class InternalCommandSocketClosed extends NetCommand {

  public InternalCommandSocketClosed() {
    super();
  }

  public InternalCommandSocketClosed(SocketChannel pSocketChannel) {
    setSender(pSocketChannel);
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SOCKET_CLOSED;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    UtilXml.addEmptyElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
  }

  public int initFrom(ByteArray pByteArray) {
    return pByteArray.getSmallInt();
  }

}
