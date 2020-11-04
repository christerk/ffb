package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public abstract class Player<T extends Position> implements IXmlSerializable, IJsonSerializable {

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

  public abstract String getName();

  public abstract PlayerType getPlayerType();

  abstract void setType(PlayerType pType);
  
  public abstract int getNr();
  
  public abstract int getAgility();

  public abstract void setAgility(int pAgility);
  
  public abstract int getArmour();
  
  public abstract void setArmour(int pArmour);

  public abstract int getMovement();

  public abstract void setMovement(int pMovement);

  public abstract int getStrength();
  
  public abstract void setStrength(int pStrength);

  abstract void addLastingInjury(SeriousInjury pLastingInjury) ;
  
  public abstract SeriousInjury[] getLastingInjuries();
  
  abstract void addSkill(Skill pSkill);
  
  abstract boolean removeSkill(Skill pSkill);
 
  public abstract boolean hasSkill(Skill pSkill);

  public abstract Skill[] getSkills();
  
  public abstract String getUrlPortrait();
  
  abstract void setUrlPortrait(String pUrlPortrait);
  
  public abstract String getUrlIconSet();
  
  abstract void setUrlIconSet(String pUrlIconSet);
  
  abstract int getNrOfIcons();
  
  abstract void setNrOfIcons(int pNrOfIcons);

  public abstract T getPosition();
  
  abstract void updatePosition(RosterPosition pPosition) ;
  
  public abstract Team getTeam();
  
  public abstract void setTeam(Team pTeam);
  
  public abstract String getId();
  
  abstract void setId(String pId);
  
  public abstract PlayerGender getPlayerGender();
  
  public abstract SeriousInjury getRecoveringInjury();
  
  abstract void setRecoveringInjury(SeriousInjury pCurrentInjury);
  
  public abstract int getCurrentSpps();
  
  abstract void setCurrentSpps(int pCurrentSpps);

  abstract void setName(String name);

  abstract void setGender(PlayerGender gender);

  abstract void setNr(int nr);

  public abstract int getIconSetIndex();
  
  public abstract String getPositionId();
  
  abstract void setPositionId(String pPositionId);
  
  public abstract String getRace();
  
  public abstract void init(RosterPlayer pPlayer);

  public abstract JsonObject toJsonValue();

  public static Player<?> getFrom(JsonValue jsonValue) {
    Player<?> player = createPlayer(jsonValue);
    player.initFrom(jsonValue);
    return player;
  }

  private static Player<?> createPlayer(JsonValue jsonValue) {
    if (jsonValue instanceof JsonObject &&
      ZappedPlayer.KIND.equals(IJsonOption.PLAYER_KIND.getFrom((JsonObject)jsonValue))) {
      return new ZappedPlayer();
    }
    return new RosterPlayer();
  }
}
