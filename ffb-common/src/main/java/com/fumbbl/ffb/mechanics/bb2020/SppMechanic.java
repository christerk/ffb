package com.fumbbl.ffb.mechanics.bb2020;

import java.util.Set;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;

@RulesCollection(RulesCollection.Rules.BB2020)
public class SppMechanic extends com.fumbbl.ffb.mechanics.SppMechanic {

  @Override
  public int mvpSpp() {
    return 4;
  }

  @Override
  public int touchdownSpp(Team team) {
    return 3;
  }

  @Override
  public int casualtySpp(Team team) {
    return 2;
  }

  @Override
  public int completionSpp(Team team) {
    return 1;
  }

  @Override
  public int interceptionSpp(Team team) {
    return 2;
  }

  @Override
  public int deflectionSpp(Team team) {
    return 1;
  }

  @Override
  public int catchSpp(Team team) {
    return 1;
  }

  @Override
  public int additionalCompletionSpp(Team team) {
    return 1;
  }

  @Override
  public int additionalCasualtySpp(Team team) {
    return 1;
  }

  @Override
  public int additionalCatchSpp(Team team) {
    return 1;
  }

  @Override
  public void addCompletion(Set<String> additionalCompletionSppTeams, PlayerResult pr) {
    pr.setCompletions(pr.getCompletions() + 1);
    if (additionalCompletionSppTeams.contains(pr.getPlayer().getTeam().getId())) {
      pr.setCompletionsWithAdditionalSpp(pr.getCompletionsWithAdditionalSpp() + 1);
    }
  }

  @Override
  public void addCasualty(Set<String> additionalCasualtySppTeams, PlayerResult pr) {
    pr.setCasualties(pr.getCasualties() + 1);
    if (additionalCasualtySppTeams.contains(pr.getPlayer().getTeam().getId())) {
      pr.setCasualtiesWithAdditionalSpp(pr.getCasualtiesWithAdditionalSpp() + 1);
    }
	}

  @Override
  public void addCatch(Set<String> additionalCatchSppTeams, PlayerResult pr) {}
  
}
