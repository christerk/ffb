package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InjuryMechanic extends com.fumbbl.ffb.mechanics.InjuryMechanic {

	@Override
	public SendToBoxReason raisedByNurgleReason() {
		return SendToBoxReason.NURGLES_ROT;
	}

	@Override
	public String raisedByNurgleMessage() {
		return " has been infected with Nurgle's Rot and will join team ";
	}

	@Override
	public boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult) {
		return true;
	}

	@Override
	public boolean infectedGoesToReserves() {
		return false;
	}

	@Override
	public boolean canRaiseDead(Team team) {
		return team.getRoster().hasNecromancer();
	}

	@Override
	public PlayerType raisedNurgleType() {
		return PlayerType.RAISED_FROM_DEAD;
	}

	@Override
	public boolean canUseApo(Game game, Player<?> defender, PlayerState playerState) {
		return defender.getPlayerType() != PlayerType.STAR &&
			((game.getTeamHome().hasPlayer(defender) && game.getTurnDataHome().getApothecaries() > 0)
				|| (game.getTeamAway().hasPlayer(defender) && game.getTurnDataAway().getApothecaries() > 0));
	}

}
