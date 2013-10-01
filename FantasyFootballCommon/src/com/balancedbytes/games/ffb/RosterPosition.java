package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class RosterPosition implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "position";
  
  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_STANDING = "standing";
  private static final String _XML_ATTRIBUTE_MOVING = "moving";
  private static final String _XML_ATTRIBUTE_BASE_ICON_PATH = "baseIconPath";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  
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
  
  private static final String _XML_TAG_ICON_LIST = "iconList";
  private static final String _XML_TAG_PORTRAIT = "portrait";
  private static final String _XML_TAG_AWAY = "away";
  private static final String _XML_TAG_HOME = "home";
  
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
  private String fIconUrlPortrait;
  private int fCurrentIconIndex;
  private String fBaseIconPath;
  private String fRace;
  private boolean fUndead;
  private boolean fThrall;
  private String fTeamWithPositionId;
  private List<String> fIconUrlsHomeStanding;
  private List<String> fIconUrlsHomeMoving;
  private List<String> fIconUrlsAwayStanding;
  private List<String> fIconUrlsAwayMoving;
  private Map<Skill, Integer> fSkillValues;
  private Set<SkillCategory> fSkillCategoriesOnNormalRoll;
  private Set<SkillCategory> fSkillCategoriesOnDoubleRoll;
  
  // attributes used for parsing
  private transient boolean fInsideIconListTag;
  private transient boolean fInsideSkillListTag;
  private transient boolean fInsideSkillCategoryListTag;
  private transient Integer fCurrentSkillValue;
  
  public RosterPosition(String pId) {
    fId = pId;
    fSkillValues = new LinkedHashMap<Skill, Integer>();
    fSkillCategoriesOnNormalRoll = new HashSet<SkillCategory>();
    fSkillCategoriesOnDoubleRoll = new HashSet<SkillCategory>();
    fIconUrlsHomeMoving = new ArrayList<String>();
    fIconUrlsHomeStanding = new ArrayList<String>();
    fIconUrlsAwayMoving = new ArrayList<String>();
    fIconUrlsAwayStanding = new ArrayList<String>();
    fCurrentIconIndex = -1;
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
  
  public Integer getSkillValue(Skill pSkill) {
  	return fSkillValues.get(pSkill);
  }

  public String getIconUrlPortrait() {
    return fIconUrlPortrait;
  }

  public void setIconUrlPortrait(String pIconPortrait) {
    fIconUrlPortrait = pIconPortrait;
  }

  public String getIconUrl(boolean pHome, boolean pStanding, int pIndex) {
    if (pHome) {
      if (pStanding) {
        return getIconUrl(fIconUrlsHomeStanding, pIndex);
      } else {
        return getIconUrl(fIconUrlsHomeMoving, pIndex);
      }
    } else {
      if (pStanding) {
        return getIconUrl(fIconUrlsAwayStanding, pIndex);
      } else {
        return getIconUrl(fIconUrlsAwayMoving, pIndex);
      }
    }
  }
  
  private String getIconUrl(List<String> pRelativeUrls, int pIndex) {
    if ((pRelativeUrls != null) && (pRelativeUrls.size() > 0) && (pIndex >= 0) && (pIndex < pRelativeUrls.size())) {
      return pRelativeUrls.get(pIndex);
    } else {
      return null;
    }
  }
  
//  public String[] getIconUrls(boolean pHome, boolean pStanding) {
//    if (pHome) {
//      if (pStanding) {
//        return getIconUrls(fIconUrlsHomeStanding);
//      } else {
//        return getIconUrls(fIconUrlsHomeMoving);
//      }
//    } else {
//      if (pStanding) {
//        return getIconUrls(fIconUrlsAwayStanding);
//      } else {
//        return getIconUrls(fIconUrlsAwayMoving);
//      }
//    }
//  }
  
//  private String[] getIconUrls(List<String> pRelativeUrls) {
//    if ((pRelativeUrls != null) && (pRelativeUrls.size() > 0)) {
//      String[] urls = new String[pRelativeUrls.size()];
//      for (int i = 0; i < urls.length; i++) {
//        urls[i] = getIconUrl(pRelativeUrls.get(i));
//      }
//      return urls;
//    } else {
//      return new String[0];
//    }
//  }
  
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
  
  public String getBaseIconPath() {
    return fBaseIconPath;
  }
  
  public void setBaseIconPath(String pBaseIconPath) {
    fBaseIconPath = pBaseIconPath;
  }
  
  public int getNextIconIndex() {
    if (fIconUrlsHomeStanding.size() > 0) {
      fCurrentIconIndex++;
      if (fCurrentIconIndex >= fIconUrlsHomeStanding.size()) {
        fCurrentIconIndex = 0;
      }
    }
    return fCurrentIconIndex;
  }
  
  public int getNrOfIcons() {
    return fIconUrlsHomeStanding.size();
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
    	if (getSkillValue(skill) != null) {
    		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getSkillValue(skill));
    	}
    	UtilXml.startElement(pHandler, _XML_TAG_SKILL, attributes);
    	UtilXml.addCharacters(pHandler, skill.getName());
  		UtilXml.endElement(pHandler, _XML_TAG_SKILL);
    }

    UtilXml.endElement(pHandler, _XML_TAG_SKILL_LIST);
        
  	attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BASE_ICON_PATH, getBaseIconPath());
    UtilXml.startElement(pHandler, _XML_TAG_ICON_LIST, attributes);
    
  	UtilXml.addValueElement(pHandler, _XML_TAG_PORTRAIT, getIconUrlPortrait());

    for (int i = 0; i < fIconUrlsHomeStanding.size(); i++) {
    	
    	attributes = new AttributesImpl();
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STANDING, fIconUrlsHomeStanding.get(i));
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, fIconUrlsAwayMoving.get(i));
    	UtilXml.addEmptyElement(pHandler, _XML_TAG_HOME, attributes);

    	attributes = new AttributesImpl();
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STANDING, fIconUrlsAwayStanding.get(i));
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, fIconUrlsAwayMoving.get(i));
    	UtilXml.addEmptyElement(pHandler, _XML_TAG_AWAY, attributes);

    }
		
    UtilXml.endElement(pHandler, _XML_TAG_ICON_LIST);

    UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }  
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (fInsideIconListTag) {
      if (_XML_TAG_AWAY.equals(pXmlTag)) {
        fIconUrlsAwayStanding.add(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STANDING));
        fIconUrlsAwayMoving.add(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_MOVING));
      }
      if (_XML_TAG_HOME.equals(pXmlTag)) {
        fIconUrlsHomeStanding.add(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STANDING));
        fIconUrlsHomeMoving.add(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_MOVING));
      }
    } else if (fInsideSkillListTag) {
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
      if (_XML_TAG_ICON_LIST.equals(pXmlTag)) {
        fInsideIconListTag = true;
        setBaseIconPath(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_BASE_ICON_PATH));
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
    } else {
      if (fInsideSkillListTag) {
        if (_XML_TAG_SKILL_LIST.equals(pTag)) {
          fInsideSkillListTag = false;
        }
        if (_XML_TAG_SKILL.equals(pTag)) {
          Skill skill = Skill.fromName(pValue);
          if (skill != null) {
          	fSkillValues.put(skill, fCurrentSkillValue);
          }
        }
      } else if (fInsideSkillCategoryListTag) {
        if (_XML_TAG_SKILLCATEGORY_LIST.equals(pTag)) {
          fInsideSkillCategoryListTag = false;
        }
        if (_XML_TAG_NORMAL.equals(pTag)) {
          SkillCategory pSkillCategory = SkillCategory.fromName(pValue);
          if (pSkillCategory != null) {
            fSkillCategoriesOnNormalRoll.add(pSkillCategory);
          }
        }
        if (_XML_TAG_DOUBLE.equals(pTag)) {
          SkillCategory pSkillCategory = SkillCategory.fromName(pValue);
          if (pSkillCategory != null) {
            fSkillCategoriesOnDoubleRoll.add(pSkillCategory);
          }
        }
      } else if (fInsideIconListTag) {
        if (_XML_TAG_ICON_LIST.equals(pTag)) {
          fInsideIconListTag = false;
        }
        if (_XML_TAG_PORTRAIT.equals(pTag)) {
          setIconUrlPortrait(pValue);
        }
      } else {
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
          setType(PlayerType.fromName(pValue));
        }
        if (_XML_TAG_GENDER.equals(pTag)) {
          setGender(PlayerGender.fromName(pValue));
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
    }
    return complete;
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 4;
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
    pByteList.addString(getBaseIconPath());
    pByteList.addString(fIconUrlPortrait);
    pByteList.addString(getRace());
    
    // Icons Home Standing
    pByteList.addStringArray(fIconUrlsHomeStanding.toArray(new String[fIconUrlsHomeStanding.size()]));

    // Icons Home Moving
    pByteList.addStringArray(fIconUrlsHomeMoving.toArray(new String[fIconUrlsHomeMoving.size()]));

    // Icons Away Standing
    pByteList.addStringArray(fIconUrlsAwayStanding.toArray(new String[fIconUrlsAwayStanding.size()]));
    
    // Icons Away Moving
    pByteList.addStringArray(fIconUrlsAwayMoving.toArray(new String[fIconUrlsAwayMoving.size()]));

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
    	if (getSkillValue(skills[j]) != null) {
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
    setType(PlayerType.fromId(pByteArray.getByte()));
    setGender(PlayerGender.fromId(pByteArray.getByte()));
    setQuantity(pByteArray.getByte());
    setMovement(pByteArray.getByte());
    setStrength(pByteArray.getByte());
    setAgility(pByteArray.getByte());
    setArmour(pByteArray.getByte());
    setCost(pByteArray.getInt());
    setBaseIconPath(pByteArray.getString());
    setIconUrlPortrait(pByteArray.getString());
    setRace(pByteArray.getString());
    
    // Icons Home Standing
    String[] iconUrlsHomeStanding = pByteArray.getStringArray();
    for (int j = 0; j < iconUrlsHomeStanding.length; j++) {
      fIconUrlsHomeStanding.add(iconUrlsHomeStanding[j]);
    }
    
    // Icons Home Moving
    String[] iconUrlsHomeMoving = pByteArray.getStringArray();
    for (int j = 0; j < iconUrlsHomeMoving.length; j++) {
      fIconUrlsHomeMoving.add(iconUrlsHomeMoving[j]);
    }

    // Icons Away Standing
    String[] iconUrlsAwayStanding = pByteArray.getStringArray();
    for (int j = 0; j < iconUrlsAwayStanding.length; j++) {
      fIconUrlsAwayStanding.add(iconUrlsAwayStanding[j]);
    }

    // Icons Away Moving
    String[] iconUrlsAwayMoving = pByteArray.getStringArray();
    for (int j = 0; j < iconUrlsAwayMoving.length; j++) {
      fIconUrlsAwayMoving.add(iconUrlsAwayMoving[j]);
    }

    // SkillCategories Normal
    byte[] skillCategoryNormalIds = pByteArray.getByteArray();
    for (int j = 0; j < skillCategoryNormalIds.length; j++) {
      SkillCategory pSkillCategory = SkillCategory.fromId(skillCategoryNormalIds[j]);
      if (pSkillCategory != null) {
        fSkillCategoriesOnNormalRoll.add(pSkillCategory);
      }
    }

    // SkillCategories Double
    byte[] skillCategoryDoubleIds = pByteArray.getByteArray();
    for (int j = 0; j < skillCategoryDoubleIds.length; j++) {
      SkillCategory pSkillCategory = SkillCategory.fromId(skillCategoryDoubleIds[j]);
      if (pSkillCategory != null) {
        fSkillCategoriesOnDoubleRoll.add(pSkillCategory);
      }
    }
    
    // Skills
    byte[] skillIds = pByteArray.getByteArray();
    for (int j = 0; j < skillIds.length; j++) {
      Skill skill = Skill.fromId(skillIds[j]);
      if (skill != null) {
        fSkillValues.put(skill, null);
      }
    }

    if (byteArraySerializationVersion > 1) {
    	int nrOfSkills = pByteArray.getByte();
    	for (int j = 0; j < nrOfSkills; j++) {
    		if (pByteArray.getBoolean()) {
          Skill skill = Skill.fromId(skillIds[j]);
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

}
