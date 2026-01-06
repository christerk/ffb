package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.RaiseType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
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
	public boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult, Player<?> attacker, Player<?> deadPlayer) {
		return team.getSpecialRules().contains(SpecialRule.FAVOURED_OF_NURGLE) && teamResult.getRaisedDead() == 0 &&
			(attacker != null) && attacker.hasSkillProperty(NamedProperties.allowsRaisingLineman) &&
			(deadPlayer.getStrength() <= 4) && !deadPlayer.hasSkillProperty(NamedProperties.preventRaiseFromDead) && !deadPlayer.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll);
	}

	@Override
	public boolean infectedGoesToReserves() {
		return true;
	}

	@Override
	public boolean canRaiseDead(Team team, TeamResult teamResult, Player<?> deadPlayer) {
		return (team.getSpecialRules().contains(SpecialRule.MASTERS_OF_UNDEATH)) &&
			(teamResult.getRaisedDead() == 0) && (deadPlayer.getStrengthWithModifiers() <= 4) &&
			!deadPlayer.hasSkillProperty(NamedProperties.preventRaiseFromDead);
	}

	@Override
	public PlayerType raisedNurgleType() {
		return PlayerType.PLAGUE_RIDDEN;
	}

	@Override
	public boolean canUseApo(Game game, Player<?> defender, PlayerState playerState) {
		return !ApothecaryType.forPlayer(game, defender, playerState).isEmpty();
	}

	@Override
	public List<RosterPosition> raisePositions(Team team) {
		return Arrays.stream(team.getRoster().getPositions()).filter(pos -> pos.getKeywords().contains(Keyword.LINEMAN))
			.collect(Collectors.toList());
	}

	@Override
	public RaiseType raiseType(Team team) {
		if (team.getSpecialRules().contains(SpecialRule.MASTERS_OF_UNDEATH)) {
			return RaiseType.ZOMBIE;
		}
		return RaiseType.ROTTER;
	}
}
