package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;

public interface Position {
  PlayerType getType();

  PlayerGender getGender();

  int getAgility();

  int getArmour();

  int getMovement();

  int getCost();

  String getName();

  String getShorthand();

  int getStrength();

  boolean hasSkill(Skill pSkill);

  Skill[] getSkills();

  int getSkillValue(Skill pSkill);

  String getUrlPortrait();

  void setUrlPortrait(String pUrlPortrait);

  String getUrlIconSet();

  int getQuantity();

  Roster getRoster();

  String getId();

  int getNrOfIcons();

  int findNextIconSetIndex();

  String getDisplayName();

  String getRace();

  boolean isUndead();

  boolean isThrall();

  String getTeamWithPositionId();

  boolean isDoubleCategory(SkillCategory category);

  SkillCategory[] getSkillCategories(boolean b);
}
