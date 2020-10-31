package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;

public class Skill implements INamedObject {

  private String name;
  private SkillCategory category;
  private List<PlayerModifier> playerModifiers;
  private List<PassModifier> passModifiers;
  private List<PickupModifier> pickupModifiers;
  private List<DodgeModifier> dodgeModifiers;
  private List<ISkillBehaviour> behaviours;
  private List<ISkillProperty> skillProperties;
  
  public Skill(String name, SkillCategory category) {
    this.name = name;
    this.category = category;
    behaviours = new ArrayList<ISkillBehaviour>();
    skillProperties = new ArrayList<ISkillProperty>();
    playerModifiers = new ArrayList<PlayerModifier>();
    passModifiers = new ArrayList<PassModifier>();
    pickupModifiers = new ArrayList<PickupModifier>();
    dodgeModifiers = new ArrayList<DodgeModifier>();
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  public SkillCategory getCategory() {
    return category;
  }

  public boolean equals(Object other) {
    return name != null && other instanceof Skill && name.equals(((Skill)other).name);
  }
  
  public static Comparator<Skill> getComparator() {
    return new Comparator<Skill>() {
      public int compare(Skill a, Skill b) {
        return a.getName().compareTo(b.getName());
      }
    };
  }

  protected void registerModifier(PassModifier modifier) {
    passModifiers.add(modifier);
  }

  protected void registerModifier(PickupModifier modifier) {
    pickupModifiers.add(modifier);
  }
  
  protected void registerModifier(DodgeModifier modifier) {
    dodgeModifiers.add(modifier);
  }
  
  protected void registerModifier(PlayerModifier modifier) {
    playerModifiers.add(modifier);
  }
  
  protected void registerProperty(ISkillProperty property) {
    skillProperties.add(property);
  }

  
  public List<ISkillBehaviour> getSkillBehaviours() {
    return behaviours;
  }
  
  public List<PlayerModifier> getPlayerModifiers() {
    return playerModifiers;
  }
  
  public List<PassModifier> getPassModifiers() {
    return passModifiers;
  }

  public List<PickupModifier> getPickupModifiers() {
    return pickupModifiers;
  }

  public List<DodgeModifier> getDodgeModifiers() {
    return dodgeModifiers;
  }
  
  
  public void addBehaviour(ISkillBehaviour behaviour) {
    behaviours.add(behaviour);
  }
  
  public int getCost(Player player) {
    Position position = player.getPosition();
    if (position.hasSkill(this)) {
      return 0;
    }
    if (position.isDoubleCategory(category)) {
      return 30000;
    } else {
      return 20000;
    }
  }

  public String[] getSkillUseDescription() {
    return null;
  }

  public boolean canCancel(Skill otherSkill) {
    for (ISkillProperty skillProperty : skillProperties) {
      if (skillProperty instanceof CancelSkillProperty && ((CancelSkillProperty) skillProperty).cancelsSkill(otherSkill)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasSkillProperty(ISkillProperty property) {
    for (ISkillProperty skillProperty : skillProperties) {
      if (property.matches(skillProperty)) {
        return true;
      }
    }
    return false;
  }

  public String getConfusionMessage() {
    return "is confused";
  }
}
