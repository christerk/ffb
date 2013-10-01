package com.balancedbytes.games.ffb.net;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class NetCommandLog implements IXmlWriteable {
  
  public static final String XML_TAG = "commandLog";
  
  private List<NetCommand> fCommands;

  public NetCommandLog() {
    fCommands = new ArrayList<NetCommand>();
  }
  
  public void add(NetCommand pNetCommand) {
    fCommands.add(pNetCommand);
  }
  
  public NetCommand[] getCommands() {
    return fCommands.toArray(new NetCommand[fCommands.size()]);
  }
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, XML_TAG);
    NetCommand[] commands = getCommands();
    for (NetCommand command : commands) {
      command.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

}
