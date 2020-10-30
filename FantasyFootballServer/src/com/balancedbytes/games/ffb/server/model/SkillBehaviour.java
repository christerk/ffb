package com.balancedbytes.games.ffb.server.model;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;

public class SkillBehaviour<T extends Skill> implements ISkillBehaviour {

  public T skill;
  public final Class<? extends Skill> skillClass;
  
  private List<PlayerModifier> playerModifiers;
  private List<StepModifier> stepModifiers;
  
  @SuppressWarnings("unchecked")
  public SkillBehaviour() {
    playerModifiers = new ArrayList<PlayerModifier>();
    stepModifiers = new ArrayList<StepModifier>();
    
    skillClass = (Class<? extends Skill>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0].getClass();
  }
  
  @SuppressWarnings("unchecked")
  public void setSkill(Skill skill) {
    this.skill = (T) skill;
    this.skill.addBehaviour(this);
  }
  
  protected void registerModifier(StepModifier<?, ?> stepModifier) {
    stepModifiers.add(stepModifier);
  }

  protected void registerModifier(PlayerModifier playerModifier) {
    playerModifiers.add(playerModifier);
  }
  
  public List<StepModifier> getStepModifiers() {
    return stepModifiers;
  }
}
