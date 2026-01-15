package com.fumbbl.ffb.mechanics;

import java.util.Set;

import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;

public abstract class SppMechanic implements Mechanic {

  @Override
  public Type getType() {
    return Type.SPP;
  }

  public abstract int mvpSpp();

  public abstract int touchdownSpp(Team team);

  public abstract int casualtySpp(Team team);

  public abstract int completionSpp();

  public abstract int interceptionSpp();

  public abstract int deflectionSpp();

  public abstract int catchSpp();

  public abstract int landingSpp();

  public abstract int additionalCompletionSpp();

  public abstract int additionalCasualtySpp();
  
  public abstract int additionalCatchSpp();

  public abstract void addCompletion(Set<String> additionalCompletionSppTeams, PlayerResult playerResult);

  public abstract void addCasualty(Set<String> additionalCasualtySppTeams, PlayerResult playerResult);

  public abstract void addCatch(Set<String> additionalCatchSppTeams, PlayerResult playerResult);

  public abstract void addLanding(PlayerResult playerResult);
  
}
