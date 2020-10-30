package com.balancedbytes.games.ffb.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Position;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;

public abstract class ServerSkill extends Skill {

  private String name;
  private SkillCategory category;
  private List<PlayerModifier> playerModifiers;
  private List<StepModifier> stepModifiers;
  
  public ServerSkill(String name, SkillCategory category) {
    super(name, category);
    this.name = name;
    this.category = category;
    playerModifiers = new ArrayList<PlayerModifier>();
    stepModifiers = new ArrayList<StepModifier>();
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  public SkillCategory getCategory() {
    return category;
  }

  public boolean equals(Object other) {
    return name != null && other instanceof ServerSkill && name.equals(((ServerSkill)other).name);
  }
  
  protected void registerModifier(PlayerModifier modifier) {
    playerModifiers.add(modifier);
  }
  
  protected void registerModifier(StepModifier modifier) {
    stepModifiers.add(modifier);
  }

  
  public List<PlayerModifier> getPlayerModifiers() {
    return playerModifiers;
  }
  
  public Collection<StepModifier> getStepModifiers() {
    return stepModifiers;
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

  public StepCommandStatus applyUseSkillCommandHooks(IStep step, Object state, ClientCommandUseSkill useSkillCommand) {
    for (StepModifier<?,?> modifier : stepModifiers) {
      if (modifier.appliesTo(step)) {
        return modifier.handleCommand(step, state, useSkillCommand);
      }
    }
    return null;
  }

}
