package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogConfirmEndActionParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.*;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportFumblerooskie;
import com.fumbbl.ffb.report.mixed.ReportStallerDetected;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
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
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepInitSelecting extends AbstractStep {
	private String fGotoLabelOnEnd;
	private PlayerAction fDispatchPlayerAction;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private boolean forceGotoOnDispatch;

	private transient boolean fUpdatePersistence;

	private final StallingExtension stallingExtension = new StallingExtension();

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
			FieldModel fieldModel = game.getFieldModel();
			TargetSelectionState targetSelectionState = fieldModel.getTargetSelectionState();
			switch (pReceivedCommand.getId()) {
				case CLIENT_CONFIRM: // confirms ending target select action like blitz or gaze
					fEndPlayerAction = true;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					Player<?> selectedPlayer = game.getPlayerById(actingPlayerCommand.getPlayerId());
					PlayerState playerState = game.getFieldModel().getPlayerState(selectedPlayer);
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())
						&& game.getActingTeam() == selectedPlayer.getTeam() && playerState != null && playerState.isActive()) {
						PlayerAction playerAction = actingPlayerCommand.getPlayerAction();
						if (playerAction == PlayerAction.BLITZ_MOVE && targetSelectionState == null) {
							fDispatchPlayerAction = PlayerAction.BLITZ_SELECT;
							UtilServerGame.changeActingPlayer(this, actingPlayerCommand.getPlayerId(), playerAction, actingPlayerCommand.isJumping());
							forceGotoOnDispatch = true;
						} else {
							if (playerAction != null && playerAction.isStandingUp()) {
								actingPlayer.setStandingUp(true);
							}
							PassState passState = getGameState().getPassState();
							if (playerAction != null && playerAction.isBomb()) {
								passState.setOriginalBombardier(actingPlayerCommand.getPlayerId());
								if (playerAction == PlayerAction.ALL_YOU_CAN_EAT) {
									passState.setThrowTwoBombs(true);
								}
							} else {
								passState.reset();
							}

							forceGotoOnDispatch = playerAction != null && playerAction.forceDispatch();
							if (forceGotoOnDispatch) {
								fDispatchPlayerAction = playerAction;
							}

							UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
								playerAction, actingPlayerCommand.isJumping());

							checkForStaller();
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;

					} else if (!StringTool.isProvided(actingPlayerCommand.getPlayerId()) && actingPlayer.getPlayerId() != null) {
						boolean unusedBlitz = actingPlayer.getPlayerAction().isBlitzing() && !actingPlayer.hasBlocked();
						boolean unusedGaze = actingPlayer.getPlayerAction().isGaze() && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.inflictsConfusion);
						boolean onlyMarkedAsStandingUp = actingPlayer.isStandingUp() && actingPlayer.getCurrentMove() == Constant.MINIMUM_MOVE_TO_STAND_UP;
						if (targetSelectionState != null
							&& (unusedBlitz || unusedGaze)
							&& (actingPlayer.getCurrentMove() == 0 || onlyMarkedAsStandingUp)) {
							if (targetSelectionState.isCommitted()) {
								UtilServerDialog.showDialog(getGameState(), new DialogConfirmEndActionParameter(game.getActingTeam().getId(), actingPlayer.getPlayerAction()), false);
								commandStatus = StepCommandStatus.SKIP_STEP;
							} else {
								if (actingPlayer.getPlayerAction().isBlitzing()) {
									game.getTurnData().setBlitzUsed(false);
								}
								if (targetSelectionState.getOldActingPlayerState() != null) {
									PlayerState oldState = targetSelectionState.getOldActingPlayerState();
									if (onlyMarkedAsStandingUp) {
										oldState = oldState.changeBase(PlayerState.PRONE);
									}
									fieldModel.setPlayerState(actingPlayer.getPlayer(), oldState);
								}
								actingPlayer.setHasMoved(false);
								actingPlayer.setCurrentMove(0);
								actingPlayer.setStandingUp(false);
								getGameState().resetStalling();
								fEndPlayerAction = true;
								commandStatus = StepCommandStatus.EXECUTE_STEP;
							}
						} else {
							getGameState().resetStalling();
							fEndPlayerAction = true;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}

					} else {
						commandStatus = StepCommandStatus.SKIP_STEP;
					}
					break;
				case CLIENT_MOVE:
					ClientCommandMove moveCommand = (ClientCommandMove) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), moveCommand)
						&& UtilServerPlayerMove.isValidMove(getGameState(), moveCommand, homeCommand)) {
						publishParameter(new StepParameter(StepParameterKey.MOVE_START, UtilServerPlayerMove.fetchFromSquare(moveCommand, homeCommand)));
						publishParameter(new StepParameter(StepParameterKey.MOVE_STACK,
							UtilServerPlayerMove.fetchMoveStack(moveCommand, homeCommand)));
						publishParameter(new StepParameter(StepParameterKey.BALL_AND_CHAIN_RE_ROLL_SETTING, moveCommand.getBallAndChainRrSetting()));
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
						&& (!game.getTurnData().isFoulUsed() || actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul))) {
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
						publishParameter(new StepParameter(StepParameterKey.USING_BREATHE_FIRE, blockCommand.isUsingBreatheFire()));
						publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
						if (targetSelectionState != null) {
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
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand)) {
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
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand)) {
						Player<?> catcher = game.getPlayerById(handOverCommand.getCatcherId());
						FieldCoordinate catcherCoordinate = fieldModel.getPlayerCoordinate(catcher);
						publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, catcherCoordinate));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, false);
						fDispatchPlayerAction = PlayerAction.HAND_OVER;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_THROW_TEAM_MATE:
					ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand)
						&& (!game.getTurnData().isTtmUsed() || throwTeamMateCommand.isKicked())) {
						if (throwTeamMateCommand.getTargetCoordinate() != null) {
							if (game.isHomePlaying()) {
								publishParameter(
									new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate()));
							} else {
								publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE,
									throwTeamMateCommand.getTargetCoordinate().transform()));
							}
						}
						PlayerAction ttmAction = throwTeamMateCommand.isKicked() ? PlayerAction.KICK_TEAM_MATE : PlayerAction.THROW_TEAM_MATE;
						publishParameter(
							new StepParameter(StepParameterKey.THROWN_PLAYER_ID, throwTeamMateCommand.getThrownPlayerId()));
						publishParameter(new StepParameter(StepParameterKey.IS_KICKED_PLAYER, throwTeamMateCommand.isKicked()));
						UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), ttmAction, false);
						fDispatchPlayerAction = ttmAction;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_END_TURN:
					TurnMode turnMode = ((ClientCommandEndTurn) pReceivedCommand.getCommand()).getTurnMode();
					boolean ignoreCommand = turnMode != null && turnMode != game.getTurnMode();

					if (!ignoreCommand && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
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
						fieldModel.setBallMoving(true);
						getResult().setSound(SoundId.BOUNCE);
						getResult().addReport(new ReportFumblerooskie(player.getId(), true));
						actingPlayer.setFumblerooskiePending(true);
					}
					break;
				case CLIENT_USE_SKILL:
					commandStatus = StepCommandStatus.SKIP_STEP;
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (commandUseSkill.isSkillUsed()) {
						if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canGainHailMary)) {
							game.getFieldModel().addSkillEnhancements(actingPlayer.getPlayer(), commandUseSkill.getSkill());
							getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), commandUseSkill.getSkill(), true, SkillUse.GAIN_HAIL_MARY));
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canStabTeamMateForBall)) {
							fDispatchPlayerAction = PlayerAction.TREACHEROUS;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMoveOpenTeamMate)) {
							fDispatchPlayerAction = PlayerAction.RAIDING_PARTY;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canStealBallFromOpponent)) {
							fDispatchPlayerAction = PlayerAction.LOOK_INTO_MY_EYES;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMakeOpponentMissTurn)) {
							fDispatchPlayerAction = PlayerAction.BALEFUL_HEX;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canGetBallOnGround)) {
							fDispatchPlayerAction = PlayerAction.CATCH_OF_THE_DAY;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canBlastRemotePlayer)) {
							fDispatchPlayerAction = PlayerAction.THEN_I_STARTED_BLASTIN;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAddBlockDie)) {
							if (targetSelectionState != null && UtilCards.hasUnusedSkill(actingPlayer, commandUseSkill.getSkill())) {
								FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
								FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
								DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
								Player<?> defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
								boolean opponentCanMove = UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.canMoveBeforeBeingBlocked);
								if (diceDecoration != null && (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2 || (diceDecoration.getNrOfDice() == 3 && opponentCanMove)) && targetCoordinate.isAdjacent(playerCoordinate)) {
									targetSelectionState.addUsedSkill(commandUseSkill.getSkill());
									getResult().addReport(new ReportSkillUse(commandUseSkill.getSkill(), true, SkillUse.ADD_BLOCK_DIE));
									ServerUtilBlock.updateDiceDecorations(getGameState());
								}
								publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, targetSelectionState.getSelectedPlayerId()));
								publishParameter(new StepParameter(StepParameterKey.USING_STAB, false));
								publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, false));
								publishParameter(new StepParameter(StepParameterKey.USING_VOMIT, false));
								publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, false));
								fDispatchPlayerAction = PlayerAction.BLITZ;

								commandStatus = StepCommandStatus.EXECUTE_STEP;
							}
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canGazeAutomatically)) {
							fDispatchPlayerAction = PlayerAction.BLACK_INK;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canIgnoreJumpModifiers)) {
							actingPlayer.setJumpsWithoutModifiers(true);
							UtilServerPlayerMove.updateMoveSquares(getGameState(), true);
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canGazeAutomaticallyThreeSquaresAway)) {
							fDispatchPlayerAction = PlayerAction.AUTO_GAZE_ZOAT;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
							forceGotoOnDispatch = true;
						} 
					}
					break;
				case CLIENT_USE_TEAM_MATES_WISDOM:
					fDispatchPlayerAction = PlayerAction.WISDOM_OF_THE_WHITE_DWARF;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					forceGotoOnDispatch = true;
					break;
				case CLIENT_THROW_KEG:
					publishParameter(StepParameter.from(StepParameterKey.TARGET_PLAYER_ID, ((ClientCommandThrowKeg) pReceivedCommand.getCommand()).getPlayerId()));
					fDispatchPlayerAction = PlayerAction.THROW_KEG;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
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
					if (forceGotoOnDispatch) {
						prepareStandingUp();
					}
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
		PlayerAction playerAction = actingPlayer.getPlayerAction();
		if ((actingPlayer.getPlayer() != null) && (playerAction != null)) {
			if ((playerAction == PlayerAction.BLITZ)
				|| (playerAction == PlayerAction.BLITZ_MOVE)
				|| (playerAction == PlayerAction.KICK_EM_BLITZ)
				|| (playerAction.isBlockAction())
				|| (playerAction == PlayerAction.MULTIPLE_BLOCK)) {
				ServerUtilBlock.updateDiceDecorations(getGameState());
			}
			if (playerAction.isMoving() || playerAction.isStandingUp()) {
				if (actingPlayer.isStandingUp()
					&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
					actingPlayer.setCurrentMove(Math.min(Constant.MINIMUM_MOVE_TO_STAND_UP,
						actingPlayer.getPlayer().getMovementWithModifiers()));
					actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
					// go-for-it
				}
				UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
			}
		}
	}

	private void handleSetBlockTarget(Game game, ClientCommandSetBlockTargetSelection command) {
		FieldModel fieldModel = game.getFieldModel();

		fieldModel.addMultiBlockTarget(command.getPlayerId(), command.getKind());
		ServerUtilBlock.updateDiceDecorations(getGameState());
	}

	private void handleUnsetBlockTarget(Game game, ClientCommandUnsetBlockTargetSelection command) {
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.removeMultiBlockTarget(command.getPlayerId());
		ServerUtilBlock.updateDiceDecorations(getGameState());
	}

	private void checkForStaller() {
		if (((GameOptionBoolean) getGameState().getGame().getOptions()
			.getOptionWithDefault(GameOptionId.ENABLE_STALLING_CHECK)).isEnabled() && !getGameState().isStalling() && isConsideredStalling()) {
			getResult().addReport(new ReportStallerDetected(getGameState().getGame().getActingPlayer().getPlayerId()));
			getGameState().stallingDetected();
		}
	}

	private boolean isConsideredStalling() {
		Game game = getGameState().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();

		return stallingExtension.isConsideredStalling(game, player);
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
	public StepInitSelecting initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		forceGotoOnDispatch = IServerJsonOption.FORCE_GOTO_ON_DISPATCH.getFrom(source, jsonObject);
		return this;
	}

}
