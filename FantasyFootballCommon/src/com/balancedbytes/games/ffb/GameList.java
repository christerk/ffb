package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
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
public class GameList implements IXmlSerializable, IJsonSerializable {

	// <gameList>
	// <game id="4765261" started="2009-05-05T11:50:20.345">
	// <homeTeam id="4326809" name="Kalimars Elves" coach="Kalimar" />
	// <awayTeam id="5424335" name="BattleLores Orcs" coach="BattleLore" />
	// </game>
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

	public GameListEntry[] getEntriesSorted() {
		GameListEntry[] result = getEntries();
		Arrays.sort(result, new Comparator<GameListEntry>() {
			public int compare(GameListEntry pEntry1, GameListEntry pEntry2) {
				Date date1 = (pEntry1.getStarted() != null) ? pEntry1.getStarted() : new Date(0);
				Date date2 = (pEntry2.getStarted() != null) ? pEntry2.getStarted() : new Date(0);
				return date2.compareTo(date1);
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

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (GameListEntry.XML_TAG.equals(pXmlTag)) {
			GameListEntry gameListEntry = new GameListEntry();
			gameListEntry.startXmlElement(game, pXmlTag, pXmlAttributes);
			add(gameListEntry);
			xmlElement = gameListEntry;
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray gameListArray = new JsonArray();
		GameListEntry[] gameListEntries = getEntries();
		for (GameListEntry gameListEntry : gameListEntries) {
			gameListArray.add(gameListEntry.toJsonValue());
		}
		IJsonOption.GAME_LIST_ENTRIES.addTo(jsonObject, gameListArray);
		return jsonObject;
	}

	public GameList initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray gameListArray = IJsonOption.GAME_LIST_ENTRIES.getFrom(game, jsonObject);
		for (int i = 0; i < gameListArray.size(); i++) {
			GameListEntry gameListEntry = new GameListEntry();
			gameListEntry.initFrom(game, gameListArray.get(i));
			add(gameListEntry);
		}
		return this;
	}

}
