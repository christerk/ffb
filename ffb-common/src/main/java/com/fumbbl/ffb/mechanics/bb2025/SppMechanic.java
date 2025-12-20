package com.fumbbl.ffb.mechanics.bb2025;

import java.util.Set;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SppMechanic extends com.fumbbl.ffb.mechanics.SppMechanic {

	@Override
	public int touchdownSpp(Team team) {
		return hasBrawlinBrutes(team) ? 2 : 3;
	}

	@Override
	public int casualtySpp(Team team) {
		return hasBrawlinBrutes(team) ? 3 : 2;
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
	public void addCatch(Set<String> additionalCatchSppTeams, PlayerResult pr) {
		if (additionalCatchSppTeams.contains(pr.getPlayer().getTeam().getId())) {
			pr.setCatchesWithAdditionalSpp(pr.getCatchesWithAdditionalSpp() + 1);
		}
	}

	private boolean hasBrawlinBrutes(Team team) {
		return team != null && team.getSpecialRules().contains(SpecialRule.BRAWLIN_BRUTES);
	}
  
}
