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
  public int completionSpp() {
    return 1;
  }

  @Override
  public int interceptionSpp() {
    return 2;
  }

  @Override
  public int deflectionSpp() {
    return 1;
  }

  @Override
  public int catchSpp() {
    return 1;
  }

  @Override
  public int landingSpp() {
    return 0;
  }

  @Override
  public int additionalCompletionSpp() {
    return 1;
  }

  @Override
  public int additionalCasualtySpp() {
    return 1;
  }

  @Override
  public int additionalCatchSpp() {
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

  @Override
  public void addLanding(PlayerResult pr) {}
  
}
