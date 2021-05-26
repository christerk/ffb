package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
		add(TurnMode.BLITZ);
	}};

	@Override
	public boolean updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRolls(turnData.getReRolls() - 1);
		if (turnData.getReRollsBrilliantCoachingOneDrive() > 0) {
			turnData.setReRollsBrilliantCoachingOneDrive(turnData.getReRollsBrilliantCoachingOneDrive() - 1);
			return true;
		}
		return false;
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return player.getSkillIntValue(NamedProperties.hasToRollToUseTeamReroll);
	}

	@Override
	public int minimumProRoll() {
		return 3;
	}

	@Override
	public boolean eligibleForPro(ActingPlayer actingPlayer, Player<?> player) {
		return actingPlayer.getPlayer() == player;
	}

	@Override
	public SendToBoxReason raisedByNurgleReason() {
		return SendToBoxReason.PLAGUE_RIDDEN;
	}

	@Override
	public String raisedByNurgleMessage() {
		return " is now Plague Ridden and will join team ";
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return !modesProhibitingReRolls.contains(turnMode);
	}

	@Override
	public int mvpSpp() {
		return 4;
	}

	@Override
	public String[] concessionDialogMessages(boolean legalConcession) {
		String[] messages = new String[4];
		messages[0] = "Do you want to concede this game?";
		messages[1] = "Your you will D3 dedicated fans (to a minimum of 1).";
		messages[2] = "You will lose your player award and all your winnings.";
		messages[3] = "Some valuable players (more than 3 advancements) may decide to leave your team.";
		return messages;
	}

	@Override
	public boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player) {
		return !(usingMultiBlock && fieldModel.isMultiBlockTarget(player.getId()));
	}

	@Override
	public boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate) {
		return !(fieldModel.wasMultiBlockTargetSquare(coordinate));
	}

	@Override
	public int assistReduction(boolean usingMultiBlock, Game game, Player<?> attacker) {
		boolean reduceAssists = usingMultiBlock && !game.getActingTeam().hasPlayer(attacker) &&
			(game.getFieldModel().selectedMultiBlockTargets() < 1 || game.getFieldModel().isMultiBlockTarget(attacker.getId()));
		return reduceAssists ? 1 : 0;
	}
}
