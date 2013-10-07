package com.balancedbytes.games.ffb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class TeamSetup implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "teamSetup";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_NR = "nr";
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";

  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_TAG_COORDINATE = "coordinate";
  
  private String fName;
  private String fTeamId;
  private Map<Integer, FieldCoordinate> fCoordinateByPlayerNr;
  
  private transient int fCurrentPlayerNr;
  
  public TeamSetup() {
    fCoordinateByPlayerNr = new HashMap<Integer, FieldCoordinate>();
  }
  
  public int[] getPlayerNumbers() {
    Integer[] playerNumberIntegers = fCoordinateByPlayerNr.keySet().toArray(new Integer[fCoordinateByPlayerNr.size()]);
    int[] playerNumbers = new int[playerNumberIntegers.length];
    for (int i = 0; i < playerNumbers.length; i++) {
      playerNumbers[i] = playerNumberIntegers[i].intValue();
    }
    return playerNumbers;
  }
  
  public FieldCoordinate[] getCoordinates() {
    int[] playerNumbers = getPlayerNumbers();
    FieldCoordinate[] coordinates = new FieldCoordinate[playerNumbers.length];
    for (int i = 0; i < coordinates.length; i++) {
      coordinates[i] = getCoordinate(playerNumbers[i]);
    }
    return coordinates;
  }
  
  public void addCoordinate(FieldCoordinate pCoordinate, int pPlayerNr) {
    fCoordinateByPlayerNr.put(pPlayerNr, pCoordinate);
  }
  
  public FieldCoordinate getCoordinate(int pPlayerNr) {
    return fCoordinateByPlayerNr.get(pPlayerNr);
  }
  
  public void setName(String pName) {
    fName = pName;
  }
  
  public String getName() {
    return fName;
  }
  
  public void setTeamId(String pTeamId) {
    fTeamId = pTeamId;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public TeamSetup transform() {
    TeamSetup transformedSetup = new TeamSetup();
    transformedSetup.setName(getName());
    transformedSetup.setTeamId(getTeamId());
    int[] playerNumbers = getPlayerNumbers();
    FieldCoordinate[] coordinates = getCoordinates();
    for (int i = 0; i < playerNumbers.length; i++) {
      transformedSetup.addCoordinate(coordinates[i], playerNumbers[i]);
    }
    return transformedSetup;
  }
  
  public void applyTo(Game pGame) {
    boolean homeSetup = getTeamId().equals(pGame.getTeamHome().getId());
    Team team = homeSetup ? pGame.getTeamHome() : pGame.getTeamAway();
    for (Player player : team.getPlayers()) {
      FieldCoordinate playerCoordinate = getCoordinate(player.getNr());
      PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
      if (playerState.canBeSetUp()) {
        if (playerCoordinate != null) {
          pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.STANDING).changeActive(true));
          if (homeSetup) {
            pGame.getFieldModel().setPlayerCoordinate(player, playerCoordinate);
          } else {
            pGame.getFieldModel().setPlayerCoordinate(player, playerCoordinate.transform());
          }
        } else {
          pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
          UtilBox.putPlayerIntoBox(pGame, player);
        }
      }
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, getName());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	
    int[] playerNumbers = getPlayerNumbers();
    FieldCoordinate[] coordinates = getCoordinates();
    for (int i = 0; i < playerNumbers.length; i++) {

    	attributes = new AttributesImpl();
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR, playerNumbers[i]);
    	UtilXml.startElement(pHandler, _XML_TAG_PLAYER, attributes);

  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, coordinates[i].getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, coordinates[i].getY());
  		UtilXml.startElement(pHandler, _XML_TAG_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_COORDINATE);

  		UtilXml.endElement(pHandler, _XML_TAG_PLAYER);

    }
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      setName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME));
      setTeamId(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_TEAM_ID));
    }
    if (_XML_TAG_PLAYER.equals(pXmlTag)) {
      fCurrentPlayerNr = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NR);
    }
    if (_XML_TAG_COORDINATE.equals(pXmlTag)) {
      int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
      int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
      FieldCoordinate coordinate = new FieldCoordinate(x, y);
      addCoordinate(coordinate, fCurrentPlayerNr);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getName());
    pByteList.addString(getTeamId());
    pByteList.addByteArray(getPlayerNumbers());
    FieldCoordinate[] coordinates = getCoordinates();
    if (ArrayTool.isProvided(coordinates)) {
      pByteList.addByte((byte) coordinates.length);
      for (FieldCoordinate coordinate : coordinates) {
        pByteList.addFieldCoordinate(coordinate);
      }
    } else {
      pByteList.addByte((byte) 0);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fName = pByteArray.getString();
    fTeamId = pByteArray.getString();
    int[] playerNumbers = pByteArray.getByteArrayAsIntArray();
    int nrOfCoordinates = pByteArray.getByte();
    for (int i = 0; i < nrOfCoordinates; i++) {
      addCoordinate(pByteArray.getFieldCoordinate(), playerNumbers[i]);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NAME.addTo(jsonObject, fName);
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    // build array of inner jsonObjects with playerNr + coordinate
    JsonArray playerPositions = new JsonArray();
    int[] playerNumbers = getPlayerNumbers();
    FieldCoordinate[] coordinates = getCoordinates();
    for (int i = 0; i < playerNumbers.length; i++) {
      JsonObject playerPosition = new JsonObject();
      IJsonOption.PLAYER_NR.addTo(playerPosition, playerNumbers[i]);
      IJsonOption.COORDINATE.addTo(playerPosition, coordinates[i]);
      playerPositions.add(playerPosition);
    }
    IJsonOption.PLAYER_POSITIONS.addTo(jsonObject, playerPositions);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fName = IJsonOption.NAME.getFrom(jsonObject);
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    // get coordinates and playerNrs from array of inner jsonObjects
    JsonArray playerPositions = IJsonOption.PLAYER_POSITIONS.getFrom(jsonObject);
    for (int i = 0; i < playerPositions.size(); i++) {
      JsonObject playerPosition = playerPositions.get(i).asObject();
      addCoordinate(
        IJsonOption.COORDINATE.getFrom(playerPosition),
        IJsonOption.PLAYER_NR.getFrom(playerPosition)
      );
    }
  }

}
