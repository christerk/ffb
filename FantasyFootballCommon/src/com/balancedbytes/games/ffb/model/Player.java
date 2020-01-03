package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.eclipsesource.json.JsonObject;



/**
 * 
 * @author Kalimar
 */
public interface Player<T extends Position> extends IXmlSerializable, IJsonSerializable {
  
  static final String XML_TAG = "player";
  
  static final String _XML_ATTRIBUTE_ID = "id";

  static final String _XML_ATTRIBUTE_NR = "nr";
  static final String _XML_ATTRIBUTE_SIZE = "size";
  
  static final String _XML_TAG_NAME = "name";
  static final String _XML_TAG_TYPE = "type";
  static final String _XML_TAG_GENDER = "gender";
  static final String _XML_TAG_POSITION_ID = "positionId";
  
  static final String _XML_TAG_SKILL_LIST = "skillList";
  static final String _XML_TAG_SKILL = "skill";
  
  static final String _XML_TAG_ICON_SET = "iconSet";
  static final String _XML_TAG_PORTRAIT = "portrait";
  
  static final String _XML_TAG_INJURY_LIST = "injuryList";
  static final String _XML_TAG_INJURY = "injury";
  static final String _XML_ATTRIBUTE_RECOVERING = "recovering";
  
  static final String _XML_TAG_PLAYER_STATISTICS = "playerStatistics";
  static final String _XML_ATTRIBUTE_CURRENT_SPPS = "currentSpps";

  static final String _XML_TAG_MOVEMENT = "movement";
  static final String _XML_TAG_STRENGTH = "strength";
  static final String _XML_TAG_AGILITY = "agility";
  static final String _XML_TAG_ARMOUR = "armour";
  static final String _XML_TAG_SHORTHAND = "shorthand";
  static final String _XML_TAG_RACE = "race";

  String getName();
  
  PlayerType getPlayerType();
  
  void setType(PlayerType pType);
  
  int getNr();
  
  int getAgility();

  void setAgility(int pAgility);
  
  int getArmour();
  
  void setArmour(int pArmour);

  int getMovement();

  void setMovement(int pMovement);

  int getStrength();
  
  void setStrength(int pStrength);

  void addLastingInjury(SeriousInjury pLastingInjury) ;
  
  SeriousInjury[] getLastingInjuries();
  
  void addSkill(Skill pSkill);
  
  boolean removeSkill(Skill pSkill);
 
  boolean hasSkill(Skill pSkill);

  Skill[] getSkills();
  
  String getUrlPortrait();
  
  void setUrlPortrait(String pUrlPortrait);
  
  String getUrlIconSet();
  
  void setUrlIconSet(String pUrlIconSet);
  
  int getNrOfIcons();
  
  void setNrOfIcons(int pNrOfIcons);

  T getPosition();
  
  void updatePosition(T pPosition) ;
  
  Team getTeam();
  
  void setTeam(Team pTeam);
  
  String getId();
  
  void setId(String pId);
  
  PlayerGender getPlayerGender();
  
  SeriousInjury getRecoveringInjury();
  
  void setRecoveringInjury(SeriousInjury pCurrentInjury);
  
  int getCurrentSpps();
  
  void setCurrentSpps(int pCurrentSpps);

  void setName(String name);

  void setGender(PlayerGender gender);

  void setNr(int nr);

  int getIconSetIndex();
  
  String getPositionId();
  
  void setPositionId(String pPositionId);
  
  String getRace();
  
  void init(Player pPlayer);

  JsonObject toJsonValue();
}
