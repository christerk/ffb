package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class TeamListEntry implements IXmlSerializable, IByteArraySerializable {
    
//  <teams coach="47257">
//    <team>
//      <id>492614</id>
//      <status>1</status>
//      <division>1</division>
//      <name>Bauernopfer</name>
//      <rating>147</rating>
//      <strength>152</strength>
//      <race>Elf</race>
//      <treasury>0</treasury>
//    </team>
//  </teams>

  public static final String XML_TAG = "team";
  
  private static final String _XML_TAG_ID = "id";
  private static final String _XML_TAG_STATUS = "status";
  private static final String _XML_TAG_DIVISION = "division";
  private static final String _XML_TAG_NAME = "name";
  private static final String _XML_TAG_TEAM_VALUE = "teamValue";
  private static final String _XML_TAG_RACE = "race";
  private static final String _XML_TAG_TREASURY = "treasury";
  
  private String fTeamId;
  private TeamStatus fTeamStatus;
  private String fDivision;
  private String fTeamName;
  private int fTeamValue;
  private String fRace;
  private int fTreasury;
  
  public TeamListEntry() {
    super();
  }
  
  public void init(Team pTeam) {
    if (pTeam != null) {
      setTeamId(pTeam.getId());
      setTeamName(pTeam.getName());
      setDivision(pTeam.getDivision());
      setRace(pTeam.getRace());
      setTeamValue(pTeam.getTeamValue());
      setTreasury(pTeam.getTreasury());
    }
  }

  public String getTeamId() {
    return fTeamId;
  }
  
  public void setTeamId(String pTeamId) {
    fTeamId = pTeamId;
  }
  
  public TeamStatus getTeamStatus() {
    return fTeamStatus;
  }
  
  public void setTeamStatus(TeamStatus pTeamStatus) {
    fTeamStatus = pTeamStatus;
  }
  
  public String getDivision() {
    return fDivision;
  }
  
  public void setDivision(String pDivision) {
    fDivision = pDivision;
  }
  
  public String getTeamName() {
    return fTeamName;
  }
  
  public void setTeamName(String pTeamName) {
    fTeamName = pTeamName;
  }
   
  public int getTeamValue() {
    return fTeamValue;
  }
  
  public void setTeamValue(int pTeamRating) {
    fTeamValue = pTeamRating;
  }
  
  public String getRace() {
    return fRace;
  }
  
  public void setRace(String pRace) {
    fRace = pRace;
  }
  
  public int getTreasury() {
    return fTreasury;
  }
  
  public void setTreasury(int pTreasury) {
    fTreasury = pTreasury;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	UtilXml.startElement(pHandler, XML_TAG);
  	
  	UtilXml.addValueElement(pHandler, _XML_TAG_ID, getTeamId());
  	UtilXml.addValueElement(pHandler, _XML_TAG_STATUS, (getTeamStatus() != null) ? Integer.toString(getTeamStatus().getId()) : null);
  	UtilXml.addValueElement(pHandler, _XML_TAG_DIVISION, getDivision());
  	UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getTeamName());
  	UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, getTeamValue());
  	UtilXml.addValueElement(pHandler, _XML_TAG_RACE, getRace());
  	UtilXml.addValueElement(pHandler, _XML_TAG_TREASURY, getTreasury());
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    if (_XML_TAG_ID.equals(pXmlTag)) {
      fTeamId = pValue;
    }
    if (_XML_TAG_STATUS.equals(pXmlTag)) {
      int teamStatusId = Integer.parseInt(pValue);
      fTeamStatus = new TeamStatusFactory().forId(teamStatusId);
    }
    if (_XML_TAG_DIVISION.equals(pXmlTag)) {
      fDivision = pValue;
    }
    if (_XML_TAG_NAME.equals(pXmlTag)) {
      fTeamName = pValue;
    }
    if (_XML_TAG_TEAM_VALUE.equals(pXmlTag)) {
      fTeamValue = Integer.parseInt(pValue);
    }
    if (_XML_TAG_RACE.equals(pXmlTag)) {
      fRace = pValue;
    }
    if (_XML_TAG_TREASURY.equals(pXmlTag)) {
      fTreasury = Integer.parseInt(pValue);
    }
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) ((getTeamStatus() != null) ? getTeamStatus().getId() : 0));
    pByteList.addString(getDivision());
    pByteList.addString(getTeamName());
    pByteList.addInt(getTeamValue());
    pByteList.addString(getRace());
    pByteList.addInt(getTreasury());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fTeamStatus = new TeamStatusFactory().forId(pByteArray.getByte());
    fDivision = pByteArray.getString();
    fTeamName = pByteArray.getString();
    fTeamValue = pByteArray.getInt();
    fRace = pByteArray.getString();
    fTreasury = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.TEAM_STATUS.addTo(jsonObject, fTeamStatus);
    IJsonOption.DIVISION.addTo(jsonObject, fDivision);
    IJsonOption.TEAM_NAME.addTo(jsonObject, fTeamName);
    IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
    IJsonOption.RACE.addTo(jsonObject, fRace);
    IJsonOption.TREASURY.addTo(jsonObject, fTreasury);
    return jsonObject;
  }
  
  public TeamListEntry initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fTeamStatus = (TeamStatus) IJsonOption.TEAM_STATUS.getFrom(jsonObject);
    fDivision = IJsonOption.DIVISION.getFrom(jsonObject);
    fTeamName = IJsonOption.TEAM_NAME.getFrom(jsonObject);
    fTeamValue = IJsonOption.TEAM_VALUE.getFrom(jsonObject);
    fRace = IJsonOption.RACE.getFrom(jsonObject);
    fTreasury = IJsonOption.TREASURY.getFrom(jsonObject);
    return this;
  }

}
