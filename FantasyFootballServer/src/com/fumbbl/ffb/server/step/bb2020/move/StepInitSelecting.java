package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandBlock;
import com.fumbbl.ffb.net.commands.ClientCommandFoul;
import com.fumbbl.ffb.net.commands.ClientCommandGaze;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandKickTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
import com.fumbbl.ffb.net.commands.ClientCommandSetBlockTargetSelection;
import com.fumbbl.ffb.net.commands.ClientCommandSynchronousMultiBlock;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandUnsetBlockTargetSelection;
import com.fumbbl.ffb.report.bb2020.ReportFumblerooskie;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerConstant;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step to init the select sequence.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter UPDATE_PERSISTENCE.
 * <p>
 * Sets stepParameter BLOCK_DEFENDER_ID for all steps on the stack. Sets
 * stepParameter DISPATCH_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * FOUL_DEFENDER_ID for all steps on the stack. Sets stepParameter
 * GAZE_VICTIM_ID for all steps on the stack. Sets stepParameter HAIL_MARY_PASS
 * for all steps on the stack. Sets stepParameter MOVE_STACK for all steps on
 * the stack. Sets stepParameter TARGET_COORDINATE for all steps on the stack.
 * Sets stepParameter USING_STAB for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepInitSelecting extends AbstractStep {

	private String fGotoLabelOnEnd;
	private PlayerAction fDispatchPlayerAction;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean forceGotoOnDispatch;

	private transient boolean fUpdatePersistence;

	public StepInitSelecting(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_SELECTING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_END:
						fGotoLabelOnEnd = (String) parameter.getValue();
						break;
					// mandatory
					case UPDATE_PERSISTENCE:
						fUpdatePersistence = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			boolean homeCommand = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand);
			switch (pReceivedCommand.getId()) {
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					Player<?> selectedPlayer = game.getPlayerById(actingPlayerCommand.getPlayerId());
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())
						&& game.getActingTeam() == selectedPlayer.getTeam()) {
						if (actingPlayerCommand.getPlayerAction() == PlayerAction.BLITZ_MOVE && game.getFieldModel().getBlitzState() == null) {
							fDispatchPlayerAction = PlayerAction.BLITZ_SELECT;
							UtilServerGame.changeActingPlayer(this, actingPlayerCommand.getPlayerId(), actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
							forceGotoOnDispatch = true;
						} else {
							UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
								actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
						}
					} else {
						fEndPlayerAction = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_MOVE:
					ClientCommandMove moveCommand = (ClientCommandMove) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), moveCommand)
						&& UtilServerPlayerMove.isValidMove(getGameState(), moveCommand, homeCommand)) {
						publishParameter(new StepParameter(StepParameterKey.MOVE_START, UtilServerPlayerMove.fetchFromSquare(moveCommand, homeCommand)));
						publishParameter(new StepParameter(StepParameterKey.MOVE_STACK,
							UtilServerPlayerMove.fetchMoveStack(moveCommand, homeCommand)));
						fDispatchPlayerAction = PlayerAction.MOVE;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_BLITZ_MOVE:
					ClientCommandBlitzMove blitzMoveCommand = (ClientCommandBlitzMove) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blitzMoveCommand)
						&& UtilServerPlayerMove.isValidMove(getGameState(), blitzMoveCommand, homeCommand)) {
						publishParameter(new StepParameter(StepParameterKey.MOVE_START, UtilServerPlayerMove.fetchFromSquare(blitzMoveCommand, homeCommand)));
						publishParameter(new StepParameter(StepParameterKey.MOVE_STACK,
							UtilServerPlayerMove.fetchMoveStack(blitzMoveCommand, homeCommand)));
						fDispatchPlayerAction = PlayerAction.BLITZ_MOVE;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_FOUL:
					ClientCommandFoul foulCommand = (ClientCommandFoul) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), foulCommand)
						&& !game.getTurnData().isFoulUsed()) {
						publishParameter(new StepParameter(StepParameterKey.FOUL_DEFENDER_ID, foulCommand.getDefenderId()));
						publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, foulCommand.isUsingChainsaw()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.FOUL, false);
						fDispatchPlayerAction = PlayerAction.FOUL;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_BLOCK:
					ClientCommandBlock blockCommand = (ClientCommandBlock) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blockCommand)) {
						publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockCommand.getDefenderId()));
						publishParameter(new StepParameter(StepParameterKey.USING_STAB, blockCommand.isUsingStab()));
						publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, blockCommand.isUsingChainsaw()));
						publishParameter(new StepParameter(StepParameterKey.USING_VOMIT, blockCommand.isUsingVomit()));
						if (game.getFieldModel().getBlitzState() != null) {
							fDispatchPlayerAction = PlayerAction.BLITZ;
						} else {
							fDispatchPlayerAction = PlayerAction.BLOCK;
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_GAZE:
					ClientCommandGaze gazeCommand = (ClientCommandGaze) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), gazeCommand)) {
						publishParameter(new StepParameter(StepParameterKey.GAZE_VICTIM_ID, gazeCommand.getVictimId()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.GAZE, false);
						fDispatchPlayerAction = PlayerAction.GAZE;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_PASS:
					ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
					boolean passAllowed = !game.getTurnData().isPassUsed()
						|| ((actingPlayer.getPlayer() != null) && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)
						|| (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB)));
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand) && passAllowed) {
						if (passCommand.getTargetCoordinate() != null) {
							if (game.isHomePlaying()) {
								publishParameter(
									new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate()));
							} else {
								publishParameter(
									new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate().transform()));
							}
						}
						if ((actingPlayer.getPlayer() != null) && ((actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS)
							|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)
							|| (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
							fDispatchPlayerAction = actingPlayer.getPlayerAction();
						} else {
							UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.PASS, false);
							fDispatchPlayerAction = PlayerAction.PASS;
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_HAND_OVER:
					ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand)
						&& !game.getTurnData().isHandOverUsed()) {
						Player<?> catcher = game.getPlayerById(handOverCommand.getCatcherId());
						FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
						publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, catcherCoordinate));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, false);
						fDispatchPlayerAction = PlayerAction.HAND_OVER;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_THROW_TEAM_MATE:
					ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand)
						&& !game.getTurnData().isPassUsed()) {
						if (throwTeamMateCommand.getTargetCoordinate() != null) {
							if (game.isHomePlaying()) {
								publishParameter(
									new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate()));
							} else {
								publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE,
									throwTeamMateCommand.getTargetCoordinate().transform()));
							}
						}
						publishParameter(
							new StepParameter(StepParameterKey.THROWN_PLAYER_ID, throwTeamMateCommand.getThrownPlayerId()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE, false);
						fDispatchPlayerAction = PlayerAction.THROW_TEAM_MATE;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_KICK_TEAM_MATE:
					ClientCommandKickTeamMate kickTeamMateCommand = (ClientCommandKickTeamMate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), kickTeamMateCommand)
						&& !game.getTurnData().isBlitzUsed()) {
						if (kickTeamMateCommand.getNumDice() != 0) {
							publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, kickTeamMateCommand.getNumDice()));
						}
						publishParameter(
							new StepParameter(StepParameterKey.KICKED_PLAYER_ID, kickTeamMateCommand.getKickedPlayerId()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.KICK_TEAM_MATE, false);
						fDispatchPlayerAction = PlayerAction.KICK_TEAM_MATE;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						fEndTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_SET_BLOCK_TARGET_SELECTION:
					handleSetBlockTarget(getGameState().getGame(), (ClientCommandSetBlockTargetSelection) pReceivedCommand.getCommand());
					break;
				case CLIENT_UNSET_BLOCK_TARGET_SELECTION:
					handleUnsetBlockTarget(getGameState().getGame(), (ClientCommandUnsetBlockTargetSelection) pReceivedCommand.getCommand());
					break;
				case CLIENT_SYNCHRONOUS_MULTI_BLOCK:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						publishParameter(new StepParameter(StepParameterKey.BLOCK_TARGETS,
							((ClientCommandSynchronousMultiBlock) pReceivedCommand.getCommand()).getSelectedTargets()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MULTIPLE_BLOCK, false);
						fDispatchPlayerAction = PlayerAction.MULTIPLE_BLOCK;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_FUMBLEROOSKIE:
					Player<?> player = game.getActingPlayer().getPlayer();
					PlayerAction playerAction = game.getActingPlayer().getPlayerAction();
					if (playerAction != null && playerAction.allowsFumblerooskie() && UtilPlayer.hasBall(game, player)) {
						game.getFieldModel().setBallMoving(true);
						getResult().setSound(SoundId.BOUNCE);
						getResult().addReport(new ReportFumblerooskie(player.getId(), true));
						actingPlayer.setFumblerooskiePending(true);
					}
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public void start() {
		if (fUpdatePersistence) {
			fUpdatePersistence = false;
			GameCache gameCache = getGameState().getServer().getGameCache();
			gameCache.queueDbUpdate(getGameState(), true);
		}
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (game.isTimeoutEnforced() || fEndTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fEndPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fDispatchPlayerAction != null) {
			if (StringTool.isProvided(actingPlayer.getPlayerId()) && (actingPlayer.getPlayerAction() != null)) {
				publishParameter(new StepParameter(StepParameterKey.DISPATCH_PLAYER_ACTION, fDispatchPlayerAction));
				if (actingPlayer.isStandingUp() && !forceGotoOnDispatch) {
					prepareStandingUp();
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
				}
			}
		} else {
			prepareStandingUp();
			if ((actingPlayer.getPlayerAction() == PlayerAction.REMOVE_CONFUSION)
				|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP)
				|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ)) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}

	private void prepareStandingUp() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayer() != null) && (actingPlayer.getPlayerAction() != null)) {
			if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
				|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
				|| (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)) {
				ServerUtilBlock.updateDiceDecorations(game);
			}
			if (actingPlayer.getPlayerAction().isMoving()) {
				if (actingPlayer.isStandingUp()
					&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
					actingPlayer.setCurrentMove(Math.min(IServerConstant.MINIMUM_MOVE_TO_STAND_UP,
						actingPlayer.getPlayer().getMovementWithModifiers()));
					actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
					// go-for-it
				}
				UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
			}
		}
	}

	private void handleSetBlockTarget(Game game, ClientCommandSetBlockTargetSelection command) {
		Player<?> player = game.getPlayerById(command.getPlayerId());
		FieldModel fieldModel = game.getFieldModel();
		PlayerState playerState = fieldModel.getPlayerState(player);
		switch (command.getKind()) {
			case STAB:
				playerState = playerState.changeSelectedStabTarget(true);
				break;
			case CHAINSAW:
				playerState = playerState.changeSelectedChainsawTarget(true);
				break;
			default:
				playerState = playerState.changeSelectedBlockTarget(true);
				break;
		}
		fieldModel.setPlayerState(player, playerState);
	}

	private void handleUnsetBlockTarget(Game game, ClientCommandUnsetBlockTargetSelection command) {
		Player<?> player = game.getPlayerById(command.getPlayerId());
		FieldModel fieldModel = game.getFieldModel();
		PlayerState playerState = fieldModel.getPlayerState(player).changeSelectedStabTarget(false).changeSelectedBlockTarget(false);
		fieldModel.setPlayerState(player, playerState);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.DISPATCH_PLAYER_ACTION.addTo(jsonObject, fDispatchPlayerAction);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.FORCE_GOTO_ON_DISPATCH.addTo(jsonObject, forceGotoOnDispatch);
		return jsonObject;
	}

	@Override
	public StepInitSelecting initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		forceGotoOnDispatch = IServerJsonOption.FORCE_GOTO_ON_DISPATCH.getFrom(game, jsonObject);
		return this;
	}

}
