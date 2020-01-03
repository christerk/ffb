package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;

import java.util.Arrays;
import java.util.List;

public class ZappedPosition implements Position {

  private int move = 5;
  private int strength = 1;
  private int agility = 4;
  private int armour = 4;
  private List<Skill> skills = Arrays.asList(Skill.DODGE,
      Skill.NO_HANDS,
      Skill.TITCHY,
      Skill.STUNTY,
      Skill.VERY_LONG_LEGS,
      Skill.LEAP);
  private String race = "Transmogrified Frog";
  private String shortHand = "zf";

  private Position originalPosition;

  public ZappedPosition(Position originalPosition) {
    this.originalPosition = originalPosition;
  }

  @Override
  public PlayerType getType() {
    return PlayerType.REGULAR;
  }

  @Override
  public PlayerGender getGender() {
    return PlayerGender.NEUTRAL;
  }

  @Override
  public int getAgility() {
    return agility;
  }

  @Override
  public int getArmour() {
    return armour;
  }

  @Override
  public int getMovement() {
    return move;
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public String getName() {
    return race;
  }

  @Override
  public String getShorthand() {
    return shortHand;
  }

  @Override
  public int getStrength() {
    return strength;
  }

  @Override
  public boolean hasSkill(Skill pSkill) {
    return skills.contains(pSkill);
  }

  @Override
  public Skill[] getSkills() {
    return skills.toArray(new Skill[0]);
  }

  @Override
  public int getSkillValue(Skill pSkill) {
    return 0;
  }

  @Override
  public String getUrlPortrait() {
    return null;
  }

  @Override
  public void setUrlPortrait(String pUrlPortrait) {

  }

  @Override
  public String getUrlIconSet() {
    return null;
  }

  @Override
  public int getQuantity() {
    return 0;
  }

  @Override
  public Roster getRoster() {
    return null;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public int getNrOfIcons() {
    return 0;
  }

  @Override
  public int findNextIconSetIndex() {
    return 0;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getRace() {
    return null;
  }

  @Override
  public boolean isUndead() {
    return false;
  }

  @Override
  public boolean isThrall() {
    return false;
  }

  @Override
  public String getTeamWithPositionId() {
    return null;
  }

  @Override
  public boolean isDoubleCategory(SkillCategory category) {
    return false;
  }

  @Override
  public SkillCategory[] getSkillCategories(boolean b) {
    return new SkillCategory[0];
  }
}
