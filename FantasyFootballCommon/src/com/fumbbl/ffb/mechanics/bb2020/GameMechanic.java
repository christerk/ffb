package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.SpecialRule;
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
		add(TurnMode.QUICK_SNAP);
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
	public boolean eligibleForPro(Game game, Player<?> player) {
		return game.getActingPlayer().getPlayer() == player && game.getTurnMode() == TurnMode.REGULAR;
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
		messages[1] = "Your will lose D3 dedicated fans (to a minimum of 1).";
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
	public boolean canRaiseDead(Roster roster) {
		return roster.getSpecialRules().contains(SpecialRule.MASTERS_OF_UNDEATH);
	}

	@Override
	public boolean canPreventStripBall(PlayerState playerState) {
		return playerState.hasTacklezones();
	}

	@Override
	public boolean isFoulActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isBombActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isGazeActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isKickTeamMateActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean areSpecialBlockActionsAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean allowesCancellingGuard(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isBlockActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public PlayerStats zappedPlayerStats() {
		return new PlayerStats() {
			@Override
			public int move() {
				return 5;
			}

			@Override
			public int strength() {
				return 1;
			}

			@Override
			public int agility() {
				return 2;
			}

			@Override
			public int passing() {
				return 0;
			}

			@Override
			public int armour() {
				return 5;
			}
		};
	}
}
