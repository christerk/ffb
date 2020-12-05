package com.balancedbytes.games.ffb.server.model;

import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.step.IStep;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class SkillBehaviour<T extends Skill> implements ISkillBehaviour {

  public T skill;

  @SuppressWarnings("unchecked")
  public final Class<T> skillClass = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  
  private List<PlayerModifier> playerModifiers;
  private List<StepModifier<? extends IStep, ?>> stepModifiers;
  
  public SkillBehaviour() {
    playerModifiers = new ArrayList<>();
    stepModifiers = new ArrayList<>();
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
