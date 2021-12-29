package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
	}};

	@Override
	public boolean updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRollUsed(true);
		turnData.setReRolls(turnData.getReRolls() - 1);
		return false;
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return 4;
	}

	@Override
	public int minimumProRoll() {
		return 4;
	}

	@Override
	public boolean eligibleForPro(Game game, Player<?> player) {
		return true;
	}

	@Override
	public SendToBoxReason raisedByNurgleReason() {
		return SendToBoxReason.NURGLES_ROT;
	}

	@Override
	public String raisedByNurgleMessage() {
		return " has been infected with Nurgle's Rot and will join team ";
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return !modesProhibitingReRolls.contains(turnMode);
	}

	@Override
	public int mvpSpp() {
		return 5;
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
			messages[1] = "Your fan factor will decrease by 1.";
			messages[2] = "You will lose your player award and all your winnings.";
			messages[3] = "Some valuable players (SPP 51+) may decide to leave your team.";
		}
		return messages;
	}

	@Override
	public boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player) {
		return true;
	}

	@Override
	public boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate) {
		return true;
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
	public boolean canPreventStripBall(PlayerState playerState) {
		return true;
	}

	@Override
	public boolean isFoulActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean isBombActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean isGazeActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean declareGazeActionAtStart() {
		return false;
	}

	@Override
	public boolean isKickTeamMateActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean areSpecialBlockActionsAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean allowsCancellingGuard(TurnMode turnMode) {
		return false;
	}

	@Override
	public boolean isBlockActionAllowed(TurnMode turnMode) {
		return true;
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
				return 4;
			}

			@Override
			public int passing() {
				return 0;
			}

			@Override
			public int armour() {
				return 4;
			}
		};
	}

	@Override
	public String calculatePlayerLevel(Game game, Player<?> player) {
		PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
		int oldSpps = playerResult.getCurrentSpps();
		if (oldSpps > 175) {
			return "Legend";
		} else if (oldSpps > 75) {
			return "Super Star";
		} else if (oldSpps > 50) {
			return "Star";
		} else if (oldSpps > 30) {
			return "Emerging";
		} else if (oldSpps > 15) {
			return "Veteran";
		} else if (oldSpps > 5) {
			return "Experienced";
		} else {
			return "Rookie";
		}
	}

	@Override
	public boolean touchdownEndsGame(Game game) {
		return game.getHalf() == 3;
	}

	@Override
	public RosterPosition riotousRookiesPosition(Roster roster) {
		return roster.getRiotousPosition();
	}

	@Override
	public boolean isLegalConcession(Game game, Team team) {
		return UtilPlayer.findPlayersInReserveOrField(game, team).length <= 2;
	}

	@Override
	public boolean starPairCountsAsTwo() {
		return false;
	}

	@Override
	public String fanModificationName() {
		return "Fan Factor";
	}

	@Override
	public int fanModification(TeamResult teamResult) {
		return teamResult.getFanFactorModifier();
	}

	@Override
	public int fans(Team team) {
		return team.getFanFactor();
	}

	@Override
	public String audienceName() {
		return "Fame";
	}

	@Override
	public int audience(TeamResult teamResult) {
		return teamResult.getFame();
	}

	@Override
	public PlayerType raisedNurgleType() {
		return PlayerType.RAISED_FROM_DEAD;
	}

	@Override
	public boolean canUseApo(Game game, Player<?> defender) {
		return defender.getPlayerType() != PlayerType.STAR &&
			((game.getTeamHome().hasPlayer(defender) && game.getTurnDataHome().getApothecaries() > 0)
				|| (game.getTeamAway().hasPlayer(defender) && game.getTurnDataAway().getApothecaries() > 0));
	}

	@Override
	public String weatherDescription(Weather weather) {

		switch (weather) {
			case SWELTERING_HEAT:
				return "Each player on the pitch may suffer from heat exhaustion on a roll of 1 before the next kick-off.";
			case VERY_SUNNY:
				return "A -1 modifier applies to all passing rolls.";
			case NICE:
				return "Perfect Fantasy Football weather.";
			case POURING_RAIN:
				return "A -1 modifier applies to all catch, intercept, or pick-up rolls.";
			case BLIZZARD:
				return "Going For It fails on a roll of 1 or 2 and only quick or short passes can be attempted.";
			default:
				return "No weather at all, but the intro screen shown by the client.";
		}

	}
}
