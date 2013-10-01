package com.balancedbytes.games.ffb.server.admin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class AdminListEntry implements IXmlSerializable {
  
  public static final String XML_TAG = "game";

  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_STARTED = "started";
  private static final String _XML_ATTRIBUTE_FINISHED = "finished";
  private static final String _XML_ATTRIBUTE_STATUS = "status";

  private static final String _XML_TAG_TEAM = "team";
  private static final String _XML_ATTRIBUTE_HOME = "home";
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");  // 2001-07-04T12:08:56.235
  
  private long fGameId;
  
  private Date fStarted;
  private Date fFinished;
  private GameStatus fStatus;
  private String fTeamHomeId;
  private String fTeamHomeName;
  private String fTeamHomeCoach;
  private String fTeamAwayId;
  private String fTeamAwayName;
  private String fTeamAwayCoach;
  
  public AdminListEntry() {
    super();
  }
  
  public long getGameId() {
    return fGameId;
  }

  public void setGameId(long pGameId) {
    fGameId = pGameId;
  }

  public Date getStarted() {
    return fStarted;
  }

  public void setStarted(Date pStarted) {
    fStarted = pStarted;
  }
  
  public Date getFinished() {
    return fFinished;
  }
  
  public void setFinished(Date pFinished) {
    fFinished = pFinished;
  }
  
  public GameStatus getStatus() {
    return fStatus;
  }
  
  public void setStatus(GameStatus pStatus) {
    fStatus = pStatus;
  }

  public String getTeamHomeId() {
    return fTeamHomeId;
  }

  public void setTeamHomeId(String pTeamHomeId) {
    fTeamHomeId = pTeamHomeId;
  }

  public String getTeamHomeName() {
    return fTeamHomeName;
  }

  public void setTeamHomeName(String pTeamHomeName) {
    fTeamHomeName = pTeamHomeName;
  }

  public String getTeamHomeCoach() {
    return fTeamHomeCoach;
  }

  public void setTeamHomeCoach(String pTeamHomeCoach) {
    fTeamHomeCoach = pTeamHomeCoach;
  }

  public String getTeamAwayId() {
    return fTeamAwayId;
  }

  public void setTeamAwayId(String pTeamAwayId) {
    fTeamAwayId = pTeamAwayId;
  }

  public String getTeamAwayName() {
    return fTeamAwayName;
  }

  public void setTeamAwayName(String pTeamAwayName) {
    fTeamAwayName = pTeamAwayName;
  }

  public String getTeamAwayCoach() {
    return fTeamAwayCoach;
  }

  public void setTeamAwayCoach(String pTeamAwayCoach) {
    fTeamAwayCoach = pTeamAwayCoach;
  }
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getGameId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STARTED, (getStarted() != null) ? _TIMESTAMP_FORMAT.format(getStarted()) : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FINISHED, (getFinished() != null) ? _TIMESTAMP_FORMAT.format(getFinished()) : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATUS, (getStatus() != null) ? getStatus().getName() : null);
    UtilXml.startElement(pHandler, XML_TAG, attributes);

    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getTeamHomeId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME, true);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, getTeamHomeName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getTeamHomeCoach());
    UtilXml.addEmptyElement(pHandler, _XML_TAG_TEAM, attributes);

    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getTeamAwayId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME, false);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, getTeamAwayName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getTeamAwayCoach());
    UtilXml.addEmptyElement(pHandler, _XML_TAG_TEAM, attributes);
    
    UtilXml.endElement(pHandler, XML_TAG);

  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      fGameId = UtilXml.getLongAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
      fStatus = GameStatus.fromName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STATUS));
      String startedTimestamp = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STARTED);
      if (StringTool.isProvided(startedTimestamp)) {
        try {
          fStarted = _TIMESTAMP_FORMAT.parse(startedTimestamp);
        } catch (ParseException pe) {
          fStarted = null;
        }
      } else {
        fStarted = null;
      }
      String finishedTimestamp = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_FINISHED);
      if (StringTool.isProvided(finishedTimestamp)) {
        try {
          fFinished = _TIMESTAMP_FORMAT.parse(finishedTimestamp);
        } catch (ParseException pe) {
          fFinished = null;
        }
      } else {
        fFinished = null;
      }
    }
    if (_XML_TAG_TEAM.equals(pXmlTag)) {
      boolean homeTeam = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_HOME);
      if (homeTeam) {
        fTeamHomeId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
        fTeamHomeName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME);
        fTeamHomeCoach = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_COACH);
      } else {
        fTeamAwayId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
        fTeamAwayName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME);
        fTeamAwayCoach = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_COACH);
      }
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

}
