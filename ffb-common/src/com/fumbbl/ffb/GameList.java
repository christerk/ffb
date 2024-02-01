package com.fumbbl.ffb;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

	private final List<GameListEntry> fEntries;

	public GameList() {
		fEntries = new ArrayList<>();
	}

	public GameListEntry[] getEntries() {
		return fEntries.toArray(new GameListEntry[0]);
	}

	public GameListEntry[] getEntriesSorted() {
		GameListEntry[] result = getEntries();
		Arrays.sort(result, (pEntry1, pEntry2) -> {
			Date date1 = (pEntry1.getStarted() != null) ? pEntry1.getStarted() : new Date(0);
			Date date2 = (pEntry2.getStarted() != null) ? pEntry2.getStarted() : new Date(0);
			return date2.compareTo(date1);
		});
		return result;
	}

	public void add(GameListEntry pGameListEntry) {
		if (pGameListEntry != null) {
			fEntries.add(pGameListEntry);
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

	public GameList initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray gameListArray = IJsonOption.GAME_LIST_ENTRIES.getFrom(source, jsonObject);
		for (int i = 0; i < gameListArray.size(); i++) {
			GameListEntry gameListEntry = new GameListEntry();
			gameListEntry.initFrom(source, gameListArray.get(i));
			add(gameListEntry);
		}
		return this;
	}

}
