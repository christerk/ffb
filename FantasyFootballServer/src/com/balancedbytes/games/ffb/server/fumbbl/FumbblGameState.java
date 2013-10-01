package com.balancedbytes.games.ffb.server.fumbbl;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.GameOptions;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class FumbblGameState implements IXmlSerializable {
    
  public static final String XML_TAG = "gamestate";

  private static final String _XML_TAG_URL = "url";
  private static final String _XML_TAG_RESULT = "result";
  private static final String _XML_TAG_REASON = "reason";
  private static final String _XML_TAG_DESCRIPTION = "description";
  private static final String _XML_TAG_GAME_ID = "gameid";
  
  private String fUrl;
  private String fResult;
  private String fReason;
  private String fDescription;
  private String fGameId;
  private GameOptions fOptions;
  
  public FumbblGameState(String pUrl) {
    fUrl = pUrl;
    fOptions = new GameOptions(null);
  }
  
  public boolean isOk() {
    return "ok".equalsIgnoreCase(getResult());
  }
  
  public String getUrl() {
    return fUrl;
  }
  
  public String getReason() {
    return fReason;
  }
  
  public String getResult() {
    return fResult;
  }
  
  public String getDescription() {
    return fDescription;
  }
  
  public String getGameId() {
    return fGameId;
  }
  
  public GameOptions getOptions() {
    return fOptions;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    
    UtilXml.startElement(pHandler, XML_TAG);
    
    UtilXml.addValueElement(pHandler, _XML_TAG_URL, getUrl());
    UtilXml.addValueElement(pHandler, _XML_TAG_RESULT, getResult());
    UtilXml.addValueElement(pHandler, _XML_TAG_REASON, getReason());
    UtilXml.addValueElement(pHandler, _XML_TAG_DESCRIPTION, getDescription());
    UtilXml.addValueElement(pHandler, _XML_TAG_GAME_ID, getGameId());

    getOptions().addToXml(pHandler);
    
    UtilXml.endElement(pHandler, XML_TAG);
    
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (GameOptions.XML_TAG.equals(pXmlTag)) {
      getOptions().startXmlElement(pXmlTag, pXmlAttributes);
      xmlElement = getOptions();
    }
    return xmlElement;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    if (_XML_TAG_URL.equals(pXmlTag)) {
      fUrl = pValue;
    }
    if (_XML_TAG_RESULT.equals(pXmlTag)) {
      fResult = pValue;
    }
    if (_XML_TAG_REASON.equals(pXmlTag)) {
      fReason = pValue;
    }
    if (_XML_TAG_DESCRIPTION.equals(pXmlTag)) {
      fDescription = pValue;
    }
    if (_XML_TAG_GAME_ID.equals(pXmlTag)) {
      fGameId = pValue;
    }
    return XML_TAG.equals(pXmlTag);
  }

}
