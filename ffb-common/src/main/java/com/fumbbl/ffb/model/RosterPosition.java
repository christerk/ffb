package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PlayerGenderFactory;
import com.fumbbl.ffb.factory.PlayerTypeFactory;
import com.fumbbl.ffb.factory.SkillCategoryFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class RosterPosition implements Position {

	public static final String XML_TAG = "position";

	private static final String _XML_ATTRIBUTE_ID = "id";
	private static final String _XML_ATTRIBUTE_DISPLAY_VALUE = "displayValueAs";
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
	private static final String _XML_TAG_PASSING = "passing";
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

	private static final String _XML_TAG_REPLACES_POSITION = "replacesPosition";
	private static final String _XML_TAG_KEYWORDS = "keywords";
	private static final String _XML_TAG_KEYWORD = "keyword";

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
	private int fPassing;
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
	private String replacesPosition;
	private final List<Keyword> keywords;

	private final Map<Skill, String> fSkillValues;
	private final Map<Skill, String> displayValues;
	private final Set<SkillCategory> fSkillCategoriesOnNormalRoll;
	private final Set<SkillCategory> fSkillCategoriesOnDoubleRoll;

	// attributes used for parsing
	private transient boolean fInsideSkillListTag;
	private transient boolean fInsideSkillCategoryListTag;
	private transient String fCurrentSkillValue;
	private transient String currentDisplayValue;

	public RosterPosition() {
		this(null);
	}

	public RosterPosition(String pId) {
		fId = pId;
		fSkillValues = new LinkedHashMap<>();
		fSkillCategoriesOnNormalRoll = new HashSet<>();
		fSkillCategoriesOnDoubleRoll = new HashSet<>();
		displayValues = new LinkedHashMap<>();
		fCurrentIconSetIndex = -1;
		keywords = new ArrayList<>();
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
	public int getMovement() {
		return fMovement;
	}

	@Override
	public int getStrength() {
		return fStrength;
	}

	@Override
	public int getAgility() {
		return fAgility;
	}

	@Override
	public int getPassing() {
		return fPassing;
	}

	@Override
	public int getArmour() {
		return fArmour;
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
	public SkillCategory[] getSkillCategories(boolean pOnDouble) {
		if (pOnDouble) {
			return fSkillCategoriesOnDoubleRoll.toArray(new SkillCategory[0]);
		} else {
			return fSkillCategoriesOnNormalRoll.toArray(new SkillCategory[0]);
		}
	}

	@Override
	public List<Keyword> getKeywords() {
		return keywords;
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
		return fSkillValues.keySet().toArray(new Skill[0]);
	}

	@Override
	public String getSkillValue(Skill pSkill) {
		return fSkillValues.get(pSkill);
	}

	@Override
	public String getDisplayValue(Skill pSkill) {
		return displayValues.get(pSkill);
	}

	@Override
	public int getSkillIntValue(Skill skill) {
		String skillValue = getSkillValue(skill);
		if (StringTool.isProvided(skillValue) && StringTool.isNumber(skillValue)) {
			return Integer.parseInt(skillValue);
		}
		return skill.getDefaultSkillValue();
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

	public void setPassing(int passing) {
		fPassing = passing;
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
		return fThrall || keywords.contains(Keyword.THRALL);
	}

	@Override
	public boolean isDwarf() {
		return keywords.contains(Keyword.DWARF);
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

	public String getReplacesPosition() {
		return replacesPosition;
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
		UtilXml.addValueElement(pHandler, _XML_TAG_PASSING, getPassing());
		UtilXml.addValueElement(pHandler, _XML_TAG_ARMOUR, getArmour());
		UtilXml.addValueElement(pHandler, _XML_TAG_RACE, getRace());
		UtilXml.addValueElement(pHandler, _XML_TAG_UNDEAD, isUndead());
		UtilXml.addValueElement(pHandler, _XML_TAG_THRALL, isThrall());
		UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_WITH_POSITION_ID, getTeamWithPositionId());
		UtilXml.addValueElement(pHandler, _XML_TAG_NAME_GENERATOR, nameGenerator);
		UtilXml.addValueElement(pHandler, _XML_TAG_REPLACES_POSITION, replacesPosition);

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
			if (StringTool.isProvided(getSkillValue(skill))) {
				UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getSkillValue(skill));
			}
			if (StringTool.isProvided(getDisplayValue(skill))) {
				UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DISPLAY_VALUE, getDisplayValue(skill));
			}
			UtilXml.startElement(pHandler, _XML_TAG_SKILL, attributes);
			UtilXml.addCharacters(pHandler, skill.getName());
			UtilXml.endElement(pHandler, _XML_TAG_SKILL);
		}

		UtilXml.endElement(pHandler, _XML_TAG_SKILL_LIST);

		UtilXml.startElement(pHandler, _XML_TAG_KEYWORDS);

		for (Keyword keyword : keywords) {
			UtilXml.addValueElement(pHandler, _XML_TAG_KEYWORD, keyword.getName());
		}

		UtilXml.endElement(pHandler, _XML_TAG_KEYWORDS);

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
					fCurrentSkillValue = skillValue;
				} else {
					fCurrentSkillValue = null;
				}
				String displayValue = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_DISPLAY_VALUE);
				if (StringTool.isProvided(displayValue)) {
					currentDisplayValue = displayValue;
				} else {
					currentDisplayValue = null;
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
					displayValues.put(skill, currentDisplayValue);
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
			if (_XML_TAG_PASSING.equals(pTag)) {
				setPassing(pValue != null && pValue.length() > 0 ? Integer.parseInt(pValue) : 0);
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
			if (_XML_TAG_REPLACES_POSITION.equals(pTag)) {
				replacesPosition = pValue;
			}
			if (_XML_TAG_KEYWORD.equalsIgnoreCase(pTag)) {
				keywords.add(Keyword.forName(pValue));
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
		IJsonOption.PASSING.addTo(jsonObject, fPassing);
		IJsonOption.ARMOUR.addTo(jsonObject, fArmour);
		IJsonOption.COST.addTo(jsonObject, fCost);
		IJsonOption.RACE.addTo(jsonObject, fRace);
		IJsonOption.UNDEAD.addTo(jsonObject, fUndead);
		IJsonOption.THRALL.addTo(jsonObject, fThrall);
		IJsonOption.TEAM_WITH_POSITION_ID.addTo(jsonObject, fTeamWithPositionId);
		IJsonOption.NAME_GENERATOR.addTo(jsonObject, nameGenerator);
		IJsonOption.REPLACES_POSITION.addTo(jsonObject, replacesPosition);

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
		List<String> skillValues = new ArrayList<>();
		List<String> displayValues = new ArrayList<>();
		for (Skill skill : getSkills()) {
			skillArray.add(UtilJson.toJsonValue(skill));
			skillValues.add(fSkillValues.get(skill));
			displayValues.add(this.displayValues.get(skill));
		}
		if (skillArray.size() > 0) {
			IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);
		}
		if (skillValues.size() > 0) {
			IJsonOption.SKILL_VALUES.addTo(jsonObject, skillValues);
		}
		if (displayValues.size() > 0) {
			IJsonOption.SKILL_DISPLAY_VALUES.addTo(jsonObject, displayValues);
		}

		IJsonOption.KEYWORDS.addTo(jsonObject, keywords.stream().map(Keyword::getName).collect(Collectors.toList()));

		return jsonObject;

	}

	public RosterPosition initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		fId = IJsonOption.POSITION_ID.getFrom(source, jsonObject);
		fName = IJsonOption.POSITION_NAME.getFrom(source, jsonObject);
		fShorthand = IJsonOption.SHORTHAND.getFrom(source, jsonObject);
		fDisplayName = IJsonOption.DISPLAY_NAME.getFrom(source, jsonObject);
		fType = (PlayerType) IJsonOption.PLAYER_TYPE.getFrom(source, jsonObject);
		fGender = (PlayerGender) IJsonOption.PLAYER_GENDER.getFrom(source, jsonObject);
		fQuantity = IJsonOption.QUANTITY.getFrom(source, jsonObject);
		fMovement = IJsonOption.MOVEMENT.getFrom(source, jsonObject);
		fStrength = IJsonOption.STRENGTH.getFrom(source, jsonObject);
		fAgility = IJsonOption.AGILITY.getFrom(source, jsonObject);
		fPassing = IJsonOption.PASSING.getFrom(source, jsonObject);
		fArmour = IJsonOption.ARMOUR.getFrom(source, jsonObject);
		fCost = IJsonOption.COST.getFrom(source, jsonObject);
		fRace = IJsonOption.RACE.getFrom(source, jsonObject);
		fUndead = IJsonOption.UNDEAD.getFrom(source, jsonObject);
		fThrall = IJsonOption.THRALL.getFrom(source, jsonObject);
		fTeamWithPositionId = IJsonOption.TEAM_WITH_POSITION_ID.getFrom(source, jsonObject);
		nameGenerator = IJsonOption.NAME_GENERATOR.getFrom(source, jsonObject);
		replacesPosition = IJsonOption.REPLACES_POSITION.getFrom(source, jsonObject);

		fUrlPortrait = IJsonOption.URL_PORTRAIT.getFrom(source, jsonObject);
		fUrlIconSet = IJsonOption.URL_ICON_SET.getFrom(source, jsonObject);
		fNrOfIcons = IJsonOption.NR_OF_ICONS.getFrom(source, jsonObject);

		SkillCategoryFactory skillCategoryFactory = new SkillCategoryFactory();

		fSkillCategoriesOnNormalRoll.clear();
		JsonArray skillCategoriesNormal = IJsonOption.SKILL_CATEGORIES_NORMAL.getFrom(source, jsonObject);
		for (int i = 0; i < skillCategoriesNormal.size(); i++) {
			fSkillCategoriesOnNormalRoll
				.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesNormal.get(i)));
		}
		fSkillCategoriesOnDoubleRoll.clear();
		JsonArray skillCategoriesDouble = IJsonOption.SKILL_CATEGORIES_DOUBLE.getFrom(source, jsonObject);
		for (int i = 0; i < skillCategoriesDouble.size(); i++) {
			fSkillCategoriesOnDoubleRoll
				.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesDouble.get(i)));
		}

		fSkillValues.clear();
		displayValues.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		String[] skillValues = IJsonOption.SKILL_VALUES.getFrom(source, jsonObject);
		String[] displayValues = IJsonOption.SKILL_DISPLAY_VALUES.getFrom(source, jsonObject);
		if ((skillArray != null) && (skillArray.size() > 0) && ArrayTool.isProvided(skillValues)) {
			SkillFactory skillFactory = source.getFactory(Factory.SKILL);
			for (int i = 0; i < skillArray.size(); i++) {
				Skill skill = (Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i));
				if (skill != null) {
					fSkillValues.put(skill, skillValues[i]);
					if (displayValues != null && displayValues.length >= i + 1) {
						this.displayValues.put(skill, displayValues[i]);
					}
				}
			}
		}

		if (IJsonOption.KEYWORDS.isDefinedIn(jsonObject)) {
			for (String name : IJsonOption.KEYWORDS.getFrom(source, jsonObject)) {
				keywords.add(Keyword.forName(name));
			}
		}

		return this;

	}

}
