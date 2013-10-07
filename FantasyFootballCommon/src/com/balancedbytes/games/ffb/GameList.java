package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
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
public class GameList implements IXmlSerializable, IByteArraySerializable {
    
  // <gameList>
  //   <game id="4765261" started="2009-05-05T11:50:20.345">
  //     <homeTeam id="4326809" name="Kalimars Elves" coach="Kalimar" />
  //     <awayTeam id="5424335" name="BattleLores Orcs" coach="BattleLore" />
  //   </game>
  // </gameList>

  public static final String XML_TAG = "gameList";

  private List<GameListEntry> fEntries;
  
  public GameList() {
    fEntries = new ArrayList<GameListEntry>();
  }
  
  public GameList(GameListEntry[] pGameListEntries) {
    this();
    add(pGameListEntries);
  }

  public GameListEntry[] getEntries() {
    return fEntries.toArray(new GameListEntry[fEntries.size()]);
  }
  
  public GameListEntry[] getEntries(boolean onlyActive) {
	  LinkedList<GameListEntry> list = new LinkedList<GameListEntry>();
	  for (GameListEntry entry : fEntries) {
		  if (!onlyActive || entry.getStarted() != null)
			  list.add(entry);
	  }
	  GameListEntry[] result = list.toArray(new GameListEntry[list.size()]);
	  Arrays.sort(result, new Comparator<GameListEntry>() {
	    public int compare(GameListEntry pEntry1, GameListEntry pEntry2) {
	      return pEntry2.getStarted().compareTo(pEntry1.getStarted());
	    }
	  });
	  return result;
  }
  
  public void add(GameListEntry pGameListEntry) {
    if (pGameListEntry != null) {
      fEntries.add(pGameListEntry);
    }
  }
  
  private void add(GameListEntry[] pGameListEntries) {
    if (ArrayTool.isProvided(pGameListEntries)) {
      for (GameListEntry gameListEntry : pGameListEntries) {
        add(gameListEntry);
      }
    }
  }
  
  public int size() {
    return fEntries.size();
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	UtilXml.startElement(pHandler, XML_TAG);

    GameListEntry[] entries = getEntries();
    for (GameListEntry gameListEntry : entries) {
    	gameListEntry.addToXml(pHandler);
    }

  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
  	IXmlReadable xmlElement = this;
    if (GameListEntry.XML_TAG.equals(pXmlTag)) {
      GameListEntry gameListEntry = new GameListEntry();
      gameListEntry.startXmlElement(pXmlTag, pXmlAttributes);
      add(gameListEntry);
      xmlElement = gameListEntry;
    }
    return xmlElement;
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
    GameListEntry[] gameListEntries = getEntries();
    pByteList.addByte((byte) gameListEntries.length);
    for (GameListEntry gameListEntry : gameListEntries) {
      gameListEntry.addTo(pByteList);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfGameListEntries = pByteArray.getByte();
    for (int i = 0; i < nrOfGameListEntries; i++) {
      GameListEntry gameListEntry = new GameListEntry();
      gameListEntry.initFrom(pByteArray);
      add(gameListEntry);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray gameListArray = new JsonArray();
    GameListEntry[] gameListEntries = getEntries();
    for (GameListEntry gameListEntry : gameListEntries) {
      gameListArray.add(gameListEntry.toJsonValue());
    }
    IJsonOption.GAME_LIST.addTo(jsonObject, gameListArray);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    JsonArray gameListArray = IJsonOption.GAME_LIST.getFrom(jsonObject);
    for (int i = 0; i < gameListArray.size(); i++) {
      GameListEntry gameListEntry = new GameListEntry();
      gameListEntry.initFrom(gameListArray.get(i));
      add(gameListEntry);
    }
  }

}
