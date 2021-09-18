package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		return game.getActingPlayer().getPlayer() == player && (game.getTurnMode() == TurnMode.REGULAR || game.getTurnMode() == TurnMode.BLITZ);
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
		String[] messages;
		if (legalConcession) {
			messages = new String[2];
			messages[0] = "Do you want to concede this game?";
			messages[1] = "The concession will have no negative consequences at this point.";
		} else {
			messages = new String[4];
			messages[0] = "Do you want to concede this game?";
			messages[1] = "You will lose D3 dedicated fans (to a minimum of 1).";
			messages[2] = "You will lose your player award and all your winnings.";
			messages[3] = "Some valuable players (3 or more advancements) may decide to leave your team.";
		}
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
	public boolean allowsCancellingGuard(TurnMode turnMode) {
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

	@Override
	public String calculatePlayerLevel(Game game, Player<?> player) {
		int gainedSkills = (int) player.skillInfos().stream()
			.filter(info -> info.getCategory() == SkillDisplayInfo.Category.PLAYER
				&& info.getSkill().getCategory() != SkillCategory.STAT_DECREASE).count();

		switch (gainedSkills) {
			case 0:
				return "Rookie";
			case 1:
				return "Experienced";
			case 2:
				return "Veteran";
			case 3:
				return "Emerging";
			case 4:
				return "Star";
			case 5:
				return "Super Star";
			default:
				return "Legend";
		}
	}

	@Override
	public boolean touchdownEndsGame(Game game) {
		return false;
	}

	@Override
	public RosterPosition riotousRookiesPosition(Roster roster) {
		List<RosterPosition> rosterPositions = Arrays.stream(roster.getPositions()).filter(pos -> pos.getQuantity() == 12 || pos.getQuantity() == 16).collect(Collectors.toList());
		if (rosterPositions.isEmpty()) {
			return null;
		}
		Collections.shuffle(rosterPositions);
		return rosterPositions.get(0);
	}

	@Override
	public boolean isLegalConcession(Game game, Team team) {
		return game.getTurnMode() == TurnMode.SETUP && Arrays.stream(team.getPlayers())
			.map(player -> game.getFieldModel().getPlayerState(player))
			.filter(PlayerState::canBeSetUp)
			.count() <= 3;
	}

	@Override
	public boolean starPairCountsAsTwo() {
		return true;
	}

	@Override
	public String fanModificationName() {
		return "Dedicated Fans";
	}

	@Override
	public int fanModification(TeamResult teamResult) {
		return teamResult.getDedicatedFansModifier();
	}

	@Override
	public int fans(Team team) {
		return team.getDedicatedFans();
	}

	@Override
	public String audienceName() {
		return "Fan Factor";
	}

	@Override
	public int audience(TeamResult teamResult) {
		return teamResult.getFanFactor();
	}

	@Override
	public PlayerType raisedNurgleType() {
		return PlayerType.PLAGUE_RIDDEN;
	}
}
