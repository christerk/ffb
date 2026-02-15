package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.RaiseType;

import java.util.Collections;
import java.util.List;

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
	public boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult, Player<?> attacker, Player<?> deadPlayer) {
		return  (attacker != null) &&
			attacker.hasSkillProperty(NamedProperties.allowsRaisingLineman) && (deadPlayer.getStrength() <= 4) &&
			!deadPlayer.hasSkillProperty(NamedProperties.preventRaiseFromDead) &&
			!deadPlayer.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll);
	}

	@Override
	public boolean infectedGoesToReserves() {
		return false;
	}

	@Override
	public boolean canRaiseDead(Team team, TeamResult teamResult, Player<?> deadPlayer) {
		return (team.getRoster().hasNecromancer() || team.getRoster().hasVampireLord()) &&
			(teamResult.getRaisedDead() == 0) && (deadPlayer.getStrength() <= 4) &&
			!deadPlayer.hasSkillProperty(NamedProperties.preventRaiseFromDead);
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

	@Override
	public List<RosterPosition> raisePositions(Team team) {
		return Collections.emptyList();
	}

	@Override
	public RaiseType raiseType(Team team) {
		if (team.getRoster().hasNecromancer()) {
			return RaiseType.ZOMBIE;
		}
		if (team.getRoster().hasVampireLord()) {
			return RaiseType.THRALL;
		}
		return RaiseType.ROTTER;
	}

}
