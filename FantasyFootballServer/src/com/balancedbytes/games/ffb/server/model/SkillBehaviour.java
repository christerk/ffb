package com.balancedbytes.games.ffb.server.model;

import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.step.IStep;

import java.util.ArrayList;
import java.util.List;

public abstract class SkillBehaviour<T extends Skill> implements ISkillBehaviour {

  public T skill;
  public final Class<T> skillClass;
  
  private List<PlayerModifier> playerModifiers;
  private List<StepModifier<? extends IStep, ?>> stepModifiers;
  
  public SkillBehaviour(Class<T> skillClass) {
    playerModifiers = new ArrayList<>();
    stepModifiers = new ArrayList<>();
    
	  this.skillClass = skillClass;
  }
  
  public void setSkill(T skill) {
    this.skill = skill;
    this.skill.setBehaviour(this);
  }
  
  protected void registerModifier(StepModifier<?, ?> stepModifier) {
    stepModifiers.add(stepModifier);
  }

  protected void registerModifier(PlayerModifier playerModifier) {
    playerModifiers.add(playerModifier);
  }
  
  public List<StepModifier<? extends IStep, ?>> getStepModifiers() {
    return stepModifiers;
  }
}
