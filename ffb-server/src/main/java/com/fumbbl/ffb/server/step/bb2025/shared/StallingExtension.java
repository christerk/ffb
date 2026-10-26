package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.util.pathfinding.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportThrowAtStallingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThrowARockStalling;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StallingExtension {

	// from this turn on the rock throwing coach runs out of time to be angry about stalling
	private static final int LAST_STALLING_TURN = 6;

	private final Set<ISkillProperty> rollAtActivation = new HashSet<ISkillProperty>() {{
		add(NamedProperties.appliesConfusion);
		add(NamedProperties.needsToRollForActionBlockingIsEasier);
		add(NamedProperties.needsToRollForActionButKeepsTacklezone);
		add(NamedProperties.becomesImmovable);
	}};

	public boolean isConsideredStalling(Game game, Player<?> player) {
		return UtilPlayer.hasBall(game, player)
			&& player.getSkillsIncludingTemporaryOnes().stream().flatMap(skill -> skill.getSkillProperties().stream())
			.noneMatch(rollAtActivation::contains)
			&& !ArrayTool.isProvided(UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(player.getTeam()),
			game.getFieldModel().getPlayerCoordinate(player), false))
			&& hasOpenPathToEndzone(game, player);
	}

	/**
	 * Checks whether ending the turn right now would get a rock thrown at a team mate that could still score.
	 *
	 * @param excludedPlayer the currently acting player, they are not stalling as long as their action is running
	 */
	public boolean wouldEndOfTurnTriggerStallingRoll(Game game, Player<?> excludedPlayer) {
		if (game.getTurnMode() != TurnMode.REGULAR
			|| !UtilGameOption.isOptionEnabled(game, GameOptionId.ENABLE_STALLING_CHECK)
			|| game.getTurnData().getTurnNr() > LAST_STALLING_TURN) {
			return false;
		}

		Team team = excludedPlayer != null ? excludedPlayer.getTeam() : game.getActingTeam();

		return Arrays.stream(team.getPlayers())
			.filter(player -> player != excludedPlayer && UtilPlayer.hasBall(game, player))
			.anyMatch(player -> {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				return playerState != null && playerState.isActive() && isConsideredStalling(game, player);
			});
	}

	private boolean hasOpenPathToEndzone(Game game, Player<?> player) {
		FieldCoordinateBounds endzoneBounds =
			game.getTeamHome().hasPlayer(player) ? FieldCoordinateBounds.ENDZONE_AWAY : FieldCoordinateBounds.ENDZONE_HOME;

		Set<FieldCoordinate> endZoneCoordinates =
			Arrays.stream(endzoneBounds.fieldCoordinates()).collect(Collectors.toSet());

		return ArrayTool.isProvided(
			PathFinderWithPassBlockSupport.INSTANCE.getShortestPath(game, endZoneCoordinates, player, 0));
	}

	public void handleStaller(IStep step, Player<?> player) {
		GameState gameState = step.getGameState();
		Game game = gameState.getGame();

		int roll = 0;
		boolean successful;

		if (game.getTurnData().getTurnNr() > LAST_STALLING_TURN) {
			successful = false;
		} else {
			roll = gameState.getDiceRoller().rollDice(6);

			successful = roll >= game.getTurnData().getTurnNr();
		}

		step.getResult().addReport(new ReportThrowAtStallingPlayer(player.getId(), roll, successful));

		TeamResult teamResult =
			game.getTeamHome().hasPlayer(player) ? game.getGameResult().getTeamResultHome() :
				game.getGameResult().getTeamResultAway();

		teamResult.setStalled(true);

		if (successful) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

			FieldCoordinate startCoordinate;
			if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
				startCoordinate = new FieldCoordinate(gameState.getDiceRoller().rollXCoordinate(), 0);
			} else {
				startCoordinate = new FieldCoordinate(gameState.getDiceRoller().rollXCoordinate(), 14);
			}

			step.getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate));
			UtilServerGame.syncGameModel(step);

			InjuryResult injuryResult = UtilServerInjury.handleInjury(step,
				new InjuryTypeThrowARockStalling(), null, player, playerCoordinate, null, null, ApothecaryMode.HIT_PLAYER);
			DropPlayerContext dropPlayerContext = new DropPlayerContext(injuryResult, false, true,
				null, player.getId(), ApothecaryMode.HIT_PLAYER, false);
			step.publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
				new SteadyFootingContext(dropPlayerContext)));
		}
	}
}
