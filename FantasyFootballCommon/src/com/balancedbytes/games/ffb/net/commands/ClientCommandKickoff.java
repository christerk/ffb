package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandKickoff extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";

  private static final String _XML_TAG_BALL_COORDINATE = "ballCoordinate";
  
  private FieldCoordinate fBallCoordinate;
  
  public ClientCommandKickoff() {
    super();
  }

  public ClientCommandKickoff(FieldCoordinate pBallCoordinate) {
    fBallCoordinate = pBallCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_KICKOFF;
  }
  
  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	UtilXml.startElement(pHandler, getId().getName());
  	if (getBallCoordinate() != null) {
  		AttributesImpl attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBallCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBallCoordinate().getY());
  		UtilXml.addEmptyElement(pHandler, _XML_TAG_BALL_COORDINATE, attributes);
  	}
  	UtilXml.endElement(pHandler, getId().getName());
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
    pByteList.addFieldCoordinate(getBallCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fBallCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
    
}
