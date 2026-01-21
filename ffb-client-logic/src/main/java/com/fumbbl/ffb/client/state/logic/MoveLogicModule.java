package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.util.pathfinding.PathFinderWithPassBlockSupport;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MoveLogicModule extends LogicModule {

	public MoveLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.MOVE;
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
			add(ClientAction.BLACK_INK);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.BLOCK);
			add(ClientAction.CATCH_OF_THE_DAY);
			add(ClientAction.BOUNDING_LEAP);
			add(ClientAction.THEN_I_STARTED_BLASTIN);
			add(ClientAction.AUTO_GAZE_ZOAT);
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
					if (PlayerAction.PASS == actingPlayer.getPlayerAction() || PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) {
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
					break;
				case THEN_I_STARTED_BLASTIN:
					if (isThenIStartedBlastinAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case AUTO_GAZE_ZOAT:
					if (isZoatGazeAvailable(actingPlayer)) {
						Skill zoatGazeInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomaticallyThreeSquaresAway);
						client.getCommunication().sendUseSkill(zoatGazeInkSkill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(game.getTurnMode());
	}

	private MoveSquare moveSquare(FieldCoordinate coordinate) {
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

	protected boolean movePlayer(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return false;
		}
		return movePlayer(new FieldCoordinate[]{pCoordinate});
	}

	private boolean movePlayer(FieldCoordinate[] pCoordinates) {
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

		FieldCoordinate lastCoordinate = pCoordinates[pCoordinates.length - 1];
		if (actingPlayer.isJumping() && !mechanic.isValidJump(game, actingPlayer.getPlayer(), coordinateFrom, lastCoordinate)) {
			return false;
		}

		// Clean up the coordinates if we are jumping (only the last coordinate matters).
		// This mitigates a bug where a move-path was sent while jumping, resulting in additional dodge rolls
		// and spending more movement than necessary.
		// The source of this bug is so far unknown, but we want to prevent the occurrence of the bug.
		FieldCoordinate[] cleanedCoordinates = pCoordinates;

		if (actingPlayer.isJumping()) {
			cleanedCoordinates = new FieldCoordinate[]{lastCoordinate};
		}

		sendCommand(actingPlayer, coordinateFrom, cleanedCoordinates);
		return true;
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		client.getCommunication().sendPlayerMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates, client.getProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN));
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
			return PathFinderWithPassBlockSupport.INSTANCE.getShortestPath(game, coordinate);
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
				return PathFinderWithPassBlockSupport.INSTANCE.getShortestPathToPlayer(game, playerInTarget);
			} else {
				return PathFinderWithPassBlockSupport.INSTANCE.getShortestPath(game, coordinate);
			}
		}
		return new FieldCoordinate[0];
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate position = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (player == actingPlayer.getPlayer()) {
			JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
			if (actingPlayer.hasActed() || mechanic.canJump(game, player, position)
				|| player.hasSkillProperty(NamedProperties.canGazeDuringMove)
				|| isSpecialAbilityAvailable(actingPlayer)
				|| (player.hasSkillProperty(NamedProperties.canDropBall) && UtilPlayer.hasBall(game, player))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) && UtilPlayer.hasBall(game, player))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE) && UtilPlayer.hasBall(game, player))
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE)) {
				return InteractionResult.selectAction(actionContext(actingPlayer));
			} else {
				deselectActingPlayer();
				return InteractionResult.handled();
			}
		} else {
			// B&C
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
			if (moveSquare != null && movePlayer(playerCoordinate)) {
				return InteractionResult.perform().with(playerCoordinate);
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		MoveSquare moveSquare = moveSquare(coordinate);
		FieldCoordinate[] movePath = automovePath(coordinate);
		if (moveSquare != null) {
			movePlayer(coordinate);
			return InteractionResult.handled();
		} else if (ArrayTool.isProvided(movePath)) {
			movePlayer(movePath);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		MoveSquare moveSquare = moveSquare(coordinate);
		if (moveSquare != null) {
			return InteractionResult.perform().with(moveSquare);
		} else {
			FieldCoordinate[] shortestPath = automovePath(coordinate);
			if (ArrayTool.isProvided(shortestPath)) {
				return InteractionResult.perform().with(shortestPath);
			}
		}
		return InteractionResult.reset();
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext context = new ActionContext();
		Game game = client.getGame();
		if (isPassAnySquareAvailable(actingPlayer, game)) {
			context.add(ClientAction.PASS);
		}

		if (isMoveAvailable(actingPlayer)) {
			context.add(ClientAction.MOVE);
		}
		if (isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			context.add(ClientAction.JUMP);
			if (actingPlayer.isJumping()) {
				context.add(Influences.IS_JUMPING);
			} else {
				Optional<Skill> boundingLeap = isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					context.add(ClientAction.BOUNDING_LEAP);
				}
			}
		}
		if (isHypnoticGazeActionAvailable(false, actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
			context.add(ClientAction.GAZE);
		}
		if (isFumblerooskieAvailable()) {
			context.add(ClientAction.FUMBLEROOSKIE);
		}
		if (isEndPlayerActionAvailable()) {
			if (playerActivationUsed()) {
				context.add(Influences.HAS_ACTED);
			}
			context.add(ClientAction.END_MOVE);
		}
		if (isTreacherousAvailable(actingPlayer)) {
			context.add(ClientAction.TREACHEROUS);
		}
		if (isWisdomAvailable(actingPlayer)) {
			context.add(ClientAction.WISDOM);
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			context.add(ClientAction.RAIDING_PARTY);
		}
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			context.add(ClientAction.LOOK_INTO_MY_EYES);
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			context.add(ClientAction.BALEFUL_HEX);
		}
		if (isPutridRegurgitationAvailable()) {
			context.add(Influences.VOMIT_DUE_TO_PUTRID_REGURGITATION);
			context.add(ClientAction.PROJECTILE_VOMIT);
		}
		if (isBlackInkAvailable(actingPlayer)) {
			context.add(ClientAction.BLACK_INK);
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			context.add(ClientAction.CATCH_OF_THE_DAY);
		}
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			context.add(ClientAction.THEN_I_STARTED_BLASTIN);
		}
		if (isZoatGazeAvailable(actingPlayer)) {
			context.add(ClientAction.AUTO_GAZE_ZOAT);
		}
		return context;
	}

}
