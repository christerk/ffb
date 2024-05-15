package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.PlayerInteractionResult;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MoveLogicModule extends LogicModule {

	public MoveLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public Set<ClientAction> availableActions() {

		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
			add(ClientAction.JUMP);
			add(ClientAction.HAND_OVER);
			add(ClientAction.PASS);
			add(ClientAction.THROW_TEAM_MATE);
			add(ClientAction.KICK_TEAM_MATE);
			add(ClientAction.MOVE);
			add(ClientAction.GAZE);
			add(ClientAction.FUMBLEROOSKIE);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.BLOCK);
			add(ClientAction.CATCH_OF_THE_DAY);
			add(ClientAction.BOUNDING_LEAP);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case END_MOVE:
					if (isEndPlayerActionAvailable()) {
						communication.sendActingPlayer(null, null, false);
					}
					break;
				case JUMP:
					if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(player, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case HAND_OVER:
					if (UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
						if (PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction()) {
							communication.sendActingPlayer(player, PlayerAction.HAND_OVER, actingPlayer.isJumping());
						} else if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
							communication.sendActingPlayer(player, PlayerAction.HAND_OVER_MOVE, actingPlayer.isJumping());
						}
					}
					break;
				case PASS:
					if (PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()
						&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
						communication.sendActingPlayer(player, PlayerAction.PASS, actingPlayer.isJumping());
					}
					break;
				case THROW_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.THROW_TEAM_MATE, actingPlayer.isJumping());
					break;
				case KICK_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.KICK_TEAM_MATE, actingPlayer.isJumping());
					break;
				case MOVE:
					if (PlayerAction.GAZE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(player, PlayerAction.MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.PASS == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(player, PlayerAction.PASS_MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(player, PlayerAction.THROW_TEAM_MATE_MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.KICK_TEAM_MATE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(player, PlayerAction.KICK_TEAM_MATE_MOVE, actingPlayer.isJumping());
					}
					break;
				case GAZE:
					if (isHypnoticGazeActionAvailable(false, actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
						communication.sendActingPlayer(player, PlayerAction.GAZE, actingPlayer.isJumping());
					}
					break;
				case FUMBLEROOSKIE:
					if (isFumblerooskieAvailable()) {
						communication.sendUseFumblerooskie();
					}
					break;
				case TREACHEROUS:
					if (isTreacherousAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case WISDOM:
					if (isWisdomAvailable(actingPlayer)) {
						communication.sendUseWisdom();
					}
					break;
				case RAIDING_PARTY:
					if (isRaidingPartyAvailable(actingPlayer)) {
						Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
						communication.sendUseSkill(raidingSkill, true, player.getId());
					}
					break;
				case LOOK_INTO_MY_EYES:
					if (isLookIntoMyEyesAvailable(player)) {
						UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
							.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
					}
					break;
				case BALEFUL_HEX:
					if (isBalefulHexAvailable(actingPlayer)) {
						Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
						communication.sendUseSkill(balefulSkill, true, player.getId());
					}
					break;
				case PROJECTILE_VOMIT:
					if (isPutridRegurgitationAvailable()) {
						Skill putridSkill = player.getSkillWithProperty(NamedProperties.canUseVomitAfterBlock);
						communication.sendUseSkill(putridSkill, true, player.getId());
					}
					break;
				case BLACK_INK:
					if (isBlackInkAvailable(actingPlayer)) {
						Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
						communication.sendUseSkill(blackInkSkill, true, player.getId());
					}
					break;
				case CATCH_OF_THE_DAY:
					if (isCatchOfTheDayAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case BOUNDING_LEAP:
					isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
						communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
				default:
					break;
			}
		}
	}

	public boolean isEndPlayerActionAvailable() {
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
			|| isBlackInkAvailable(actingPlayer);
	}

	public boolean isPassAnySquareAvailable(ActingPlayer actingPlayer, Game game) {
		return (PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer());
	}

	public boolean isRangeGridAvailable(ActingPlayer actingPlayer, Game game) {
		return isPassAnySquareAvailable(actingPlayer, game)
			|| showGridForKTM(game, actingPlayer)
			|| ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), true));
	}

	public boolean isMoveAvailable(ActingPlayer actingPlayer) {
		return PlayerAction.GAZE == actingPlayer.getPlayerAction();
	}

	protected boolean showGridForKTM(@SuppressWarnings("unused") Game game, @SuppressWarnings("unused") ActingPlayer actingPlayer) {
		return false;
	}

	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(game.getTurnMode());
	}

	public MoveSquare moveSquare(FieldCoordinate coordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(coordinate);
		FieldCoordinate fromCoordinate = null;
		if (actingPlayer != null) {
			fromCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		}
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		if (moveSquare != null && (actingPlayer == null || !actingPlayer.isJumping() || mechanic.isValidJump(game, actingPlayer.getPlayer(), fromCoordinate, coordinate))) {
			return moveSquare;
		}
		return null;
	}

	public MoveSquare.Kind kind(MoveSquare moveSquare) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (moveSquare.isGoingForIt() && (moveSquare.isDodging() && !actingPlayer.isJumping())) {
			return MoveSquare.Kind.RUSH_DODGE;
		} else if (moveSquare.isGoingForIt()) {
			return MoveSquare.Kind.RUSH;
		} else if (moveSquare.isDodging() && !actingPlayer.isJumping()) {
			return MoveSquare.Kind.DODGE;
		} else {
			return MoveSquare.Kind.MOVE;
		}
	}

	public boolean movePlayer(FieldCoordinate[] pCoordinates) {
		if (!ArrayTool.isProvided(pCoordinates)) {
			return false;
		}
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate coordinateFrom = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (coordinateFrom == null) {
			return false;
		}

		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());

		if (actingPlayer.isJumping() && !mechanic.isValidJump(game, actingPlayer.getPlayer(), coordinateFrom, pCoordinates[pCoordinates.length - 1])) {
			return false;
		}

		sendCommand(actingPlayer, coordinateFrom, pCoordinates);
		return true;
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		client.getCommunication().sendPlayerMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}

  // TODO do we really need both path finder calls?
	public FieldCoordinate[] automovePath(FieldCoordinate coordinate) {
		String automoveProperty = client.getProperty(CommonProperty.SETTING_AUTOMOVE);
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer != null) && (actingPlayer.getPlayerAction() != null)
			&& actingPlayer.getPlayerAction().isMoving() && ArrayTool.isProvided(game.getFieldModel().getMoveSquares())
			&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
			&& (game.getTurnMode() != TurnMode.PASS_BLOCK) && (game.getTurnMode() != TurnMode.KICKOFF_RETURN)
			&& (game.getTurnMode() != TurnMode.SWARMING)
			&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)) {
			return PathFinderWithPassBlockSupport.getShortestPath(game, coordinate);
		}
		return new FieldCoordinate[0];
	}

	public FieldCoordinate[] findShortestPath(FieldCoordinate coordinate) {
		String automoveProperty = client.getProperty(CommonProperty.SETTING_AUTOMOVE);
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer != null
			&& actingPlayer.getPlayerAction() != null
			&& actingPlayer.getPlayerAction().isMoving()
			&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
			&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)
		) {

			Player<?> playerInTarget = game.getFieldModel().getPlayer(coordinate);

			if (actingPlayer.isStandingUp()
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
				actingPlayer.setCurrentMove(Math.min(Constant.MINIMUM_MOVE_TO_STAND_UP,
					actingPlayer.getPlayer().getMovementWithModifiers()));
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				// go-for-it
			}

			if (playerInTarget != null && playerInTarget.getTeam() != actingPlayer.getPlayer().getTeam()) {
				return PathFinderWithPassBlockSupport.getShortestPathToPlayer(game, playerInTarget);
			} else {
				return PathFinderWithPassBlockSupport.getShortestPath(game, coordinate);
			}
		}
		return new FieldCoordinate[0];
	}

	@Override
	public PlayerInteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate position = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (player == actingPlayer.getPlayer()) {
			JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
			if (actingPlayer.hasActed() || mechanic.canJump(game, player, position)
				|| player.hasSkillProperty(NamedProperties.inflictsConfusion)
				|| isSpecialAbilityAvailable(actingPlayer)
				|| (player.hasSkillProperty(NamedProperties.canDropBall) && UtilPlayer.hasBall(game, player))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) && UtilPlayer.hasBall(game, player))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE) && UtilPlayer.hasBall(game, player))
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE)) {
				return new PlayerInteractionResult(PlayerInteractionResult.Kind.SHOW_ACTIONS);
			} else {
				return new PlayerInteractionResult(PlayerInteractionResult.Kind.DESELECT);
			}
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
			if (moveSquare != null) {
				return new PlayerInteractionResult(PlayerInteractionResult.Kind.MOVE, position);
			}
		}
		return new PlayerInteractionResult(PlayerInteractionResult.Kind.IGNORE);
	}
}
