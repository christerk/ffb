package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.fumbbl.ffb.inducement.Usage.GAME_MODIFICATION;
import static com.fumbbl.ffb.inducement.Usage.LONER;
import static com.fumbbl.ffb.inducement.Usage.REROLL_ONES_ON_KOS;
import static com.fumbbl.ffb.inducement.Usage.STAFF;
import static com.fumbbl.ffb.inducement.Usage.STAR;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
	}};

	@Override
	public ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRollUsed(true);
		turnData.setReRolls(turnData.getReRolls() - 1);
		return null;
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
	public boolean isFoulActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean isBombActionAllowed(TurnMode turnMode) {
		return true;
	}

	@Override
	public boolean isGazeActionAllowed(TurnMode turnMode, PlayerAction playerAction) {
		return playerAction == PlayerAction.MOVE;
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

	@Override
	public Set<String> enhancementsToRemoveAtEndOfTurn(SkillFactory skillFactory) {
		return Collections.emptySet();
	}

	@Override
	public Set<String> enhancementsToRemoveAtEndOfTurnWhenNotSettingActive(SkillFactory skillFactory) {
		return Collections.emptySet();
	}

	@Override
	public boolean rollForChefAtStartOfHalf() {
		return true;
	}

	@Override
	public boolean allowMovementInEndZone() {
		return true;
	}

	@Override
	public Set<Usage> explicitlySelectedInducements() {
		return new HashSet<Usage>() {{
			add(LONER);
			add(STAR);
			add(GAME_MODIFICATION);
			add(STAFF);
			add(REROLL_ONES_ON_KOS);
		}};
	}
}
