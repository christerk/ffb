package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class Player implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "player";
  
  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_NR = "nr";
  private static final String _XML_ATTRIBUTE_STANDING = "standing";
  private static final String _XML_ATTRIBUTE_MOVING = "moving";
  private static final String _XML_ATTRIBUTE_BASE_ICON_PATH = "baseIconPath";
  
  private static final String _XML_TAG_NAME = "name";
  private static final String _XML_TAG_TYPE = "type";
  private static final String _XML_TAG_GENDER = "gender";
  private static final String _XML_TAG_POSITION_ID = "positionId";
  
  private static final String _XML_TAG_SKILL_LIST = "skillList";
  private static final String _XML_TAG_SKILL = "skill";
  
  private static final String _XML_TAG_ICON_LIST = "iconList";
  private static final String _XML_TAG_PORTRAIT = "portrait";
  private static final String _XML_TAG_AWAY = "away";
  private static final String _XML_TAG_HOME = "home";
  
  private static final String _XML_TAG_INJURY_LIST = "injuryList";
  private static final String _XML_TAG_INJURY = "injury";
  private static final String _XML_ATTRIBUTE_RECOVERING = "recovering";
  
  private static final String _XML_TAG_PLAYER_STATISTICS = "playerStatistics";
  private static final String _XML_ATTRIBUTE_CURRENT_SPPS = "currentSpps";

  private static final String _XML_TAG_MOVEMENT = "movement";
  private static final String _XML_TAG_STRENGTH = "strength";
  private static final String _XML_TAG_AGILITY = "agility";
  private static final String _XML_TAG_ARMOUR = "armour";
  private static final String _XML_TAG_SHORTHAND = "shorthand";
  private static final String _XML_TAG_RACE = "race";
  
  private String fId;
  private int fNr;
  private Team fTeam;
  private String fName;
  private PlayerType fType;
  private PlayerGender fGender;
  private int fMovement;
  private int fStrength;
  private int fAgility;
  private int fArmour;
  
  private String fBaseIconPath;
  private String fIconUrlPortrait;
  private String fIconUrlStandingHome;
  private String fIconUrlMovingHome;
  private String fIconUrlStandingAway;
  private String fIconUrlMovingAway;
  
  private String fPositionId;
  private RosterPosition fPosition;
  private transient int fPositionIconIndex;

  private List<Skill> fSkills;
  private List<SeriousInjury> fLastingInjuries;
  private SeriousInjury fRecoveringInjury;
  
  private transient int fCurrentSpps;
  private transient boolean fInsideIconList;
  private transient boolean fInsideSkillList;
  private transient boolean fInsideInjuryList;
  private transient boolean fInjuryCurrent;
    
  public Player() {
    fLastingInjuries = new ArrayList<SeriousInjury>();
    fSkills = new ArrayList<Skill>();
    setGender(PlayerGender.MALE);
    fPositionIconIndex = -1;
    fPosition = new RosterPosition(null);
  }
    
  public String getName() {
    return fName;
  }
  
  public PlayerType getType() {
    return fType;
  }
  
  public void setType(PlayerType pType) {
    fType = pType;
  }
  
  public int getNr() {
    return fNr;
  }
  
  public int getAgility() {
    return fAgility;
  }
  
  public void setAgility(int pAgility) {
    fAgility = pAgility;
  }
  
  public int getArmour() {
    return fArmour;
  }
  
  public void setArmour(int pArmour) {
    fArmour = pArmour;
  }

  public int getMovement() {
    return fMovement;
  }
  
  public void setMovement(int pMovement) {
    fMovement = pMovement;
  }

  public int getStrength() {
    return fStrength;
  }
  
  public void setStrength(int pStrength) {
    fStrength = pStrength;
  }

  public void addLastingInjury(SeriousInjury pLastingInjury) {
  	if (pLastingInjury != null) {
  		fLastingInjuries.add(pLastingInjury);
  	}
  }
  
  public SeriousInjury[] getLastingInjuries() {
    return fLastingInjuries.toArray(new SeriousInjury[fLastingInjuries.size()]);
  }
  
  public void addSkill(Skill pSkill) {
    if ((pSkill != null) && ((pSkill.getCategory() == SkillCategory.STAT_INCREASE) || !fSkills.contains(pSkill))) {
      fSkills.add(pSkill);
    }
  }
  
  public boolean removeSkill(Skill pSkill) {
    return fSkills.remove(pSkill);
  }
 
  public boolean hasSkill(Skill pSkill) {
    return fSkills.contains(pSkill);
  }

  public Skill[] getSkills() {
    return fSkills.toArray(new Skill[fSkills.size()]);
  }
  
  public String getIconUrlPortrait() {
    return fIconUrlPortrait;
  }

  public String getIconUrl(boolean pHome, boolean pStanding) {
    if (pHome) {
      if (pStanding) {
        return fIconUrlStandingHome;
      } else {
        return fIconUrlMovingHome;
      }
    } else {
      if (pStanding) {
        return fIconUrlStandingAway;
      } else {
        return fIconUrlMovingAway;
      }
    }
  }

  public RosterPosition getPosition() {
    return fPosition;
  }
  
  public void updatePosition(RosterPosition pPosition) {
    fPosition = pPosition;
    if (fPosition != null) {
      setPositionId(fPosition.getId());
      if (getType() == null) {
        setType(fPosition.getType());
      }
      if (fPosition.getGender() != null) {
        setGender(fPosition.getGender());
      }
      setMovement(fPosition.getMovement());
      setStrength(fPosition.getStrength());
      setAgility(fPosition.getAgility());
      setArmour(fPosition.getArmour());
      fPositionIconIndex = pPosition.getNextIconIndex();
      for (Skill skill : fPosition.getSkills()) {
        addSkill(skill);
      }
      for (Skill skill : getSkills()) {
        switch (skill) {
          case MOVEMENT_INCREASE:
            fMovement++;
            break;
          case STRENGTH_INCREASE:
            fStrength++;
            break;
          case AGILITY_INCREASE:
            fAgility++;
            break;
          case ARMOUR_INCREASE:
            fArmour++;
            break;
          default:
          	break;
        }
      }
      int oldMovement = getMovement();
      int oldArmour = getArmour();
      int oldAgility = getAgility();
      int oldStrength = getStrength();
      for (SeriousInjury injury : getLastingInjuries()) {
        switch (injury) {
          case SMASHED_HIP:
          case SMASHED_ANKLE:
            if ((fMovement > 1) && ((oldMovement - fMovement) < 2)) {
              fMovement--;
            }
            break;
          case SERIOUS_CONCUSSION:
          case FRACTURED_SKULL:
            if ((fArmour > 1) && ((oldArmour - fArmour) < 2)) {
              fArmour--;
            }
            break;
          case BROKEN_NECK:
            if ((fAgility > 1) && ((oldAgility - fAgility) < 2)) {
              fAgility--;
            }
            break;
          case SMASHED_COLLAR_BONE:
            if ((fStrength > 1) && ((oldStrength - fStrength) < 2)) {
              fStrength--;
            }
            break;
          default:
          	break;
        }
      }
    }
  }
  
  public Team getTeam() {
    return fTeam;
  }
  
  public void setTeam(Team pTeam) {
    fTeam = pTeam;
  }
  
  public String getId() {
    return fId;
  }
  
  public void setId(String pId) {
    fId = pId;
  }
  
  public PlayerGender getGender() {
    return fGender;
  }
  
  public String getBaseIconPath() {
    return fBaseIconPath;
  }
  
  public void setBaseIconPath(String pBaseIconPath) {
    fBaseIconPath = pBaseIconPath;
  }
  
  public SeriousInjury getRecoveringInjury() {
    return fRecoveringInjury;
  }
  
  public void setRecoveringInjury(SeriousInjury pCurrentInjury) {
    fRecoveringInjury = pCurrentInjury;
  }
  
  public int getCurrentSpps() {
    return fCurrentSpps;
  }
  
  public void setCurrentSpps(int pCurrentSpps) {
    fCurrentSpps = pCurrentSpps;
  }
  

  public void setName(String name) {
    fName = name;
  }

  public void setGender(PlayerGender gender) {
    fGender = gender;
  }

  public void setNr(int nr) {
    fNr = nr;
  }

  public void setIconUrlPortrait(String iconUrlPortrait) {
    fIconUrlPortrait = iconUrlPortrait;
  }

  public void setIconUrlStandingHome(String iconUrlStandingHome) {
    fIconUrlStandingHome = iconUrlStandingHome;
  }

  public void setIconUrlMovingHome(String iconUrlMovingHome) {
    fIconUrlMovingHome = iconUrlMovingHome;
  }

  public void setIconUrlStandingAway(String iconUrlStandingAway) {
    fIconUrlStandingAway = iconUrlStandingAway;
  }

  public void setIconUrlMovingAway(String iconUrlMovingAway) {
    fIconUrlMovingAway = iconUrlMovingAway;
  }
  
  public int getPositionIconIndex() {
    return fPositionIconIndex;
  }
  
  public String getPositionId() {
    return fPositionId;
  }
  
  public void setPositionId(String pPositionId) {
    fPositionId = pPositionId;
  }
  
  public String getRace() {
    return getPosition().getRace();
  }
  
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Player other = (Player) obj;
    return getId().equals(other.getId());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {

  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR, getNr());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);

    UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
    UtilXml.addValueElement(pHandler, _XML_TAG_GENDER, (getGender() != null) ? getGender().getName() : null); 
    UtilXml.addValueElement(pHandler, _XML_TAG_POSITION_ID, getPositionId());
    UtilXml.addValueElement(pHandler, _XML_TAG_TYPE, (getType() != null) ? getType().getName() : null);

    UtilXml.startElement(pHandler, _XML_TAG_SKILL_LIST);
    if (fSkills.size() > 0) {
      for (Skill skill : fSkills) {
        UtilXml.addValueElement(pHandler, _XML_TAG_SKILL, skill.getName());
      }
    }
    UtilXml.endElement(pHandler, _XML_TAG_SKILL_LIST);
    
    UtilXml.startElement(pHandler, _XML_TAG_INJURY_LIST);
    if (fLastingInjuries.size() > 0) {
      for (SeriousInjury lastingInjury : fLastingInjuries) {
        UtilXml.addValueElement(pHandler, _XML_TAG_INJURY, lastingInjury.getName());
      }
    }
    UtilXml.endElement(pHandler, _XML_TAG_INJURY_LIST);

  	attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BASE_ICON_PATH, getBaseIconPath());
    UtilXml.startElement(pHandler, _XML_TAG_ICON_LIST, attributes);
    if (StringTool.isProvided(getIconUrlPortrait()) || StringTool.isProvided(fIconUrlStandingHome)) {
      if (StringTool.isProvided(getIconUrlPortrait()) && !getIconUrlPortrait().equals(getPosition().getIconUrlPortrait())) {
      	UtilXml.addValueElement(pHandler, _XML_TAG_PORTRAIT, getIconUrlPortrait());
      }
    	attributes = new AttributesImpl();
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STANDING, fIconUrlStandingHome);
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, fIconUrlMovingHome);
    	UtilXml.addEmptyElement(pHandler, _XML_TAG_HOME, attributes);
    	attributes = new AttributesImpl();
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STANDING, fIconUrlStandingAway);
    	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, fIconUrlMovingAway);
    	UtilXml.addEmptyElement(pHandler, _XML_TAG_AWAY, attributes);
    }
    UtilXml.endElement(pHandler, _XML_TAG_ICON_LIST);
    
    UtilXml.endElement(pHandler, XML_TAG);
    
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (fInsideIconList) {
      if (_XML_TAG_AWAY.equals(pXmlTag)) {
        setIconUrlStandingAway(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STANDING));
        setIconUrlMovingAway(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_MOVING));
      }
      if (_XML_TAG_HOME.equals(pXmlTag)) {
        setIconUrlStandingHome(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STANDING));
        setIconUrlMovingHome(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_MOVING));
      }
    } else if (fInsideInjuryList) {
      if (_XML_TAG_INJURY.equals(pXmlTag)) {
        fInjuryCurrent = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_RECOVERING);
      }
    } else {
      if (XML_TAG.equals(pXmlTag)) {
        fId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
        setNr(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NR));
      }
      if (_XML_TAG_INJURY_LIST.equals(pXmlTag)) {
        fInsideInjuryList = true;
      }
      if (_XML_TAG_ICON_LIST.equals(pXmlTag)) {
        fInsideIconList = true;
        setBaseIconPath(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_BASE_ICON_PATH));
      }
      if (_XML_TAG_SKILL_LIST.equals(pXmlTag)) {
        fInsideSkillList = true;
      }
      if (_XML_TAG_PLAYER_STATISTICS.equals(pXmlTag)) {
        setCurrentSpps(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_CURRENT_SPPS));
      }
    }
    return xmlElement;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    boolean complete = XML_TAG.equals(pXmlTag);
    if (!complete) {
      if (fInsideSkillList) {
        if (_XML_TAG_SKILL_LIST.equals(pXmlTag)) {
          fInsideSkillList = false;
        }
        if (_XML_TAG_SKILL.equals(pXmlTag)) {
          Skill skill = new SkillFactory().forName(pValue);
          if (skill != null) {
            fSkills.add(skill);
          }
        }
      } else if (fInsideInjuryList) {
        if (_XML_TAG_INJURY_LIST.equals(pXmlTag)) {
          fInsideInjuryList = false;
        }
        if (_XML_TAG_INJURY.equals(pXmlTag)) {
          SeriousInjury injury = new SeriousInjuryFactory().forName(pValue);
          if (injury != null) {
            fLastingInjuries.add(injury);
            if (fInjuryCurrent) {
              fRecoveringInjury = injury;
            }
          }
        }
      } else if (fInsideIconList) {
        if (_XML_TAG_ICON_LIST.equals(pXmlTag)) {
          fInsideIconList = false;
        }
        if (_XML_TAG_PORTRAIT.equals(pXmlTag)) {
          setIconUrlPortrait(pValue);
        }
      } else {
        if (_XML_TAG_NAME.equals(pXmlTag)) {
          setName(pValue);
        }
        if (_XML_TAG_GENDER.equals(pXmlTag)) {
          setGender(PlayerGender.fromName(pValue));
          if (getGender() == null) {
            setGender(PlayerGender.MALE);
          }
        }
        if (_XML_TAG_POSITION_ID.equals(pXmlTag)) {
          setPositionId(pValue);
        }
        if (_XML_TAG_TYPE.equals(pXmlTag)) {
          setType(PlayerType.fromName(pValue));
        }
        // attributes for special player definitions (without rosterPosition)
        if (_XML_TAG_MOVEMENT.equals(pXmlTag)) {
          setMovement(Integer.parseInt(pValue));
        }
        if (_XML_TAG_STRENGTH.equals(pXmlTag)) {
          setStrength(Integer.parseInt(pValue));
        }
        if (_XML_TAG_AGILITY.equals(pXmlTag)) {
          setAgility(Integer.parseInt(pValue));
        }
        if (_XML_TAG_ARMOUR.equals(pXmlTag)) {
          setArmour(Integer.parseInt(pValue));
        }
        if (_XML_TAG_RACE.equals(pXmlTag)) {
        	getPosition().setRace(pValue);
        }
        if (_XML_TAG_SHORTHAND.equals(pXmlTag)) {
        	getPosition().setShorthand(pValue);
        }
      }
    }
    return complete;
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
    
  public void addTo(ByteList pByteList) {
    
    pByteList.addString(getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());    
    pByteList.addByte((byte) getNr());
    pByteList.addString(getPositionId());
    pByteList.addString(getName());
    pByteList.addByte((byte) ((getGender() != null) ? getGender().getId() : 0));
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0));

    pByteList.addByte((byte) getMovement());
    pByteList.addByte((byte) getStrength());
    pByteList.addByte((byte) getAgility());
    pByteList.addByte((byte) getArmour());
    
    pByteList.addByte((byte) fLastingInjuries.size());
    if (fLastingInjuries.size() > 0) {
      for (SeriousInjury injury : fLastingInjuries) {
        pByteList.addByte((byte) injury.getId());
      }
    }
    pByteList.addByte((byte) ((getRecoveringInjury() != null) ? getRecoveringInjury().getId() : 0));
    
    pByteList.addString(getBaseIconPath());
    pByteList.addString(fIconUrlPortrait);
    pByteList.addByte((byte) getPositionIconIndex());
    pByteList.addString(fIconUrlStandingHome);
    pByteList.addString(fIconUrlMovingHome);
    pByteList.addString(fIconUrlStandingAway);
    pByteList.addString(fIconUrlMovingAway);

    Skill[] skills = getSkills();
    byte[] idBytes = new byte[skills.length];
    for (int j = 0; j < skills.length; j++) {
      idBytes[j] = (byte) skills[j].getId();
    }
    pByteList.addByteArray(idBytes);
    
  }
  
  public void init(Player pPlayer) {
    if (pPlayer != null) {
      
      setMovement(pPlayer.getMovement());
      setStrength(pPlayer.getStrength());
      setAgility(pPlayer.getAgility());
      setArmour(pPlayer.getArmour());
      
      fLastingInjuries.clear();
      for (SeriousInjury injury : pPlayer.getLastingInjuries()) {
        addLastingInjury(injury);
      }
      setRecoveringInjury(pPlayer.getRecoveringInjury());

      setBaseIconPath(pPlayer.getBaseIconPath());
      setIconUrlPortrait(pPlayer.getIconUrlPortrait());
      setIconUrlStandingHome(pPlayer.getIconUrl(true, true));
      setIconUrlMovingHome(pPlayer.getIconUrl(true, false));
      setIconUrlStandingAway(pPlayer.getIconUrl(false, true));
      setIconUrlMovingAway(pPlayer.getIconUrl(false, false));

      fSkills.clear();
      for (Skill skill : pPlayer.getSkills()) {
        addSkill(skill);
      }      
    }
  }
      
  public int initFrom(ByteArray pByteArray) {
    
    fId = pByteArray.getString();
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setNr(pByteArray.getByte());
    setPositionId(pByteArray.getString());
    setName(pByteArray.getString());
    setGender(PlayerGender.fromId(pByteArray.getByte()));
    setType(PlayerType.fromId(pByteArray.getByte()));

    setMovement(pByteArray.getByte());
    setStrength(pByteArray.getByte());
    setAgility(pByteArray.getByte());
    setArmour(pByteArray.getByte());

    int nrOfLastingInjuries = pByteArray.getByte();
    SeriousInjuryFactory seriousInjuryFactory = new SeriousInjuryFactory();
    for (int i = 0; i < nrOfLastingInjuries; i++) {
      addLastingInjury(seriousInjuryFactory.forId(pByteArray.getByte()));
    }
    fRecoveringInjury = seriousInjuryFactory.forId(pByteArray.getByte());

    setBaseIconPath(pByteArray.getString());
    setIconUrlPortrait(pByteArray.getString());
    fPositionIconIndex = pByteArray.getByte();
    setIconUrlStandingHome(pByteArray.getString());
    setIconUrlMovingHome(pByteArray.getString());
    setIconUrlStandingAway(pByteArray.getString());
    setIconUrlMovingAway(pByteArray.getString());

    byte[] skillIds = pByteArray.getByteArray();
    SkillFactory skillFactory = new SkillFactory();
    for (int j = 0; j < skillIds.length; j++) {
      addSkill(skillFactory.forId(skillIds[j]));
    }
    
    return byteArraySerializationVersion;

  }
    
}
