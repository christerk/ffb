package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InjuryMechanic extends com.fumbbl.ffb.mechanics.InjuryMechanic {
	@Override
	public SendToBoxReason raisedByNurgleReason() {
		return SendToBoxReason.PLAGUE_RIDDEN;
	}

	@Override
	public String raisedByNurgleMessage() {
		return " is now Plague Ridden and will join team ";
	}

	@Override
	public boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult) {
		return team.getSpecialRules().contains(SpecialRule.FAVOURED_OF_NURGLE) && teamResult.getRaisedDead() == 0;
	}

	@Override
	public boolean infectedGoesToReserves() {
		return true;
	}

	@Override
	public boolean canRaiseDead(Team team) {
		return team.getSpecialRules().contains(SpecialRule.MASTERS_OF_UNDEATH);
	}

	@Override
	public PlayerType raisedNurgleType() {
		return PlayerType.PLAGUE_RIDDEN;
	}

	@Override
	public boolean canUseApo(Game game, Player<?> defender, PlayerState playerState) {
		return !ApothecaryType.forPlayer(game, defender, playerState).isEmpty();
	}


}
