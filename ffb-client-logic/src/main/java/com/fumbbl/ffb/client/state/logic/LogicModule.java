package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class LogicModule {
	protected final FantasyFootballClient client;

	public LogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	public abstract ClientStateId getId();

	public abstract Set<ClientAction> availableActions();

	protected abstract ActionContext actionContext(ActingPlayer actingPlayer);

	public void setUp() {
	}

	public void teardown() {
	}

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

	public boolean endPlayerActivation() {
		if (client.getGame().getTurnMode().allowEndPlayerAction()) {
			if (client.getGame().getFieldModel() != null) {
				client.getGame().getFieldModel().setRangeRuler(null);
			}
			client.getCommunication().sendActingPlayer(null, null, false);
			return true;
		}
		return false;
	}

	public ActingPlayer getActingPlayer() {
		return client.getGame().getActingPlayer();
	}

	public boolean playerActivationUsed() {
		return client.getGame().getActingPlayer().hasActed();
	}

	public FieldCoordinate getCoordinate(Player<?> player) {
		return client.getGame().getFieldModel().getPlayerCoordinate(player);
	}

	public InteractionResult playerInteraction(Player<?> player) {
		return InteractionResult.ignore();
	}

	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		return InteractionResult.ignore();
	}

	public InteractionResult playerPeek(Player<?> player) {
		return InteractionResult.reset();
	}

	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		return InteractionResult.reset();
	}

	public Optional<Player<?>> getPlayer(FieldCoordinate coordinate) {
		return Optional.ofNullable(client.getGame().getFieldModel().getPlayer(coordinate));
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

	public boolean isTreacherousAvailable(Player<?> player) {
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

	public boolean isLookIntoMyEyesAvailable(Player<?> player) {
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

	public boolean isThenIStartedBlastinAvailable(ActingPlayer player) {
		return !player.hasActed() && isThenIStartedBlastinAvailable(player.getPlayer());
	}

	protected boolean isThenIStartedBlastinAvailable(Player<?> player) {
		Game game = client.getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canBlastRemotePlayer) &&
			Arrays.stream(game.getOtherTeam(game.getActingTeam()).getPlayers()).anyMatch(
				opponent -> fieldModel.getPlayerCoordinate(opponent).distanceInSteps(playerCoordinate) <= 3);
	}

	public boolean isBlockActionAvailable(Player<?> player) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive() && !player.hasSkillProperty(NamedProperties.preventRegularBlockAction)
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& player.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 0);
		}
		return false;
	}

	public boolean isMultiBlockActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive()
			&& ((UtilCards.hasSkillWithProperty(player, NamedProperties.canBlockMoreThanOnce)
			&& !UtilCards.hasSkillToCancelProperty(player, NamedProperties.canBlockMoreThanOnce))
			|| (UtilCards.hasSkillWithProperty(player, NamedProperties.canBlockTwoAtOnce)
			&& !UtilCards.hasSkillToCancelProperty(player, NamedProperties.canBlockTwoAtOnce)))
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& player.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 1);
		}
		return false;
	}

	public boolean isThrowBombActionAvailable(Player<?> player) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null)
			&& mechanic.isBombActionAllowed(game.getTurnMode())
			&& !game.getTurnData().isBombUsed()
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& !playerState.isProneOrStunned()
			&& player.hasSkillProperty(NamedProperties.enableThrowBombAction));
	}

	public boolean isBlitzActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return (!game.getTurnData().isBlitzUsed()
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED) && (playerState != null)
			&& playerState.isActive() && (playerState.isAbleToMove() || playerState.isRooted())
			&& !player.hasSkillProperty(NamedProperties.preventRegularBlitzAction));
	}

	public boolean isFoulActionAvailable(Player<?> player) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.isFoulActionAllowed(game.getTurnMode())
			&& playerState.isActive() && (!game.getTurnData().isFoulUsed() || player.hasSkillProperty(NamedProperties.allowsAdditionalFoul))
			&& !player.hasSkillProperty(NamedProperties.preventRegularFoulAction)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = game.getFieldModel().getPlayerState(opponent);
				if (opponentState.canBeFouled()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPassActionAvailable(Player<?> player, boolean treacherousAvailable) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, player) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, player) || treacherousAvailable))
			&& !player.hasSkillProperty(NamedProperties.preventRegularPassAction));
	}

	public boolean isHandOverActionAvailable(Player<?> player, boolean treacherousAvailable) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return (!game.getTurnData().isHandOverUsed()
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, player) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, player) || treacherousAvailable))
			&& !player.hasSkillProperty(NamedProperties.preventRegularHandOverAction));
	}

	public boolean isThrowTeamMateActionAvailable(Player<?> player) {
		Game game = client.getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if ((playerState == null) || player.hasSkillProperty(NamedProperties.preventThrowTeamMateAction)) {
			return false;
		}

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = client.getGame().getFieldModel();
		Player<?>[] teamPlayers = player.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeThrown(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = ArrayTool.isProvided(mechanic.findThrowableTeamMates(game, player));

		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.canThrow(player) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	public boolean isKickTeamMateActionAvailable(Player<?> player) {
		Game game = client.getGame();
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if (!gameMechanic.isKickTeamMateActionAllowed(game.getTurnMode()) || playerState == null || player.hasSkillProperty(NamedProperties.preventKickTeamMateAction)) {
			return false;
		}
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = client.getGame().getFieldModel();
		Player<?>[] teamPlayers = player.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeKicked(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = false;
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		Player<?>[] adjacentTeamPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(game, player.getTeam(),
			playerCoordinate, false);
		for (Player<?> adjacentTeamPlayer : adjacentTeamPlayers) {
			if (mechanic.canBeKicked(game, adjacentTeamPlayer)) {
				rightStuffAdjacent = true;
				break;
			}
		}

		return (mechanic.isKtmAvailable(game.getTurnData())
			&& !game.getFieldModel().hasCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& player.hasSkillProperty(NamedProperties.canKickTeamMates) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	public boolean isStandUpActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null) && (playerState.getBase() == PlayerState.PRONE) && playerState.isActive()
			&& !player.hasSkillProperty(NamedProperties.preventStandUpAction));
	}

	public boolean isRecoverFromConfusionActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null) && playerState.isConfused() && playerState.isActive()
			&& (playerState.getBase() != PlayerState.PRONE)
			&& !player.hasSkillProperty(NamedProperties.preventRecoverFromConcusionAction));
	}

	public boolean isRecoverFromGazeActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null) && playerState.isHypnotized() && (playerState.getBase() != PlayerState.PRONE)
			&& !player.hasSkillProperty(NamedProperties.preventRecoverFromGazeAction));
	}

	public boolean isBeerBarrelBashAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return game.getTurnMode() == TurnMode.REGULAR && playerState.getBase() == PlayerState.STANDING && UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canThrowKeg);
	}

	public boolean isAllYouCanEatAvailable(Player<?> player) {
		Game game = client.getGame();
		return isThrowBombActionAvailable(player) && game.getTurnMode() == TurnMode.REGULAR
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseThrowBombActionTwice);
	}

	public boolean isKickEmBlockAvailable(Player<?> player) {
		return isKickEmAvailable(player, false);
	}

	public boolean isKickEmBlitzAvailable(Player<?> player) {
		return isKickEmAvailable(player, true);
	}

	private boolean isKickEmAvailable(Player<?> player, boolean moveAllowed) {
		Game game = client.getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		PlayerState playerState = fieldModel.getPlayerState(player);
		if ((playerState != null) && playerState.isActive() && (!game.getTurnData().isBlitzUsed() || !moveAllowed)
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseChainsawOnDownedOpponents) && player.hasSkill(NamedProperties.blocksLikeChainsaw)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = fieldModel.getPlayerState(opponent);
				if (opponentState.canBeFouled() && (moveAllowed || playerCoordinate.isAdjacent(fieldModel.getPlayerCoordinate(opponent)))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isFlashingBladeAvailable(Player<?> player) {
		Game game = client.getGame();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		return (playerState != null) && playerState.isActive()
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& (playerState.getBase() != PlayerState.PRONE)
			&& player.hasUnusedSkillProperty(NamedProperties.canStabAndMoveAfterwards)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player)));
	}

	protected boolean isEndPlayerActionAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (!actingPlayer.hasActed()
			|| !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.forceFullMovement)
			|| (actingPlayer.getCurrentMove() >= actingPlayer.getPlayer().getMovementWithModifiers()));
	}


	public boolean isJumpAvailableAsNextMove(Game game, ActingPlayer actingPlayer, boolean jumping) {
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		return mechanic.isAvailableAsNextMove(game, actingPlayer, jumping);
	}

	public Optional<Skill> isBoundingLeapAvailable(Game game, ActingPlayer actingPlayer) {
		if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			return Optional.ofNullable(UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canIgnoreJumpModifiers));
		}

		return Optional.empty();
	}

	public boolean isFumblerooskieAvailable() {
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();

		return (UtilCards.hasUncanceledSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canDropBall)
			&& actingPlayer.getPlayerAction() != null
			&& actingPlayer.getPlayerAction().allowsFumblerooskie()
			&& UtilPlayer.hasBall(client.getGame(), actingPlayer.getPlayer()));
	}

	public boolean isPutridRegurgitationAvailable() {
		return false;
	}

	public boolean isSpecialAbilityAvailable(ActingPlayer actingPlayer) {
		return isTreacherousAvailable(actingPlayer)
			|| isWisdomAvailable(actingPlayer)
			|| isRaidingPartyAvailable(actingPlayer)
			|| isLookIntoMyEyesAvailable(actingPlayer)
			|| isBalefulHexAvailable(actingPlayer)
			|| isPutridRegurgitationAvailable()
			|| isCatchOfTheDayAvailable(actingPlayer)
			|| isBlackInkAvailable(actingPlayer)
			|| isThenIStartedBlastinAvailable(actingPlayer);
	}

	public boolean isPassAnySquareAvailable(ActingPlayer actingPlayer, Game game) {
		return (PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer());
	}

	public boolean performsRangeGridAction(ActingPlayer actingPlayer, Game game) {
		return isPassAnySquareAvailable(actingPlayer, game)
			|| showGridForKTM(game, actingPlayer)
			|| ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction() || PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction())
			&& UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), true));
	}

	public boolean isMoveAvailable(ActingPlayer actingPlayer) {
		return PlayerAction.GAZE == actingPlayer.getPlayerAction();
	}

	protected boolean showGridForKTM(@SuppressWarnings("unused") Game game, @SuppressWarnings("unused") ActingPlayer actingPlayer) {
		return false;
	}

	public boolean isHailMaryPassActionAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
			&& !(game.getFieldModel().getWeather().equals(Weather.BLIZZARD)));
	}

	protected boolean isViciousVinesAvailable(Player<?> player) {
		Game game = client.getGame();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		return (playerState != null) && playerState.isActive() && playerState.getBase() == PlayerState.STANDING
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& player.hasUnusedSkillProperty(NamedProperties.canBlockOverDistance)
			&& ArrayTool.isProvided(UtilPlayer.findNonAdjacentBlockablePlayersTwoSquaresAway(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player)));
	}

	protected boolean isFuriousOutburstAvailable(Player<?> player) {
		Game game = client.getGame();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);

		return (playerState != null) && playerState.isActive()
			&& playerState.getBase() == PlayerState.STANDING
			&& !game.getTurnData().isBlitzUsed()
			&& player.hasUnusedSkillProperty(NamedProperties.canTeleportBeforeAndAfterAvRollAttack)
			&& ArrayTool.isProvided(UtilPlayer.findBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player), 3));
	}
}
