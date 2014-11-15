package com.balancedbytes.games.ffb.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Team implements IXmlSerializable, IByteArrayReadable, IJsonSerializable {

  public static final String XML_TAG = "team";

  private static final String _XML_ATTRIBUTE_ID = "id";

  private static final String _XML_TAG_NAME = "name";
  private static final String _XML_TAG_RACE = "race";
  private static final String _XML_TAG_ROSTER_ID = "rosterId";
  private static final String _XML_TAG_RE_ROLLS = "reRolls";
  private static final String _XML_TAG_APOTHECARIES = "apothecaries";
  private static final String _XML_TAG_CHEERLEADERS = "cheerleaders";
  private static final String _XML_TAG_ASSISTANT_COACHES = "assistantCoaches";
  private static final String _XML_TAG_COACH = "coach";
  private static final String _XML_TAG_FAN_FACTOR = "fanFactor";
  private static final String _XML_TAG_TEAM_VALUE = "teamValue";
  private static final String _XML_TAG_DIVISION = "division";
  private static final String _XML_TAG_TREASURY = "treasury";
  private static final String _XML_TAG_BASE_ICON_PATH = "baseIconPath";
  private static final String _XML_TAG_LOGO_URL = "logo";

  private String fId;
  private String fName;
  private String fRace;
  private int fReRolls;
  private int fApothecaries;
  private int fCheerleaders;
  private int fAssistantCoaches;
  private String fCoach;
  private int fFanFactor;
  private int fTeamValue;
  private String fDivision;
  private int fTreasury;
  private String fBaseIconPath;
  private String fLogoUrl;

  private String fRosterId;
  private Roster fRoster;

  private transient Map<String, Player> fPlayerById;
  private transient Map<Integer, Player> fPlayerByNr;

  private class PlayerComparatorByNr implements Comparator<Player> {
    public int compare(Player pPlayer1, Player pPlayer2) {
      return (pPlayer1.getNr() - pPlayer2.getNr());
    }
  }

  public Team() {
    fPlayerById = new HashMap<String, Player>();
    fPlayerByNr = new HashMap<Integer, Player>();
    updateRoster(new Roster());
  }
  
  public void setId(String pId) {
    fId = pId;
  }

  public String getRosterId() {
    return fRosterId;
  }

  public void setRosterId(String pRosterId) {
    fRosterId = pRosterId;
  }

  public void setRace(String pRace) {
    fRace = pRace;
  }

  public String getRace() {
    return fRace;
  }

  public int getFanFactor() {
    return fFanFactor;
  }

  public void setFanFactor(int fanFactor) {
    fFanFactor = fanFactor;
  }

  public String getId() {
    return fId;
  }

  public void addPlayer(Player pPlayer) {
    fPlayerByNr.put(pPlayer.getNr(), pPlayer);
    if (pPlayer.getId() != null) {
      fPlayerById.put(pPlayer.getId(), pPlayer);
    }
    pPlayer.setTeam(this);
  }
  
  public void removePlayer(Player pPlayer) {
    fPlayerByNr.remove(pPlayer.getNr());
    fPlayerById.remove(pPlayer.getId());
    pPlayer.setTeam(null);
  }

  public Player getPlayerById(String pId) {
    return fPlayerById.get(pId);
  }

  public Player getPlayerByNr(int pNr) {
    return fPlayerByNr.get(pNr);
  }

  /**
   * @return array of playes sorted by playerNr
   */
  public Player[] getPlayers() {
    Player[] players = fPlayerByNr.values().toArray(new Player[fPlayerByNr.size()]);
    Arrays.sort(players, new PlayerComparatorByNr());
    return players;
  }
  
  public int getMaxPlayerNr() {
    int maxPlayerNr = 0;
    for (Player player : getPlayers()) {
      if (player.getNr() > maxPlayerNr) {
        maxPlayerNr = player.getNr();
      }
    }
    return maxPlayerNr;
  }
  
  public int getNrOfAvailablePlayersInPosition(RosterPosition pos) {
	    int nrOfPlayersInPosition = 0;
	    for (Player player : getPlayers()) {
	      if ((player.getPosition() == pos) && (player.getRecoveringInjury() == null)) {
	    	  nrOfPlayersInPosition++;
	      }
	    }
	    return nrOfPlayersInPosition;	  
  }
  
  public int getNrOfRegularPlayers() {
    int nrOfRegularPlayers = 0;
    for (Player player : getPlayers()) {
      if ((player.getPlayerType() != PlayerType.JOURNEYMAN) && (player.getPlayerType() != PlayerType.STAR)) {
        nrOfRegularPlayers++;
      }
    }
    return nrOfRegularPlayers;
  }
  
  public int getNrOfAvailablePlayers() {
	    int nrOfAvailablePlayers = 0;
	    for (Player player : getPlayers()) {
	      if (player.getRecoveringInjury() == null) {
	    	  nrOfAvailablePlayers++;
	      }
	    }
	    return nrOfAvailablePlayers;
	  }

  public boolean hasPlayer(Player pPlayer) {
    return ((pPlayer != null) && (getPlayerById(pPlayer.getId()) != null));
  }

  public String getName() {
    return fName;
  }

  public void setName(String pName) {
    fName = pName;
  }

  public int getReRolls() {
    return fReRolls;
  }

  public int getApothecaries() {
    return fApothecaries;
  }

  public void setApothecaries(int pApothecaries) {
    fApothecaries = pApothecaries;
  }

  public void setReRolls(int pReRolls) {
    fReRolls = pReRolls;
  }

  public Roster getRoster() {
    return fRoster;
  }

  public void updateRoster(Roster pRoster) {
    fRoster = pRoster;
    if (fRoster != null) {
      setRosterId(fRoster.getId());
      setRace(fRoster.getName());
      for (Player player : getPlayers()) {
        String positionId = player.getPositionId();
        player.updatePosition(fRoster.getPositionById(positionId));
      }
    }
  }

  public int getCheerleaders() {
    return fCheerleaders;
  }

  public void setCheerleaders(int cheerleaders) {
    fCheerleaders = cheerleaders;
  }

  public int getAssistantCoaches() {
    return fAssistantCoaches;
  }

  public void setAssistantCoaches(int assistantCoaches) {
    fAssistantCoaches = assistantCoaches;
  }

  public String getCoach() {
    return fCoach;
  }

  public int getTeamValue() {
    return fTeamValue;
  }

  public void setTeamValue(int pTeamValue) {
    fTeamValue = pTeamValue;
  }

  public String getDivision() {
    return fDivision;
  }

  public int getTreasury() {
    return fTreasury;
  }

  public void setTreasury(int pTreasury) {
    fTreasury = pTreasury;
  }

  public String getBaseIconPath() {
    return fBaseIconPath;
  }

  public void setBaseIconPath(String pBaseIconPath) {
    fBaseIconPath = pBaseIconPath;
  }

  public void setLogoUrl(String pTeamLogoUrl) {
    fLogoUrl = pTeamLogoUrl;
  }

  public String getLogoUrl() {
    return fLogoUrl;
  }


  public void setDivision(String division) {
    fDivision = division;
  }

  public void setCoach(String coach) {
    fCoach = coach;
  }

  public static Comparator<Team> comparatorByName() {
    return new Comparator<Team>() {
      public int compare(Team pTeam1, Team pTeam2) {
        return pTeam1.getName().compareTo(pTeam2.getName());
      }
    };
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);

  	UtilXml.addValueElement(pHandler, _XML_TAG_COACH, getCoach());
  	UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
  	UtilXml.addValueElement(pHandler, _XML_TAG_RACE, getRace());
  	UtilXml.addValueElement(pHandler, _XML_TAG_ROSTER_ID, getRosterId());
  	UtilXml.addValueElement(pHandler, _XML_TAG_RE_ROLLS, getReRolls());
  	UtilXml.addValueElement(pHandler, _XML_TAG_FAN_FACTOR, getFanFactor());
  	UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, getTeamValue());
  	UtilXml.addValueElement(pHandler, _XML_TAG_APOTHECARIES, getApothecaries());
  	UtilXml.addValueElement(pHandler, _XML_TAG_CHEERLEADERS, getCheerleaders());
  	UtilXml.addValueElement(pHandler, _XML_TAG_ASSISTANT_COACHES, getAssistantCoaches());
  	UtilXml.addValueElement(pHandler, _XML_TAG_DIVISION, getDivision());
  	UtilXml.addValueElement(pHandler, _XML_TAG_TREASURY, getTreasury());
  	UtilXml.addValueElement(pHandler, _XML_TAG_BASE_ICON_PATH, getBaseIconPath());
  	UtilXml.addValueElement(pHandler, _XML_TAG_LOGO_URL, getLogoUrl());
  	
    for (Player player : getPlayers()) {
    	player.addToXml(pHandler);
    }
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (XML_TAG.equals(pXmlTag)) {
      setId(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID));
    }
    if (Player.XML_TAG.equals(pXmlTag)) {
      Player player = new Player();
      player.startXmlElement(pXmlTag, pXmlAttributes);
      addPlayer(player);
      xmlElement = player;
    }
    return xmlElement;
  }

  public boolean endXmlElement(String pXmlTag, String pValue) {
    boolean complete = XML_TAG.equals(pXmlTag);
    if (!complete) {
      if (_XML_TAG_NAME.equals(pXmlTag)) {
        fName = pValue;
      }
      if (_XML_TAG_COACH.equals(pXmlTag)) {
        setCoach(pValue);
      }
      if (_XML_TAG_RACE.equals(pXmlTag)) {
        setRace(pValue);
      }
      if (_XML_TAG_ROSTER_ID.equals(pXmlTag)) {
        setRosterId(pValue);
      }
      if (_XML_TAG_RE_ROLLS.equals(pXmlTag)) {
        setReRolls(Integer.parseInt(pValue));
      }
      if (_XML_TAG_FAN_FACTOR.equals(pXmlTag)) {
        setFanFactor(Integer.parseInt(pValue));
      }
      if (_XML_TAG_APOTHECARIES.equals(pXmlTag)) {
        setApothecaries(Integer.parseInt(pValue));
      }
      if (_XML_TAG_CHEERLEADERS.equals(pXmlTag)) {
        setCheerleaders(Integer.parseInt(pValue));
      }
      if (_XML_TAG_ASSISTANT_COACHES.equals(pXmlTag)) {
        setAssistantCoaches(Integer.parseInt(pValue));
      }
      if (_XML_TAG_TEAM_VALUE.equals(pXmlTag)) {
        setTeamValue(Integer.parseInt(pValue));
      }
      if (_XML_TAG_DIVISION.equals(pXmlTag)) {
        setDivision(pValue);
      }
      if (_XML_TAG_TREASURY.equals(pXmlTag)) {
        setTreasury(Integer.parseInt(pValue));
      }
      if (_XML_TAG_BASE_ICON_PATH.equals(pXmlTag)) {
        setBaseIconPath(pValue);
      }
      if (_XML_TAG_LOGO_URL.equals(pXmlTag)) {
        setLogoUrl(pValue);
      }
    }
    return complete;
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 0;  // TODO implement serialization info for this object
  }
    
  public void addTo(ByteList pByteList) {

    getRoster().addTo(pByteList);

    pByteList.addString(getId());
    pByteList.addString(getName());
    pByteList.addString(getCoach());
    pByteList.addString(getRace());
    pByteList.addByte((byte) getReRolls());
    pByteList.addByte((byte) getApothecaries());
    pByteList.addByte((byte) getCheerleaders());
    pByteList.addByte((byte) getAssistantCoaches());
    pByteList.addByte((byte) getFanFactor());
    pByteList.addInt(getTeamValue());
    pByteList.addInt(getTreasury());
    pByteList.addString(getBaseIconPath());
    pByteList.addString(getLogoUrl());

    Player[] players = getPlayers();
    if (ArrayTool.isProvided(players)) {
      pByteList.addByte((byte) players.length);
      for (Player player : players) {
        player.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }

  }

  public int initFrom(ByteArray pByteArray) {

    Roster roster = new Roster();
    roster.initFrom(pByteArray);

    setId(pByteArray.getString());
    setName(pByteArray.getString());
    setCoach(pByteArray.getString());
    setRace(pByteArray.getString());
    setReRolls(pByteArray.getByte());
    setApothecaries(pByteArray.getByte());
    setCheerleaders(pByteArray.getByte());
    setAssistantCoaches(pByteArray.getByte());
    setFanFactor(pByteArray.getByte());
    setTeamValue(pByteArray.getInt());
    setTreasury(pByteArray.getInt());
    setBaseIconPath(pByteArray.getString());
    setLogoUrl(pByteArray.getString());

    int nrOfPlayers = pByteArray.getByte();
    
    for (int i = 0; i < nrOfPlayers; i++) {
      Player player = new Player();
      player.initFrom(pByteArray);
      addPlayer(player);
    }

    updateRoster(roster);
    
    // TODO implement serialization info for this object
    return 0;

  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {

    JsonObject jsonObject = new JsonObject();
    
    IJsonOption.TEAM_ID.addTo(jsonObject, fId);
    IJsonOption.TEAM_NAME.addTo(jsonObject, fName);
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.RACE.addTo(jsonObject, fRace);
    IJsonOption.RE_ROLLS.addTo(jsonObject, fReRolls);
    IJsonOption.APOTHECARIES.addTo(jsonObject, fApothecaries);
    IJsonOption.CHEERLEADERS.addTo(jsonObject, fCheerleaders);
    IJsonOption.ASSISTANT_COACHES.addTo(jsonObject, fAssistantCoaches);
    IJsonOption.FAN_FACTOR.addTo(jsonObject, fFanFactor);
    IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
    IJsonOption.TREASURY.addTo(jsonObject, fTreasury);
    IJsonOption.BASE_ICON_PATH.addTo(jsonObject, fBaseIconPath);
    IJsonOption.LOGO_URL.addTo(jsonObject, fLogoUrl);
    
    JsonArray playerArray = new JsonArray();
    for (Player player : getPlayers()) {
      playerArray.add(player.toJsonValue());
    }
    IJsonOption.PLAYER_ARRAY.addTo(jsonObject, playerArray);

    if (fRoster != null) {
      IJsonOption.ROSTER.addTo(jsonObject, fRoster.toJsonValue());
    }

    return jsonObject;
    
  }
  
  public Team initFrom(JsonValue pJsonValue) {
    
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    
    fId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fName = IJsonOption.TEAM_NAME.getFrom(jsonObject);
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fRace = IJsonOption.RACE.getFrom(jsonObject);
    fReRolls = IJsonOption.RE_ROLLS.getFrom(jsonObject);
    fApothecaries = IJsonOption.APOTHECARIES.getFrom(jsonObject);
    fCheerleaders = IJsonOption.CHEERLEADERS.getFrom(jsonObject);
    fAssistantCoaches = IJsonOption.ASSISTANT_COACHES.getFrom(jsonObject);
    fFanFactor = IJsonOption.FAN_FACTOR.getFrom(jsonObject);
    fTeamValue = IJsonOption.TEAM_VALUE.getFrom(jsonObject);
    fTreasury = IJsonOption.TREASURY.getFrom(jsonObject);
    fBaseIconPath = IJsonOption.BASE_ICON_PATH.getFrom(jsonObject);
    fLogoUrl = IJsonOption.LOGO_URL.getFrom(jsonObject);

    fPlayerById.clear();
    fPlayerByNr.clear();
    
    JsonArray playerArray = IJsonOption.PLAYER_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < playerArray.size(); i++) {
      addPlayer(new Player().initFrom(playerArray.get(i)));
    }

    Roster roster = null;
    JsonObject rosterObject = IJsonOption.ROSTER.getFrom(jsonObject);
    if (rosterObject != null) {
      roster = new Roster().initFrom(rosterObject);
    }
    updateRoster(roster);

    return this;
    
  }

}
