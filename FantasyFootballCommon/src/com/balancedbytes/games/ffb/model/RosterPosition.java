package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.factory.PlayerGenderFactory;
import com.balancedbytes.games.ffb.factory.PlayerTypeFactory;
import com.balancedbytes.games.ffb.factory.SkillCategoryFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class RosterPosition implements Position {

	public static final String XML_TAG = "position";

	private static final String _XML_ATTRIBUTE_ID = "id";
	private static final String _XML_ATTRIBUTE_VALUE = "value";
	private static final String _XML_ATTRIBUTE_SIZE = "size";

	private static final String _XML_TAG_QUANTITY = "quantity";
	private static final String _XML_TAG_NAME = "name";
	private static final String _XML_TAG_DISPLAY_NAME = "displayName";
	private static final String _XML_TAG_TYPE = "type";
	private static final String _XML_TAG_GENDER = "gender";
	private static final String _XML_TAG_COST = "cost";
	private static final String _XML_TAG_MOVEMENT = "movement";
	private static final String _XML_TAG_STRENGTH = "strength";
	private static final String _XML_TAG_AGILITY = "agility";
	private static final String _XML_TAG_ARMOUR = "armour";
	private static final String _XML_TAG_SHORTHAND = "shorthand";
	private static final String _XML_TAG_RACE = "race";
	private static final String _XML_TAG_UNDEAD = "undead";
	private static final String _XML_TAG_THRALL = "thrall";
	private static final String _XML_TAG_TEAM_WITH_POSITION_ID = "teamWithPositionId";

	private static final String _XML_TAG_SKILL_LIST = "skillList";
	private static final String _XML_TAG_SKILL = "skill";

	private static final String _XML_TAG_SKILLCATEGORY_LIST = "skillCategoryList";
	private static final String _XML_TAG_NORMAL = "normal";
	private static final String _XML_TAG_DOUBLE = "double";

	private static final String _XML_TAG_PORTRAIT = "portrait";
	private static final String _XML_TAG_ICON_SET = "iconSet";
	private static final String _XML_TAG_NAME_GENERATOR = "nameGenerator";

	private String fId;
	private Roster fRoster;
	private String fName;
	private String fDisplayName;
	private String fShorthand;
	private PlayerType fType;
	private PlayerGender fGender;
	private int fQuantity;
	private int fCost;
	private int fMovement;
	private int fStrength;
	private int fAgility;
	private int fArmour;
	private String fRace;
	private boolean fUndead;
	private boolean fThrall;
	private String fTeamWithPositionId;

	private String fUrlPortrait;
	private String fUrlIconSet;
	private int fNrOfIcons;
	private int fCurrentIconSetIndex;
	private String nameGenerator;

	private Map<Skill, Integer> fSkillValues;
	private Set<SkillCategory> fSkillCategoriesOnNormalRoll;
	private Set<SkillCategory> fSkillCategoriesOnDoubleRoll;

	// attributes used for parsing
	private transient boolean fInsideSkillListTag;
	private transient boolean fInsideSkillCategoryListTag;
	private transient Integer fCurrentSkillValue;

	public RosterPosition() {
		this(null);
	}

	public RosterPosition(String pId) {
		fId = pId;
		fSkillValues = new LinkedHashMap<Skill, Integer>();
		fSkillCategoriesOnNormalRoll = new HashSet<SkillCategory>();
		fSkillCategoriesOnDoubleRoll = new HashSet<SkillCategory>();
		fCurrentIconSetIndex = -1;
	}

	@Override
	public PlayerType getType() {
		return fType;
	}

	public void setGender(PlayerGender pGender) {
		fGender = pGender;
	}

	@Override
	public PlayerGender getGender() {
		return fGender;
	}

	@Override
	public int getAgility() {
		return fAgility;
	}

	@Override
	public int getArmour() {
		return fArmour;
	}

	@Override
	public int getMovement() {
		return fMovement;
	}

	@Override
	public int getCost() {
		return fCost;
	}

	@Override
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public void setShorthand(String pShorthand) {
		fShorthand = pShorthand;
	}

	@Override
	public String getShorthand() {
		return fShorthand;
	}

	@Override
	public int getStrength() {
		return fStrength;
	}

	@Override
	public SkillCategory[] getSkillCategories(boolean pOnDouble) {
		if (pOnDouble) {
			return fSkillCategoriesOnDoubleRoll.toArray(new SkillCategory[fSkillCategoriesOnDoubleRoll.size()]);
		} else {
			return fSkillCategoriesOnNormalRoll.toArray(new SkillCategory[fSkillCategoriesOnNormalRoll.size()]);
		}
	}

	@Override
	public boolean isDoubleCategory(SkillCategory pSkillCategory) {
		return fSkillCategoriesOnDoubleRoll.contains(pSkillCategory);
	}

	@Override
	public boolean hasSkill(Skill pSkill) {
		return fSkillValues.containsKey(pSkill);
	}

	@Override
	public Skill[] getSkills() {
		return fSkillValues.keySet().toArray(new Skill[fSkillValues.size()]);
	}

	@Override
	public int getSkillValue(Skill pSkill) {
		Integer value = fSkillValues.get(pSkill);
		return (value != null) ? value : 0;
	}

	@Override
	public String getUrlPortrait() {
		return fUrlPortrait;
	}

	@Override
	public void setUrlPortrait(String pUrlPortrait) {
		fUrlPortrait = pUrlPortrait;
	}

	@Override
	public String getUrlIconSet() {
		return fUrlIconSet;
	}

	public void setUrlIconSet(String pUrlIconSet) {
		fUrlIconSet = pUrlIconSet;
	}

	@Override
	public int getQuantity() {
		return fQuantity;
	}

	@Override
	public Roster getRoster() {
		return fRoster;
	}

	protected void setRoster(Roster pRoster) {
		fRoster = pRoster;
	}

	@Override
	public String getId() {
		return fId;
	}

	@Override
	public int getNrOfIcons() {
		return fNrOfIcons;
	}

	public void setNrOfIcons(int pNrOfIcons) {
		fNrOfIcons = pNrOfIcons;
	}

	@Override
	public int findNextIconSetIndex() {
		if (fNrOfIcons > 0) {
			fCurrentIconSetIndex++;
			if (fCurrentIconSetIndex >= fNrOfIcons) {
				fCurrentIconSetIndex = 0;
			}
		}
		return fCurrentIconSetIndex;
	}

	public void setType(PlayerType type) {
		fType = type;
	}

	public void setCost(int cost) {
		fCost = cost;
	}

	public void setMovement(int movement) {
		fMovement = movement;
	}

	public void setStrength(int strength) {
		fStrength = strength;
	}

	public void setAgility(int agility) {
		fAgility = agility;
	}

	public void setArmour(int armour) {
		fArmour = armour;
	}

	public void setQuantity(int quantity) {
		fQuantity = quantity;
	}

	@Override
	public String getDisplayName() {
		return fDisplayName;
	}

	public void setDisplayName(String pDisplayName) {
		fDisplayName = pDisplayName;
	}

	@Override
	public String getRace() {
		return fRace;
	}

	public void setRace(String pRace) {
		fRace = pRace;
	}

	@Override
	public boolean isUndead() {
		return fUndead;
	}

	public void setUndead(boolean pUndead) {
		fUndead = pUndead;
	}

	@Override
	public boolean isThrall() {
		return fThrall;
	}

	public void setThrall(boolean pThrall) {
		fThrall = pThrall;
	}

	public void setTeamWithPositionId(String pTeamWithPositionId) {
		fTeamWithPositionId = pTeamWithPositionId;
	}

	@Override
	public String getTeamWithPositionId() {
		return fTeamWithPositionId;
	}

	public String getNameGenerator() {
		if (StringTool.isProvided(nameGenerator)) {
			return nameGenerator;
		}
		return getRoster().getNameGenerator();
	}
	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
		UtilXml.startElement(pHandler, XML_TAG, attributes);

		UtilXml.addValueElement(pHandler, _XML_TAG_QUANTITY, getQuantity());
		UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
		UtilXml.addValueElement(pHandler, _XML_TAG_SHORTHAND, getShorthand());
		UtilXml.addValueElement(pHandler, _XML_TAG_TYPE, (getType() != null) ? getType().getName() : null);
		UtilXml.addValueElement(pHandler, _XML_TAG_GENDER, (getGender() != null) ? getGender().getName() : null);
		UtilXml.addValueElement(pHandler, _XML_TAG_DISPLAY_NAME, getDisplayName());
		UtilXml.addValueElement(pHandler, _XML_TAG_COST, getCost());
		UtilXml.addValueElement(pHandler, _XML_TAG_MOVEMENT, getMovement());
		UtilXml.addValueElement(pHandler, _XML_TAG_STRENGTH, getStrength());
		UtilXml.addValueElement(pHandler, _XML_TAG_AGILITY, getAgility());
		UtilXml.addValueElement(pHandler, _XML_TAG_ARMOUR, getArmour());
		UtilXml.addValueElement(pHandler, _XML_TAG_RACE, getRace());
		UtilXml.addValueElement(pHandler, _XML_TAG_UNDEAD, isUndead());
		UtilXml.addValueElement(pHandler, _XML_TAG_THRALL, isThrall());
		UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_WITH_POSITION_ID, getTeamWithPositionId());
		UtilXml.addValueElement(pHandler, _XML_TAG_NAME_GENERATOR, nameGenerator);

		UtilXml.addValueElement(pHandler, _XML_TAG_PORTRAIT, getUrlPortrait());

		attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SIZE, getNrOfIcons());
		UtilXml.startElement(pHandler, _XML_TAG_ICON_SET, attributes);
		UtilXml.addCharacters(pHandler, getUrlIconSet());
		UtilXml.endElement(pHandler, _XML_TAG_ICON_SET);

		UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_WITH_POSITION_ID, getTeamWithPositionId());

		UtilXml.startElement(pHandler, _XML_TAG_SKILLCATEGORY_LIST);

		for (SkillCategory skillCategory : getSkillCategories(false)) {
			UtilXml.addValueElement(pHandler, _XML_TAG_NORMAL, skillCategory.getName());
		}

		for (SkillCategory skillCategory : getSkillCategories(true)) {
			UtilXml.addValueElement(pHandler, _XML_TAG_DOUBLE, skillCategory.getName());
		}

		UtilXml.endElement(pHandler, _XML_TAG_SKILLCATEGORY_LIST);

		UtilXml.startElement(pHandler, _XML_TAG_SKILL_LIST);

		for (Skill skill : getSkills()) {
			attributes = new AttributesImpl();
			if (getSkillValue(skill) > 0) {
				UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getSkillValue(skill));
			}
			UtilXml.startElement(pHandler, _XML_TAG_SKILL, attributes);
			UtilXml.addCharacters(pHandler, skill.getName());
			UtilXml.endElement(pHandler, _XML_TAG_SKILL);
		}

		UtilXml.endElement(pHandler, _XML_TAG_SKILL_LIST);

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		if (fInsideSkillListTag) {
			if (_XML_TAG_SKILL.equals(pXmlTag)) {
				String skillValue = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
				if (StringTool.isProvided(skillValue)) {
					fCurrentSkillValue = Integer.parseInt(skillValue);
				} else {
					fCurrentSkillValue = null;
				}
			}
		} else {
			if (XML_TAG.equals(pXmlTag)) {
				fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_ID).trim();
			}
			if (_XML_TAG_SKILLCATEGORY_LIST.equals(pXmlTag)) {
				fInsideSkillCategoryListTag = true;
			}
			if (_XML_TAG_ICON_SET.equals(pXmlTag)) {
				setNrOfIcons(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_SIZE));
			}
			if (_XML_TAG_SKILL_LIST.equals(pXmlTag)) {
				fInsideSkillListTag = true;
			}
		}
		return this;
	}

	public boolean endXmlElement(Game game, String pTag, String pValue) {
		boolean complete = XML_TAG.equals(pTag);
		if (complete) {
			// set a default shortcut if it is missing
			if (!StringTool.isProvided(getShorthand()) && StringTool.isProvided(getName())) {
				setShorthand(getName().substring(0, 1));
			}
		} else if (fInsideSkillListTag) {
			if (_XML_TAG_SKILL_LIST.equals(pTag)) {
				fInsideSkillListTag = false;
			}
			if (_XML_TAG_SKILL.equals(pTag)) {
				Skill skill = game.getRules().<SkillFactory>getFactory(Factory.SKILL).forName(pValue);
				if (skill != null) {
					fSkillValues.put(skill, fCurrentSkillValue);
				}
			}
		} else if (fInsideSkillCategoryListTag) {
			if (_XML_TAG_SKILLCATEGORY_LIST.equals(pTag)) {
				fInsideSkillCategoryListTag = false;
			}
			if (_XML_TAG_NORMAL.equals(pTag)) {
				SkillCategory pSkillCategory = new SkillCategoryFactory().forName(pValue);
				if (pSkillCategory != null) {
					fSkillCategoriesOnNormalRoll.add(pSkillCategory);
				}
			}
			if (_XML_TAG_DOUBLE.equals(pTag)) {
				SkillCategory pSkillCategory = new SkillCategoryFactory().forName(pValue);
				if (pSkillCategory != null) {
					fSkillCategoriesOnDoubleRoll.add(pSkillCategory);
				}
			}
		} else {
			if (_XML_TAG_PORTRAIT.equals(pTag)) {
				setUrlPortrait(pValue);
			}
			if (_XML_TAG_ICON_SET.equals(pTag)) {
				setUrlIconSet(pValue);
				if (getNrOfIcons() < 1) {
					setNrOfIcons(1);
				}
			}
			if (_XML_TAG_QUANTITY.equals(pTag)) {
				setQuantity(Integer.parseInt(pValue));
			}
			if (_XML_TAG_NAME.equals(pTag)) {
				setName(pValue);
			}
			if (_XML_TAG_DISPLAY_NAME.equals(pTag)) {
				setDisplayName(pValue);
			}
			if (_XML_TAG_SHORTHAND.equals(pTag)) {
				setShorthand(pValue);
			}
			if (_XML_TAG_TYPE.equals(pTag)) {
				setType(new PlayerTypeFactory().forName(pValue));
			}
			if (_XML_TAG_GENDER.equals(pTag)) {
				setGender(new PlayerGenderFactory().forName(pValue));
			}
			if (_XML_TAG_COST.equals(pTag)) {
				setCost(Integer.parseInt(pValue));
			}
			if (_XML_TAG_MOVEMENT.equals(pTag)) {
				setMovement(Integer.parseInt(pValue));
			}
			if (_XML_TAG_STRENGTH.equals(pTag)) {
				setStrength(Integer.parseInt(pValue));
			}
			if (_XML_TAG_AGILITY.equals(pTag)) {
				setAgility(Integer.parseInt(pValue));
			}
			if (_XML_TAG_ARMOUR.equals(pTag)) {
				setArmour(Integer.parseInt(pValue));
			}
			if (_XML_TAG_RACE.equals(pTag)) {
				setRace(pValue);
			}
			if (_XML_TAG_UNDEAD.equals(pTag)) {
				setUndead(Boolean.parseBoolean(pValue));
			}
			if (_XML_TAG_THRALL.equals(pTag)) {
				setThrall(Boolean.parseBoolean(pValue));
			}
			if (_XML_TAG_TEAM_WITH_POSITION_ID.equals(pTag)) {
				setTeamWithPositionId(pValue);
			}
			if (_XML_TAG_NAME_GENERATOR.equals(pTag)) {
				nameGenerator = pValue;
			}
		}
		return complete;
	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();

		IJsonOption.POSITION_ID.addTo(jsonObject, fId);
		IJsonOption.POSITION_NAME.addTo(jsonObject, fName);
		IJsonOption.SHORTHAND.addTo(jsonObject, fShorthand);
		IJsonOption.DISPLAY_NAME.addTo(jsonObject, fDisplayName);
		IJsonOption.PLAYER_TYPE.addTo(jsonObject, fType);
		IJsonOption.PLAYER_GENDER.addTo(jsonObject, fGender);
		IJsonOption.QUANTITY.addTo(jsonObject, fQuantity);
		IJsonOption.MOVEMENT.addTo(jsonObject, fMovement);
		IJsonOption.STRENGTH.addTo(jsonObject, fStrength);
		IJsonOption.AGILITY.addTo(jsonObject, fAgility);
		IJsonOption.ARMOUR.addTo(jsonObject, fArmour);
		IJsonOption.COST.addTo(jsonObject, fCost);
		IJsonOption.RACE.addTo(jsonObject, fRace);
		IJsonOption.UNDEAD.addTo(jsonObject, fUndead);
		IJsonOption.THRALL.addTo(jsonObject, fThrall);
		IJsonOption.TEAM_WITH_POSITION_ID.addTo(jsonObject, fTeamWithPositionId);
		IJsonOption.NAME_GENERATOR.addTo(jsonObject, nameGenerator);

		IJsonOption.URL_PORTRAIT.addTo(jsonObject, fUrlPortrait);
		IJsonOption.URL_ICON_SET.addTo(jsonObject, fUrlIconSet);
		IJsonOption.NR_OF_ICONS.addTo(jsonObject, fNrOfIcons);

		JsonArray skillCategoriesNormal = new JsonArray();
		for (SkillCategory skillCategory : getSkillCategories(false)) {
			skillCategoriesNormal.add(UtilJson.toJsonValue(skillCategory));
		}
		IJsonOption.SKILL_CATEGORIES_NORMAL.addTo(jsonObject, skillCategoriesNormal);

		JsonArray skillCategoriesDouble = new JsonArray();
		for (SkillCategory skillCategory : getSkillCategories(true)) {
			skillCategoriesDouble.add(UtilJson.toJsonValue(skillCategory));
		}
		IJsonOption.SKILL_CATEGORIES_DOUBLE.addTo(jsonObject, skillCategoriesDouble);

		JsonArray skillArray = new JsonArray();
		List<Integer> skillValues = new ArrayList<Integer>();
		for (Skill skill : getSkills()) {
			skillArray.add(UtilJson.toJsonValue(skill));
			skillValues.add(getSkillValue(skill));
		}
		if (skillArray.size() > 0) {
			IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		}
		if (skillValues.size() > 0) {
			IJsonOption.SKILL_VALUES.addTo(jsonObject, skillValues);
		}

		return jsonObject;

	}

	public RosterPosition initFrom(Game game, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		fId = IJsonOption.POSITION_ID.getFrom(game, jsonObject);
		fName = IJsonOption.POSITION_NAME.getFrom(game, jsonObject);
		fShorthand = IJsonOption.SHORTHAND.getFrom(game, jsonObject);
		fDisplayName = IJsonOption.DISPLAY_NAME.getFrom(game, jsonObject);
		fType = (PlayerType) IJsonOption.PLAYER_TYPE.getFrom(game, jsonObject);
		fGender = (PlayerGender) IJsonOption.PLAYER_GENDER.getFrom(game, jsonObject);
		fQuantity = IJsonOption.QUANTITY.getFrom(game, jsonObject);
		fMovement = IJsonOption.MOVEMENT.getFrom(game, jsonObject);
		fStrength = IJsonOption.STRENGTH.getFrom(game, jsonObject);
		fAgility = IJsonOption.AGILITY.getFrom(game, jsonObject);
		fArmour = IJsonOption.ARMOUR.getFrom(game, jsonObject);
		fCost = IJsonOption.COST.getFrom(game, jsonObject);
		fRace = IJsonOption.RACE.getFrom(game, jsonObject);
		fUndead = IJsonOption.UNDEAD.getFrom(game, jsonObject);
		fThrall = IJsonOption.THRALL.getFrom(game, jsonObject);
		fTeamWithPositionId = IJsonOption.TEAM_WITH_POSITION_ID.getFrom(game, jsonObject);
		nameGenerator = IJsonOption.NAME_GENERATOR.getFrom(game, jsonObject);

		fUrlPortrait = IJsonOption.URL_PORTRAIT.getFrom(game, jsonObject);
		fUrlIconSet = IJsonOption.URL_ICON_SET.getFrom(game, jsonObject);
		fNrOfIcons = IJsonOption.NR_OF_ICONS.getFrom(game, jsonObject);

		SkillCategoryFactory skillCategoryFactory = new SkillCategoryFactory();

		fSkillCategoriesOnNormalRoll.clear();
		JsonArray skillCategoriesNormal = IJsonOption.SKILL_CATEGORIES_NORMAL.getFrom(game, jsonObject);
		for (int i = 0; i < skillCategoriesNormal.size(); i++) {
			fSkillCategoriesOnNormalRoll
					.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesNormal.get(i)));
		}
		fSkillCategoriesOnDoubleRoll.clear();
		JsonArray skillCategoriesDouble = IJsonOption.SKILL_CATEGORIES_DOUBLE.getFrom(game, jsonObject);
		for (int i = 0; i < skillCategoriesDouble.size(); i++) {
			fSkillCategoriesOnDoubleRoll
					.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesDouble.get(i)));
		}

		fSkillValues.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(game, jsonObject);
		int[] skillValues = IJsonOption.SKILL_VALUES.getFrom(game, jsonObject);
		if ((skillArray != null) && (skillArray.size() > 0) && ArrayTool.isProvided(skillValues)) {
			SkillFactory skillFactory = game.getRules().getSkillFactory();
			for (int i = 0; i < skillArray.size(); i++) {
				Skill skill = (Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i));
				fSkillValues.put(skill, skillValues[i]);
			}
		}

		return this;

	}

}
