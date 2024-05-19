package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class LogicModule {
	protected final FantasyFootballClient client;

	public LogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	public abstract Set<ClientAction> availableActions();

	public final void perform(Player<?> player, ClientAction action) {
		if (availableActions().contains(action)) {
			performAvailableAction(player, action);
		} else {
			client.logError("Unsupported action " + action.name() + " in logic module " + this.getClass().getCanonicalName());
		}
	}

	protected abstract void performAvailableAction(Player<?> player, ClientAction action);

	public void endTurn() {
	}

	public void deselectActingPlayer() {
		client.getCommunication().sendActingPlayer(null, null, false);
	}

	public boolean playerActivationUsed() {
		return client.getGame().getActingPlayer().hasActed();
	}

	public InteractionResult playerInteraction(Player<?> player) {
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public InteractionResult.Kind playerPeek(Player<?> player) {
		return InteractionResult.Kind.IGNORE;
	}

	public boolean isHypnoticGazeActionAvailable(boolean declareAtStart, Player<?> player, ISkillProperty property) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		return ((mechanic.declareGazeActionAtStart() == declareAtStart)
			&& mechanic.isGazeActionAllowed(game.getTurnMode(), actingPlayer.getPlayerAction())
			&& UtilPlayer.canGaze(game, player, property));
	}

	public boolean isTreacherousAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isTreacherousAvailable(actingPlayer.getPlayer());
	}

	protected boolean isTreacherousAvailable(Player<?> player) {
		Game game = client.getGame();
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canStabTeamMateForBall)
			&& Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getActingTeam(), game.getFieldModel().getPlayerCoordinate(player)))
			.anyMatch(adjacentPlayer -> UtilPlayer.hasBall(game, adjacentPlayer));
	}

	public boolean isCatchOfTheDayAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isCatchOfTheDayAvailable(actingPlayer.getPlayer());
	}

	protected boolean isCatchOfTheDayAvailable(Player<?> player) {
		Game game = client.getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		FieldCoordinate ballCoordinate = game.getFieldModel().getBallCoordinate();

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGetBallOnGround)
			&& game.getFieldModel().isBallMoving() && playerCoordinate.distanceInSteps(ballCoordinate) <= 3;
	}


	public boolean isWisdomAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isWisdomAvailable(actingPlayer.getPlayer());
	}

	protected boolean isWisdomAvailable(Player<?> player) {
		Game game = client.getGame();

		Set<Skill> ownedSkills = player.getSkillsIncludingTemporaryOnes();

		boolean canGainSkill = Constant.getGrantAbleSkills(game.getFactory(FactoryType.Factory.SKILL)).stream()
			.map(SkillWithValue::getSkill)
			.anyMatch(skillClass -> !ownedSkills.contains(skillClass));

		return canGainSkill && Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, player.getTeam(),
				game.getFieldModel().getPlayerCoordinate(player), false))
			.anyMatch(teamMate -> teamMate.hasSkillProperty(NamedProperties.canGrantSkillsToTeamMates) && !teamMate.isUsed(NamedProperties.canGrantSkillsToTeamMates));
	}

	public boolean isBlackInkAvailable(ActingPlayer player) {
		return !player.hasActed() && !player.isStandingUp() && isBlackInkAvailable(player.getPlayer());
	}

	protected boolean isBlackInkAvailable(Player<?> player) {
		Game game = client.getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGazeAutomatically)
			&& ArrayTool.isProvided(UtilPlayer
			.findAdjacentStandingOrPronePlayers(game, game.getOtherTeam(game.getActingTeam()), playerCoordinate));
	}

	public boolean isRaidingPartyAvailable(ActingPlayer player) {
		return !player.hasActed() && isRaidingPartyAvailable(player.getPlayer());
	}

	protected boolean isRaidingPartyAvailable(Player<?> player) {
		Game game = client.getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canMoveOpenTeamMate)
			&& Arrays.stream(game.getActingTeam().getPlayers()).anyMatch(
			teamMate -> {
				FieldCoordinate teamMateCoordinate = fieldModel.getPlayerCoordinate(teamMate);
				Player<?>[] adjacentPlayersWithTacklezones = UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(game.getActingTeam()), teamMateCoordinate, false);
				FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(teamMateCoordinate, FieldCoordinateBounds.FIELD,
					1, false);
				return fieldModel.getPlayerState(teamMate).getBase() == PlayerState.STANDING
					&& teamMateCoordinate.distanceInSteps(playerCoordinate) <= 5
					&& !ArrayTool.isProvided(adjacentPlayersWithTacklezones)
					&& Arrays.stream(adjacentCoordinates).anyMatch(adjacentCoordinate -> {
					java.util.List<Player<?>> playersOnSquare = fieldModel.getPlayers(adjacentCoordinate);
					return (playersOnSquare == null || playersOnSquare.isEmpty())
						&& Arrays.stream(fieldModel.findAdjacentCoordinates(adjacentCoordinate, FieldCoordinateBounds.FIELD,
						1, false)).anyMatch(fieldCoordinate -> {
						List<Player<?>> players = game.getFieldModel().getPlayers(fieldCoordinate);
						return players != null && !players.isEmpty() && !game.getActingTeam().hasPlayer(players.get(0));
					});
				});
			}
		);
	}

	public boolean isLookIntoMyEyesAvailable(ActingPlayer actingPlayer) {
		PlayerState oldPlayerState = actingPlayer.getOldPlayerState();
		boolean hadTackleZone = oldPlayerState != null && oldPlayerState.hasTacklezones();
		return !actingPlayer.hasActed() && hadTackleZone && isLookIntoMyEyesAvailable(actingPlayer.getPlayer());
	}

	protected boolean isLookIntoMyEyesAvailable(Player<?> player) {
		Game game = client.getGame();
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
			&& Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(player.getTeam()), game.getFieldModel().getPlayerCoordinate(player)))
			.anyMatch(opponent -> UtilPlayer.hasBall(game, opponent));
	}

	public boolean isBalefulHexAvailable(ActingPlayer player) {
		return !player.hasActed() && isBalefulHexAvailable(player.getPlayer());
	}

	protected boolean isBalefulHexAvailable(Player<?> player) {
		Game game = client.getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canMakeOpponentMissTurn)
			&& Arrays.stream(game.getOtherTeam(game.getActingTeam()).getPlayers()).anyMatch(
			opponent -> fieldModel.getPlayerCoordinate(opponent).distanceInSteps(playerCoordinate) <= 5
		);
	}

}
