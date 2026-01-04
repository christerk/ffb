package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.util.pathfinding.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.mixed.ReportThrowAtStallingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThrowARockStalling;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.command.DropPlayerCommand;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StallingExtension {

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

	private boolean hasOpenPathToEndzone(Game game, Player<?> player) {
		FieldCoordinateBounds endzoneBounds =
				game.getTeamHome().hasPlayer(player) ? FieldCoordinateBounds.ENDZONE_AWAY : FieldCoordinateBounds.ENDZONE_HOME;

		Set<FieldCoordinate> endZoneCoordinates =
				Arrays.stream(endzoneBounds.fieldCoordinates()).collect(Collectors.toSet());

		return ArrayTool.isProvided(PathFinderWithPassBlockSupport.getShortestPath(game, endZoneCoordinates, player, 0));
	}

	public void handleStaller(IStep step, Player<?> player) {
		GameState gameState = step.getGameState();
		Game game = gameState.getGame();

		int roll = gameState.getDiceRoller().rollDice(6);

		boolean successful = roll >= game.getTurnData().getTurnNr();

		step.getResult().addReport(new ReportThrowAtStallingPlayer(player.getId(), roll, successful));

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
			step.publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
					new SteadyFootingContext(injuryResult, Collections.singletonList(new DropPlayerCommand(player.getId(),
							ApothecaryMode.HIT_PLAYER, true)))));
		}
	}
}
