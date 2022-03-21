package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class Team implements IXmlSerializable, IJsonSerializable {

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
	private static final String _XML_TAG_TEAM_VALUE = "currentTeamValue";
	private static final String _XML_TAG_DIVISION = "division";
	private static final String _XML_TAG_TREASURY = "treasury";
	private static final String _XML_TAG_BASE_ICON_PATH = "baseIconPath";
	private static final String _XML_TAG_LOGO_URL = "logo";
	private static final String _XML_TAG_DEDICATED_FANS = "dedicatedFans";
	private static final String _XML_TAG_SPECIAL_RULES = "specialRule";
	private static final String _XML_TAG_RULE = "rule";

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
	private int dedicatedFans;
	private final Set<SpecialRule> specialRules;

	private String fRosterId;
	private Roster fRoster;

	private InducementSet fInducementSet;

	private final transient Map<String, Player<?>> fPlayerById;
	private final transient Map<Integer, Player<?>> fPlayerByNr;

	private transient long currentGameId;

	private static class PlayerComparatorByNr implements Comparator<Player<?>> {
		public int compare(Player<?> pPlayer1, Player<?> pPlayer2) {
			return (pPlayer1.getNr() - pPlayer2.getNr());
		}
	}

	public Team(IFactorySource game) {
		fPlayerById = new HashMap<>();
		fPlayerByNr = new HashMap<>();
		specialRules = new HashSet<>();
	}

	public long getCurrentGameId() {
		return currentGameId;
	}

	public void setCurrentGameId(long currentGameId) {
		this.currentGameId = currentGameId;
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

	public int getDedicatedFans() {
		return dedicatedFans;
	}

	public void setDedicatedFans(int dedicatedFans) {
		this.dedicatedFans = dedicatedFans;
	}

	public String getId() {
		return fId;
	}

	public Set<SpecialRule> getSpecialRules() {
		return specialRules;
	}

	public void addPlayer(Player<?> pPlayer) {
		fPlayerByNr.put(pPlayer.getNr(), pPlayer);
		if (pPlayer.getId() != null) {
			fPlayerById.put(pPlayer.getId(), pPlayer);
		}
		pPlayer.setTeam(this);
	}

	public void removePlayer(Player<?> pPlayer) {
		fPlayerByNr.remove(pPlayer.getNr());
		fPlayerById.remove(pPlayer.getId());
		pPlayer.setTeam(null);
	}

	public Player<?> getPlayerById(String pId) {
		return fPlayerById.get(pId);
	}

	public Player<?> getPlayerByNr(int pNr) {
		return fPlayerByNr.get(pNr);
	}

	/**
	 * @return array of playes sorted by playerNr
	 */
	public Player<?>[] getPlayers() {
		Player<?>[] players = fPlayerByNr.values().toArray(new Player[0]);
		Arrays.sort(players, new PlayerComparatorByNr());
		return players;
	}

	public int getMaxPlayerNr() {
		int maxPlayerNr = 0;
		for (Player<?> player : getPlayers()) {
			if (player.getNr() > maxPlayerNr) {
				maxPlayerNr = player.getNr();
			}
		}
		return maxPlayerNr;
	}

	public int getNrOfAvailablePlayersInPosition(RosterPosition pos) {
		int nrOfPlayersInPosition = 0;
		for (Player<?> player : getPlayers()) {
			if ((player.getPosition() == pos) && (player.getRecoveringInjury() == null)) {
				nrOfPlayersInPosition++;
			}
		}
		return nrOfPlayersInPosition;
	}

	public int getNrOfAvailablePlayers() {
		int nrOfAvailablePlayers = 0;
		for (Player<?> player : getPlayers()) {
			if (player.getRecoveringInjury() == null) {
				nrOfAvailablePlayers++;
			}
		}
		return nrOfAvailablePlayers;
	}

	public boolean hasPlayer(Player<?> pPlayer) {
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

	public void updateRoster(Roster pRoster, IFactorySource game) {
		updateRoster(pRoster, true, game);
	}

	public void updateRoster(Roster pRoster, boolean updateStats, IFactorySource game) {
		game.logDebug(currentGameId, "Entering updateRoster");
		fRoster = pRoster;
		if (fRoster != null) {
			setRosterId(fRoster.getId());
			setRace(fRoster.getName());
			for (Player<?> player : getPlayers()) {
				String positionId = player.getPositionId();
				player.updatePosition(fRoster.getPositionById(positionId), updateStats, game, currentGameId);
			}
		}
		game.logDebug(currentGameId, "Leaving updateRoster");
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

	public InducementSet getInducementSet() {
		return fInducementSet;
	}

	public void setInducementSet(InducementSet pInducementSet) {
		fInducementSet = pInducementSet;
	}

	public static Comparator<Team> comparatorByName() {
		return Comparator.comparing(Team::getName);
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
		UtilXml.addValueElement(pHandler, _XML_TAG_DEDICATED_FANS, getDedicatedFans());

		for (Player<?> player : getPlayers()) {
			player.addToXml(pHandler);
		}

		if (!specialRules.isEmpty()) {
			UtilXml.startElement(pHandler, _XML_TAG_SPECIAL_RULES);
			specialRules.forEach(rule -> UtilXml.addValueElement(pHandler, _XML_TAG_RULE, rule.getRuleName()));
			UtilXml.endElement(pHandler, _XML_TAG_SPECIAL_RULES);
		}

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (XML_TAG.equals(pXmlTag)) {
			setId(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID));
		}
		if (RosterPlayer.XML_TAG.equals(pXmlTag)) {
			RosterPlayer player = new RosterPlayer();
			player.startXmlElement(game, pXmlTag, pXmlAttributes);
			addPlayer(player);
			xmlElement = player;
		}

		if (ZappedPlayer.XML_TAG.equals(pXmlTag)) {
			ZappedPlayer player = new ZappedPlayer();
			player.startXmlElement(game, pXmlTag, pXmlAttributes);
			addPlayer(player);
			xmlElement = player;
		}
		// when reading XML only
		if (InducementSet.XML_TAG.equals(pXmlTag)) {
			setInducementSet(new InducementSet());
			getInducementSet().startXmlElement(game, pXmlTag, pXmlAttributes);
			xmlElement = getInducementSet();
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
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
			if (_XML_TAG_DEDICATED_FANS.equals(pXmlTag)) {
				setDedicatedFans(Integer.parseInt(pValue));
			}

			if (_XML_TAG_RULE.equals(pXmlTag)) {
				SpecialRule rule = SpecialRule.from(pValue);
				if (rule != null) {
					specialRules.add(rule);
				} else {
					game.getApplicationSource().logError(game.getId(), "Null value parsed from rules tag: '" + pValue + "' in roster with id '" + fId + "'");
				}
			}
		}
		return complete;
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
		IJsonOption.DEDICATED_FANS.addTo(jsonObject, dedicatedFans);
		IJsonOption.SPECIAL_RULES.addTo(jsonObject, specialRules.stream().filter(Objects::nonNull).map(SpecialRule::name).collect(Collectors.toSet()));

		JsonArray playerArray = new JsonArray();
		for (Player<?> player : getPlayers()) {
			playerArray.add(player.toJsonValue());
		}
		IJsonOption.PLAYER_ARRAY.addTo(jsonObject, playerArray);

		if (fRoster != null) {
			IJsonOption.ROSTER.addTo(jsonObject, fRoster.toJsonValue());
		}

		return jsonObject;

	}

	public Team initFrom(IFactorySource game, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		fId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fName = IJsonOption.TEAM_NAME.getFrom(game, jsonObject);
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fRace = IJsonOption.RACE.getFrom(game, jsonObject);
		fReRolls = IJsonOption.RE_ROLLS.getFrom(game, jsonObject);
		fApothecaries = IJsonOption.APOTHECARIES.getFrom(game, jsonObject);
		fCheerleaders = IJsonOption.CHEERLEADERS.getFrom(game, jsonObject);
		fAssistantCoaches = IJsonOption.ASSISTANT_COACHES.getFrom(game, jsonObject);
		fFanFactor = IJsonOption.FAN_FACTOR.getFrom(game, jsonObject);
		fTeamValue = IJsonOption.TEAM_VALUE.getFrom(game, jsonObject);
		fTreasury = IJsonOption.TREASURY.getFrom(game, jsonObject);
		fBaseIconPath = IJsonOption.BASE_ICON_PATH.getFrom(game, jsonObject);
		fLogoUrl = IJsonOption.LOGO_URL.getFrom(game, jsonObject);
		dedicatedFans = IJsonOption.DEDICATED_FANS.getFrom(game, jsonObject);
		String[] rulesArray = IJsonOption.SPECIAL_RULES.getFrom(game, jsonObject);
		if (ArrayTool.isProvided(rulesArray)) {
			specialRules.addAll(Arrays.stream(rulesArray).filter(Objects::nonNull).map(SpecialRule::valueOf).collect(Collectors.toSet()));
		}

		fPlayerById.clear();
		fPlayerByNr.clear();

		JsonArray playerArray = IJsonOption.PLAYER_ARRAY.getFrom(game, jsonObject);
		for (int i = 0; i < playerArray.size(); i++) {
			addPlayer(Player.getFrom(game, playerArray.get(i)));
		}

		Roster roster = null;
		JsonObject rosterObject = IJsonOption.ROSTER.getFrom(game, jsonObject);
		if (rosterObject != null) {
			roster = new Roster().initFrom(game, rosterObject);
		}
		updateRoster(roster, false, game);

		return this;

	}

}
