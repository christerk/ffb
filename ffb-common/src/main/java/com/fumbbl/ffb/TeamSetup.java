package com.fumbbl.ffb;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class TeamSetup implements IXmlSerializable, IJsonSerializable {

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
	private final Map<Integer, FieldCoordinate> fCoordinateByPlayerNr;

	private transient int fCurrentPlayerNr;

	public TeamSetup() {
		fCoordinateByPlayerNr = new HashMap<>();
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
		for (Player<?> player : team.getPlayers()) {
			FieldCoordinate playerCoordinate = getCoordinate(player.getNr());
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if (playerState.canBeSetUpNextDrive()) {
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

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
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

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
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

	public TeamSetup initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fName = IJsonOption.NAME.getFrom(source, jsonObject);
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		// get coordinates and playerNrs from array of inner jsonObjects
		JsonArray playerPositions = IJsonOption.PLAYER_POSITIONS.getFrom(source, jsonObject);
		for (int i = 0; i < playerPositions.size(); i++) {
			JsonObject playerPosition = playerPositions.get(i).asObject();
			addCoordinate(IJsonOption.COORDINATE.getFrom(source, playerPosition), IJsonOption.PLAYER_NR.getFrom(source, playerPosition));
		}
		return this;
	}

}
