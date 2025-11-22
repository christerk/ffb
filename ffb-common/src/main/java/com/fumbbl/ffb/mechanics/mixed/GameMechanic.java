package com.fumbbl.ffb.mechanics.mixed;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
		add(TurnMode.BLITZ);
		add(TurnMode.QUICK_SNAP);
		add(TurnMode.BETWEEN_TURNS);
	}};

	@Override
	public ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRolls(turnData.getReRolls() - 1);
		if (turnData.getReRollsBrilliantCoachingOneDrive() > 0) {
			turnData.setReRollsBrilliantCoachingOneDrive(turnData.getReRollsBrilliantCoachingOneDrive() - 1);
			return ReRollSources.BRILLIANT_COACHING_RE_ROLL;
		}
		if (turnData.getReRollsPumpUpTheCrowdOneDrive() > 0) {
			turnData.setReRollsPumpUpTheCrowdOneDrive(turnData.getReRollsPumpUpTheCrowdOneDrive() - 1);
			return ReRollSources.PUMP_UP_THE_CROWD;
		}
		if (turnData.getReRollShowStarOneDrive() > 0) {
			turnData.setReRollShowStarOneDrive(turnData.getReRollShowStarOneDrive() - 1);
			return ReRollSources.SHOW_STAR;
		}

		return null;
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
	public boolean isFoulActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isBombActionAllowed(TurnMode turnMode) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean isGazeActionAllowed(TurnMode turnMode, PlayerAction playerAction) {
		return TurnMode.BLITZ != turnMode;
	}

	@Override
	public boolean declareGazeActionAtStart() {
		return true;
	}

	@Override
	public boolean isKickTeamMateActionAllowed(TurnMode turnMode) {
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
	public boolean touchdownEndsGame(Game game) {
		return game.getHalf() == 3 && ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.OVERTIME_GOLDEN_GOAL)).isEnabled();
	}

	@Override
	public RosterPosition riotousRookiesPosition(Roster roster) {
		List<RosterPosition> rosterPositions = Arrays.stream(roster.getPositions()).filter(pos -> pos.getQuantity() == 12 || pos.getQuantity() == 16)
			.filter(pos -> pos.getType() != PlayerType.IRREGULAR).collect(Collectors.toList());
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
			.filter(PlayerState::canBeSetUpNextDrive)
			.count() <= 3;
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
	public String weatherDescription(Weather weather) {

		switch (weather) {
			case SWELTERING_HEAT:
				return "D3 random players from each team on the pitch will suffer from heat exhaustion before the next kick-off.";
			case VERY_SUNNY:
				return "A -1 modifier applies to all passing rolls.";
			case NICE:
				return "Perfect Fantasy Football weather.";
			case POURING_RAIN:
				return "A -1 modifier applies to all catch, intercept, or pick-up rolls.";
			case BLIZZARD:
				return "Rushes fail on a roll of 1 or 2 and only quick or short passes can be attempted.";
			default:
				return "No weather at all, but the intro screen shown by the client.";
		}

	}

	@Override
	public Set<String> enhancementsToRemoveAtEndOfTurn(SkillFactory skillFactory) {
		return Constant.getEnhancementSkillsToRemoveAtEndOfTurn(skillFactory);
	}

	@Override
	public Set<String> enhancementsToRemoveAtEndOfTurnWhenNotSettingActive(SkillFactory skillFactory) {
		return Constant.getEnhancementSkillsToRemoveAtEndOfTurnWhenNotSettingActive(skillFactory);
	}

	@Override
	public boolean rollForChefAtStartOfHalf() {
		return false;
	}

	@Override
	public boolean allowMovementInEndZone() {
		return false;
	}
}
