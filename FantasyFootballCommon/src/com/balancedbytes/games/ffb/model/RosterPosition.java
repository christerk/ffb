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

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerGenderFactory;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.PlayerTypeFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.SkillCategoryFactory;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
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
public class RosterPosition implements IXmlSerializable, IByteArrayReadable, IJsonSerializable {
  
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
    
  public PlayerType getType() {
    return fType;
  }
  
  public void setGender(PlayerGender pGender) {
    fGender = pGender;
  }
  
  public PlayerGender getGender() {
    return fGender;
  }
  
  public int getAgility() {
    return fAgility;
  }

  public int getArmour() {
    return fArmour;
  }

  public int getMovement() {
    return fMovement;
  }

  public int getCost() {
    return fCost;
  }
  
  public String getName() {
    return fName;
  }
  
  public void setName(String name) {
    fName = name;
  }
  
  public void setShorthand(String pShorthand) {
    fShorthand = pShorthand;
  }
  
  public String getShorthand() {
    return fShorthand;
  }

  public int getStrength() {
    return fStrength;
  }

  public SkillCategory[] getSkillCategories(boolean pOnDouble) {
    if (pOnDouble) {
      return fSkillCategoriesOnDoubleRoll.toArray(new SkillCategory[fSkillCategoriesOnDoubleRoll.size()]);
    } else {
      return fSkillCategoriesOnNormalRoll.toArray(new SkillCategory[fSkillCategoriesOnNormalRoll.size()]);
    }
  }
  
  public boolean isDoubleCategory(SkillCategory pSkillCategory) {
    return fSkillCategoriesOnDoubleRoll.contains(pSkillCategory);
  }

  public boolean hasSkill(Skill pSkill) {
    return fSkillValues.containsKey(pSkill);
  }
  
  public Skill[] getSkills() {
    return fSkillValues.keySet().toArray(new Skill[fSkillValues.size()]);
  }
  
  public int getSkillValue(Skill pSkill) {
    Integer value = fSkillValues.get(pSkill);
    return (value != null) ? value : 0; 
  }

  public String getUrlPortrait() {
    return fUrlPortrait;
  }

  public void setUrlPortrait(String pUrlPortrait) {
    fUrlPortrait = pUrlPortrait;
  }
  
  public String getUrlIconSet() {
    return fUrlIconSet;
  }
  
  public void setUrlIconSet(String pUrlIconSet) {
    fUrlIconSet = pUrlIconSet;
  }

  public int getQuantity() {
    return fQuantity;
  }
  
  public Roster getRoster() {
    return fRoster;
  }
  
  protected void setRoster(Roster pRoster) {
    fRoster = pRoster;
  }
  
  public String getId() {
    return fId;
  }
  
  public int getNrOfIcons() {
    return fNrOfIcons;
  }
  
  public void setNrOfIcons(int pNrOfIcons) {
    fNrOfIcons = pNrOfIcons;
  }
  
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
  
  public String getDisplayName() {
    return fDisplayName;
  }
 
  public void setDisplayName(String pDisplayName) {
    fDisplayName = pDisplayName;
  }
  
  public String getRace() {
    return fRace;
  }
  
  public void setRace(String pRace) {
    fRace = pRace;
  }
  
  public boolean isUndead() {
		return fUndead;
	}
  
  public void setUndead(boolean pUndead) {
  	fUndead = pUndead;
  }
  
  public boolean isThrall() {
		return fThrall;
	}
  
  public void setThrall(boolean pThrall) {
		fThrall = pThrall;
	}
  
  public void setTeamWithPositionId(String pTeamWithPositionId) {
	  fTeamWithPositionId = pTeamWithPositionId;
  }
  
  public String getTeamWithPositionId() {
	  return fTeamWithPositionId;
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
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
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
  
  public boolean endXmlElement(String pTag, String pValue) {
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
        Skill skill = new SkillFactory().forName(pValue);
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
    }
    return complete;
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 5;
  }
  
  public void addTo(ByteList pByteList) {
    
    pByteList.addSmallInt(getByteArraySerializationVersion());
    
    pByteList.addString(getId());
    pByteList.addString(getName());
    pByteList.addString(getShorthand());
    pByteList.addString(getDisplayName());
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0));
    pByteList.addByte((byte) ((getGender() != null) ? getGender().getId() : 0));
    pByteList.addByte((byte) getQuantity());
    pByteList.addByte((byte) getMovement());
    pByteList.addByte((byte) getStrength());
    pByteList.addByte((byte) getAgility());
    pByteList.addByte((byte) getArmour());
    pByteList.addInt(getCost());
    pByteList.addString(getRace());

    pByteList.addString(fUrlPortrait);
    pByteList.addString(fUrlIconSet);
    pByteList.addByte((byte) fNrOfIcons); 

    // SkillCategories Normal
    SkillCategory[] skillCategories = getSkillCategories(false);
    byte[] idBytes = new byte[skillCategories.length];
    for (int j = 0; j < skillCategories.length; j++) {
      idBytes[j] = (byte) skillCategories[j].getId();
    }
    pByteList.addByteArray(idBytes);
    
    // SkillCategories Double
    skillCategories = getSkillCategories(true);
    idBytes = new byte[skillCategories.length];
    for (int j = 0; j < skillCategories.length; j++) {
      idBytes[j] = (byte) skillCategories[j].getId();
    }
    pByteList.addByteArray(idBytes);
    
    // Skills
    Skill[] skills = getSkills();
    idBytes = new byte[skills.length];
    for (int j = 0; j < skills.length; j++) {
      idBytes[j] = (byte) skills[j].getId();
    }
    pByteList.addByteArray(idBytes);
    
    pByteList.addByte((byte) skills.length);
    for (int j = 0; j < skills.length; j++) {
    	if (getSkillValue(skills[j]) > 0) {
    		pByteList.addBoolean(true);
    		pByteList.addSmallInt(getSkillValue(skills[j]));
    	} else {
    		pByteList.addBoolean(false);
    	}
    }
    
    pByteList.addBoolean(isUndead());
    pByteList.addBoolean(isThrall());
    pByteList.addString(getTeamWithPositionId());
    
  }
  
  public int initFrom(ByteArray pByteArray) {
    
    int byteArraySerializationVersion = pByteArray.getSmallInt(); 
    
    fId = pByteArray.getString();
    setName(pByteArray.getString());
    setShorthand(pByteArray.getString());
    setDisplayName(pByteArray.getString());
    setType(new PlayerTypeFactory().forId(pByteArray.getByte()));
    setGender(new PlayerGenderFactory().forId(pByteArray.getByte()));
    setQuantity(pByteArray.getByte());
    setMovement(pByteArray.getByte());
    setStrength(pByteArray.getByte());
    setAgility(pByteArray.getByte());
    setArmour(pByteArray.getByte());
    setCost(pByteArray.getInt());
    setRace(pByteArray.getString());
    
    fUrlPortrait = pByteArray.getString();
    fUrlIconSet = pByteArray.getString();
    fNrOfIcons = pByteArray.getByte();

    SkillCategoryFactory skillCategoryFactory = new SkillCategoryFactory();
    
    // SkillCategories Normal
    byte[] skillCategoryNormalIds = pByteArray.getByteArray();
    for (int j = 0; j < skillCategoryNormalIds.length; j++) {
      SkillCategory pSkillCategory = skillCategoryFactory.forId(skillCategoryNormalIds[j]);
      if (pSkillCategory != null) {
        fSkillCategoriesOnNormalRoll.add(pSkillCategory);
      }
    }

    // SkillCategories Double
    byte[] skillCategoryDoubleIds = pByteArray.getByteArray();
    for (int j = 0; j < skillCategoryDoubleIds.length; j++) {
      SkillCategory pSkillCategory = skillCategoryFactory.forId(skillCategoryDoubleIds[j]);
      if (pSkillCategory != null) {
        fSkillCategoriesOnDoubleRoll.add(pSkillCategory);
      }
    }
    
    // Skills
    byte[] skillIds = pByteArray.getByteArray();
    SkillFactory skillFactory = new SkillFactory();
    for (int j = 0; j < skillIds.length; j++) {
      Skill skill = skillFactory.forId(skillIds[j]);
      if (skill != null) {
        fSkillValues.put(skill, null);
      }
    }

    if (byteArraySerializationVersion > 1) {
    	int nrOfSkills = pByteArray.getByte();
    	for (int j = 0; j < nrOfSkills; j++) {
    		if (pByteArray.getBoolean()) {
          Skill skill = skillFactory.forId(skillIds[j]);
    			fSkillValues.put(skill, pByteArray.getSmallInt());
    		}
	    }
    }
    
    if (byteArraySerializationVersion > 2) {
    	setUndead(pByteArray.getBoolean());
    	setThrall(pByteArray.getBoolean());
    }
    
    if (byteArraySerializationVersion > 3) {
    	setTeamWithPositionId(pByteArray.getString());
    }
    
    return byteArraySerializationVersion;

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
  
  public RosterPosition initFrom(JsonValue pJsonValue) {
    
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

    fId = IJsonOption.POSITION_ID.getFrom(jsonObject);
    fName = IJsonOption.POSITION_NAME.getFrom(jsonObject);
    fShorthand = IJsonOption.SHORTHAND.getFrom(jsonObject);
    fDisplayName = IJsonOption.DISPLAY_NAME.getFrom(jsonObject);
    fType = (PlayerType) IJsonOption.PLAYER_TYPE.getFrom(jsonObject);
    fGender = (PlayerGender) IJsonOption.PLAYER_GENDER.getFrom(jsonObject);
    fQuantity = IJsonOption.QUANTITY.getFrom(jsonObject);
    fMovement = IJsonOption.MOVEMENT.getFrom(jsonObject);
    fStrength = IJsonOption.STRENGTH.getFrom(jsonObject);
    fAgility = IJsonOption.AGILITY.getFrom(jsonObject);
    fArmour = IJsonOption.ARMOUR.getFrom(jsonObject);
    fCost = IJsonOption.COST.getFrom(jsonObject);
    fRace = IJsonOption.RACE.getFrom(jsonObject);
    fUndead = IJsonOption.UNDEAD.getFrom(jsonObject);
    fThrall = IJsonOption.THRALL.getFrom(jsonObject);
    fTeamWithPositionId = IJsonOption.TEAM_WITH_POSITION_ID.getFrom(jsonObject);

    fUrlPortrait = IJsonOption.URL_PORTRAIT.getFrom(jsonObject);
    fUrlIconSet = IJsonOption.URL_ICON_SET.getFrom(jsonObject);
    fNrOfIcons = IJsonOption.NR_OF_ICONS.getFrom(jsonObject);
    
    SkillCategoryFactory skillCategoryFactory = new SkillCategoryFactory();

    fSkillCategoriesOnNormalRoll.clear();
    JsonArray skillCategoriesNormal = IJsonOption.SKILL_CATEGORIES_NORMAL.getFrom(jsonObject);
    for (int i = 0; i < skillCategoriesNormal.size(); i++) {
      fSkillCategoriesOnNormalRoll.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesNormal.get(i)));
    }
    fSkillCategoriesOnDoubleRoll.clear();
    JsonArray skillCategoriesDouble = IJsonOption.SKILL_CATEGORIES_DOUBLE.getFrom(jsonObject);
    for (int i = 0; i < skillCategoriesDouble.size(); i++) {
      fSkillCategoriesOnDoubleRoll.add((SkillCategory) UtilJson.toEnumWithName(skillCategoryFactory, skillCategoriesDouble.get(i)));
    }
    
    fSkillValues.clear();
    JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(jsonObject);
    int[] skillValues = IJsonOption.SKILL_VALUES.getFrom(jsonObject);
    if ((skillArray != null) && (skillArray.size() > 0) && ArrayTool.isProvided(skillValues)) {
      SkillFactory skillFactory = new SkillFactory();
      for (int i = 0; i < skillArray.size(); i++) {
        Skill skill = (Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i));
        fSkillValues.put(skill, skillValues[i]);
      }
    }

    return this;
    
  }

}
