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

  public abstract int completionSpp(Team team);

  public abstract int interceptionSpp(Team team);

  public abstract int deflectionSpp(Team team);

  public abstract int catchSpp(Team team);

  public abstract int landingSpp(Team team);

  public abstract int additionalCompletionSpp(Team team);

  public abstract int additionalCasualtySpp(Team team);
  
  public abstract int additionalCatchSpp(Team team);

  public abstract void addCompletion(Set<String> additionalCompletionSppTeams, PlayerResult playerResult);

  public abstract void addCasualty(Set<String> additionalCasualtySppTeams, PlayerResult playerResult);

  public abstract void addCatch(Set<String> additionalCatchSppTeams, PlayerResult playerResult);

  public abstract void addLanding(PlayerResult playerResult);
  
}
