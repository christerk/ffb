package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.StringTool;
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
public class Roster implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "roster";

	private static final String _XML_ATTRIBUTE_ID = "id";
	private static final String _XML_ATTRIBUTE_TEAM = "team";

	private static final String _XML_TAG_NAME = "name";
	private static final String _XML_TAG_RE_ROLL_COST = "reRollCost";
	private static final String _XML_TAG_MAX_RE_ROLLS = "maxReRolls";
	private static final String _XML_TAG_BASE_ICON_PATH = "baseIconPath";
	private static final String _XML_TAG_LOGO_URL = "logo";
	private static final String _XML_TAG_RAISED_POSITION_ID = "raisedPositionId";
	private static final String _XML_TAG_APOTHECARY = "apothecary";
	private static final String _XML_TAG_NECROMANCER = "necromancer";
	private static final String _XML_TAG_UNDEAD = "undead";
	private static final String _XML_TAG_RIOTOUS_POSITION_ID = "riotousPositionId";
	private static final String _XML_TAG_NAME_GENERATOR = "nameGenerator";
	private static final String _XML_TAG_MAX_BIG_GUYS = "maxBigGuys";

	private String fId;
	private String fName;
	private int fReRollCost;
	private int fMaxReRolls;
	private String fBaseIconPath;
	private String fLogoUrl;
	private String fRaisedPositionId;
	private boolean fApothecary;
	private boolean fNecromancer;
	private boolean fUndead;
	private String riotousPositionId;
	private String nameGenerator;
	private int maxBigGuys;

	private RosterPosition fCurrentlyParsedRosterPosition;

	private final Map<String, RosterPosition> fRosterPositionById;
	private final Map<String, RosterPosition> fRosterPositionByName;

	public Roster() {
		fRosterPositionById = new HashMap<>();
		fRosterPositionByName = new HashMap<>();
		fApothecary = true;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public int getReRollCost() {
		return fReRollCost;
	}

	public void setReRollCost(int reRollCost) {
		fReRollCost = reRollCost;
	}

	public RosterPosition[] getPositions() {
		return fRosterPositionById.values().toArray(new RosterPosition[0]);
	}

	public RosterPosition getPositionById(String pPositionId) {
		return fRosterPositionById.get(pPositionId);
	}

	public RosterPosition getPositionByName(String pPositionName) {
		return fRosterPositionByName.get(pPositionName);
	}

	public int getMaxReRolls() {
		return fMaxReRolls;
	}

	public void setMaxReRolls(int maxReRolls) {
		fMaxReRolls = maxReRolls;
	}

	public String getId() {
		return fId;
	}

	public void setId(String pId) {
		fId = pId;
	}

	public RosterPosition getRaisedRosterPosition() {
		return fRosterPositionById.get(fRaisedPositionId);
	}

	private void addPosition(RosterPosition pPosition) {
		if (pPosition != null) {
			fRosterPositionById.put(pPosition.getId(), pPosition);
			fRosterPositionByName.put(pPosition.getName(), pPosition);
			pPosition.setRoster(this);
		}
	}

	public String getBaseIconPath() {
		return fBaseIconPath;
	}

	public void setBaseIconPath(String pBaseIconPath) {
		fBaseIconPath = pBaseIconPath;
	}

	public void setLogoUrl(String pLogoUrl) {
		fLogoUrl = pLogoUrl;
	}

	public String getLogoUrl() {
		return fLogoUrl;
	}

	public boolean hasApothecary() {
		return fApothecary;
	}

	public void setApothecary(boolean pApothecary) {
		fApothecary = pApothecary;
	}

	public boolean hasNecromancer() {
		return fNecromancer;
	}

	public void setNecromancer(boolean pNecromancer) {
		fNecromancer = pNecromancer;
	}

	public boolean isUndead() {
		return fUndead;
	}

	public void setUndead(boolean pUndead) {
		fUndead = pUndead;
	}

	public String getRiotousPositionId() {
		return riotousPositionId;
	}

	public void setRiotousPositionId(String riotousPositionId) {
		this.riotousPositionId = riotousPositionId;
	}

	public RosterPosition getRiotousPosition() {
		return fRosterPositionById.get(riotousPositionId);
	}

	public String getNameGenerator() {
		if (StringTool.isProvided(nameGenerator)) {
			return nameGenerator;
		}
		return "default";
	}

	public int getMaxBigGuys() {
		return maxBigGuys;
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
		UtilXml.startElement(pHandler, XML_TAG);

		UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
		UtilXml.addValueElement(pHandler, _XML_TAG_RE_ROLL_COST, getReRollCost());
		UtilXml.addValueElement(pHandler, _XML_TAG_MAX_RE_ROLLS, getMaxReRolls());
		UtilXml.addValueElement(pHandler, _XML_TAG_BASE_ICON_PATH, getBaseIconPath());
		UtilXml.addValueElement(pHandler, _XML_TAG_LOGO_URL, getLogoUrl());
		UtilXml.addValueElement(pHandler, _XML_TAG_RAISED_POSITION_ID, fRaisedPositionId);
		UtilXml.addValueElement(pHandler, _XML_TAG_APOTHECARY, hasApothecary());
		UtilXml.addValueElement(pHandler, _XML_TAG_NECROMANCER, hasNecromancer());
		UtilXml.addValueElement(pHandler, _XML_TAG_UNDEAD, isUndead());
		UtilXml.addValueElement(pHandler, _XML_TAG_RIOTOUS_POSITION_ID, getRiotousPositionId());
		UtilXml.addValueElement(pHandler, _XML_TAG_NAME_GENERATOR, nameGenerator);
		UtilXml.addValueElement(pHandler, _XML_TAG_MAX_BIG_GUYS, maxBigGuys);

		for (RosterPosition position : getPositions()) {
			position.addToXml(pHandler);
		}

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlSerializable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlSerializable xmlElement = this;
		if (XML_TAG.equals(pXmlTag)) {
			if (StringTool.isProvided(pXmlAttributes.getValue(_XML_ATTRIBUTE_ID))) {
				fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_ID).trim();
			}
			if (StringTool.isProvided(pXmlAttributes.getValue(_XML_ATTRIBUTE_TEAM))) {
				fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_TEAM).trim();
			}
		}
		if (RosterPosition.XML_TAG.equals(pXmlTag)) {
			fCurrentlyParsedRosterPosition = new RosterPosition(null);
			fCurrentlyParsedRosterPosition.startXmlElement(game, pXmlTag, pXmlAttributes);
			xmlElement = fCurrentlyParsedRosterPosition;
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		boolean complete = XML_TAG.equals(pXmlTag);
		if (!complete) {
			if (_XML_TAG_NAME.equals(pXmlTag)) {
				setName(pValue);
			}
			if (_XML_TAG_RE_ROLL_COST.equals(pXmlTag)) {
				setReRollCost(Integer.parseInt(pValue));
			}
			if (_XML_TAG_MAX_RE_ROLLS.equals(pXmlTag)) {
				setMaxReRolls(Integer.parseInt(pValue));
			}
			if (_XML_TAG_BASE_ICON_PATH.equals(pXmlTag)) {
				setBaseIconPath(pValue);
			}
			if (_XML_TAG_LOGO_URL.equals(pXmlTag)) {
				setLogoUrl(pValue);
			}
			if (_XML_TAG_RAISED_POSITION_ID.equals(pXmlTag)) {
				fRaisedPositionId = pValue;
			}
			if (RosterPosition.XML_TAG.equals(pXmlTag)) {
				addPosition(fCurrentlyParsedRosterPosition);
			}
			if (_XML_TAG_APOTHECARY.equals(pXmlTag)) {
				setApothecary(Boolean.parseBoolean(pValue));
			}
			if (_XML_TAG_NECROMANCER.equals(pXmlTag)) {
				setNecromancer(Boolean.parseBoolean(pValue));
			}
			if (_XML_TAG_UNDEAD.equals(pXmlTag)) {
				setUndead(Boolean.parseBoolean(pValue));
			}
			if (_XML_TAG_RIOTOUS_POSITION_ID.equals(pXmlTag)) {
				riotousPositionId = pValue;
			}
			if (_XML_TAG_NAME_GENERATOR.equals(pXmlTag)) {
				nameGenerator = pValue;
			}

			if (_XML_TAG_MAX_BIG_GUYS.equals(pXmlTag)) {
				maxBigGuys = Integer.parseInt(pValue);
			}
		}
		return complete;
	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();

		IJsonOption.ROSTER_ID.addTo(jsonObject, fId);
		IJsonOption.ROSTER_NAME.addTo(jsonObject, fName);
		IJsonOption.RE_ROLL_COST.addTo(jsonObject, fReRollCost);
		IJsonOption.MAX_RE_ROLLS.addTo(jsonObject, fMaxReRolls);
		IJsonOption.BASE_ICON_PATH.addTo(jsonObject, fBaseIconPath);
		IJsonOption.LOGO_URL.addTo(jsonObject, fLogoUrl);
		IJsonOption.RAISED_POSITION_ID.addTo(jsonObject, fRaisedPositionId);
		IJsonOption.APOTHECARY.addTo(jsonObject, fApothecary);
		IJsonOption.NECROMANCER.addTo(jsonObject, fNecromancer);
		IJsonOption.UNDEAD.addTo(jsonObject, fUndead);
		IJsonOption.RIOTOUS_POSITION_ID.addTo(jsonObject, riotousPositionId);
		IJsonOption.NAME_GENERATOR.addTo(jsonObject, nameGenerator);
		IJsonOption.MAX_BIG_GUYS.addTo(jsonObject, maxBigGuys);

		JsonArray positionArray = new JsonArray();
		for (RosterPosition position : getPositions()) {
			positionArray.add(position.toJsonValue());
		}
		IJsonOption.POSITION_ARRAY.addTo(jsonObject, positionArray);

		return jsonObject;

	}

	public Roster initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		fId = IJsonOption.ROSTER_ID.getFrom(source, jsonObject);
		fName = IJsonOption.ROSTER_NAME.getFrom(source, jsonObject);
		fReRollCost = IJsonOption.RE_ROLL_COST.getFrom(source, jsonObject);
		fMaxReRolls = IJsonOption.MAX_RE_ROLLS.getFrom(source, jsonObject);
		fBaseIconPath = IJsonOption.BASE_ICON_PATH.getFrom(source, jsonObject);
		fLogoUrl = IJsonOption.LOGO_URL.getFrom(source, jsonObject);
		fRaisedPositionId = IJsonOption.RAISED_POSITION_ID.getFrom(source, jsonObject);
		fApothecary = IJsonOption.APOTHECARY.getFrom(source, jsonObject);
		fNecromancer = IJsonOption.NECROMANCER.getFrom(source, jsonObject);
		fUndead = IJsonOption.UNDEAD.getFrom(source, jsonObject);
		riotousPositionId = IJsonOption.RIOTOUS_POSITION_ID.getFrom(source, jsonObject);
		nameGenerator = IJsonOption.NAME_GENERATOR.getFrom(source, jsonObject);

		fRosterPositionById.clear();
		fRosterPositionByName.clear();
		JsonArray positionArray = IJsonOption.POSITION_ARRAY.getFrom(source, jsonObject);
		if (positionArray != null) {
			for (int i = 0; i < positionArray.size(); i++) {
				addPosition(new RosterPosition().initFrom(source, positionArray.get(i)));
			}
		}

		maxBigGuys = IJsonOption.MAX_BIG_GUYS.getFrom(source, jsonObject);

		return this;

	}

}
