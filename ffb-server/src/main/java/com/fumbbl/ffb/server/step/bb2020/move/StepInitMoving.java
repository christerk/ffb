package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandBlock;
import com.fumbbl.ffb.net.commands.ClientCommandFoul;
import com.fumbbl.ffb.net.commands.ClientCommandGaze;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportFumblerooskie;
import com.fumbbl.ffb.server.GameState;
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
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Objects;

/**
 * Step to init the move sequence.
 * <p>
 * Needs to be initialized with stepParameter DISPATCH_TO_LABEL. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_END. May be initialized with
 * stepParameter GAZE_VICTIM_ID. May be initialized with stepParameter
 * MOVE_STACK.
 * <p>
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 * <p>
 * Sets stepParameter COORDINATE_FROM for all steps on the stack. Sets
 * stepParameter COORDINATE_TO for all steps on the stack. Sets stepParameter
 * DISPATCH_PLAYER_ACTION for all steps on the stack. Sets stepParameter
 * END_TURN for all steps on the stack. Sets stepParameter END_PLAYER_ACTION for
 * all steps on the stack. Sets stepParameter MOVE_STACK for all steps on the
 * stack.
 * <p>
 * May replace rest of move sequence with inducement sequence.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepInitMoving extends AbstractStep {

	private String fGotoLabelOnEnd;
	private FieldCoordinate[] fMoveStack;
	private String fGazeVictimId;
	private boolean fEndTurn, fEndPlayerAction;

	public StepInitMoving(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_MOVING;
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
					// optional
					case GAZE_VICTIM_ID:
						fGazeVictimId = (String) parameter.getValue();
						break;
					// optional
					case MOVE_STACK:
						fMoveStack = (FieldCoordinate[]) parameter.getValue();
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
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (Objects.requireNonNull(parameter.getKey()) == StepParameterKey.MOVE_STACK) {
				fMoveStack = (FieldCoordinate[]) parameter.getValue();
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pReceivedCommand.getId()) {
				case CLIENT_BLITZ_MOVE:
					ClientCommandBlitzMove blitzMoveCommand = (ClientCommandBlitzMove) pReceivedCommand.getCommand();
					boolean homePlayerBlitz = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand);
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blitzMoveCommand)
						&& UtilServerPlayerMove.isValidMove(getGameState(), blitzMoveCommand, homePlayerBlitz)) {
						publishParameter(new StepParameter(StepParameterKey.MOVE_START, UtilServerPlayerMove.fetchFromSquare(blitzMoveCommand, homePlayerBlitz)));
						if (!ArrayTool.isProvided(fMoveStack)) {
							publishParameter(new StepParameter(StepParameterKey.MOVE_STACK,
								UtilServerPlayerMove.fetchMoveStack(blitzMoveCommand, homePlayerBlitz)));
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_MOVE:
					ClientCommandMove moveCommand = (ClientCommandMove) pReceivedCommand.getCommand();
					boolean homePlayer = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand);
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), moveCommand)
						&& UtilServerPlayerMove.isValidMove(getGameState(), moveCommand, homePlayer)) {
						publishParameter(new StepParameter(StepParameterKey.MOVE_START, UtilServerPlayerMove.fetchFromSquare(moveCommand, homePlayer)));
						if (!ArrayTool.isProvided(fMoveStack)) {
							publishParameter(new StepParameter(StepParameterKey.MOVE_STACK,
								UtilServerPlayerMove.fetchMoveStack(moveCommand, homePlayer)));
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_BLOCK:
					ClientCommandBlock blockCommand = (ClientCommandBlock) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blockCommand)) {
						if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE || actingPlayer.getPlayerAction() == PlayerAction.KICK_EM_BLITZ) && !actingPlayer.hasBlocked()
							|| actingPlayer.getPlayerAction() == PlayerAction.PUTRID_REGURGITATION_BLITZ) {
							if (actingPlayer.getPlayerAction() == PlayerAction.KICK_EM_BLITZ) {
								commandStatus = dispatchPlayerAction(PlayerAction.KICK_EM_BLITZ);
							} else {
								commandStatus = dispatchPlayerAction(PlayerAction.BLITZ);
							}
							publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, blockCommand.isUsingChainsaw()));
							publishParameter(new StepParameter(StepParameterKey.USING_VOMIT, blockCommand.isUsingVomit()));
						}
					}
					break;
				case CLIENT_FOUL:
					ClientCommandFoul foulCommand = (ClientCommandFoul) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), foulCommand)
						&& (actingPlayer.getPlayerAction() == PlayerAction.FOUL_MOVE) && !actingPlayer.hasFouled()) {
						commandStatus = dispatchPlayerAction(PlayerAction.FOUL);
					}
					break;
				case CLIENT_HAND_OVER:
					ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand)
						&& ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE)
						|| (actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER))) {
						commandStatus = dispatchPlayerAction(PlayerAction.HAND_OVER);
					}
					break;
				case CLIENT_PASS:
					ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand)) {
						if (((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE)
							|| (actingPlayer.getPlayerAction() == PlayerAction.PASS))) {
							commandStatus = dispatchPlayerAction(PlayerAction.PASS);
						}
						if (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS) {
							commandStatus = dispatchPlayerAction(PlayerAction.HAIL_MARY_PASS);
						}
					}
					break;
				case CLIENT_THROW_TEAM_MATE:
					ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand)
						&& (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE || actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)) {

						publishParameter(
							new StepParameter(StepParameterKey.THROWN_PLAYER_ID, throwTeamMateCommand.getThrownPlayerId()));
						PlayerAction ttmAction = throwTeamMateCommand.isKicked() ? PlayerAction.KICK_TEAM_MATE : PlayerAction.THROW_TEAM_MATE;
						commandStatus = dispatchPlayerAction(ttmAction);
					}
					break;
				case CLIENT_GAZE:
					ClientCommandGaze gazeCommand = (ClientCommandGaze) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), gazeCommand)) {
						fGazeVictimId = gazeCommand.getVictimId();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
						UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
							actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
						if (actingPlayer.getPlayerAction() == PlayerAction.PUTRID_REGURGITATION_BLITZ) {
							// we have to reset this here since other logic would otherwise prevent the vomit attack
							// when the target does not match the selection state data
							game.getFieldModel().setTargetSelectionState(null);
						}
					} else {
						fEndPlayerAction = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						fEndTurn = true;
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
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					Skill skill = clientCommandUseSkill.getSkill();
					TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
					commandStatus = StepCommandStatus.SKIP_STEP;
					if (targetSelectionState != null && skill.hasSkillProperty(NamedProperties.canAddBlockDie) && UtilCards.hasUnusedSkill(actingPlayer, skill)) {
						FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
						FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
						DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
						Player<?> defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
						boolean opponentCanMove = UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.canMoveBeforeBeingBlocked);
						if (diceDecoration != null && (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2 || (diceDecoration.getNrOfDice() == 3 && opponentCanMove)) && targetCoordinate.isAdjacent(playerCoordinate)) {
							targetSelectionState.addUsedSkill(skill);
							getResult().addReport(new ReportSkillUse(skill, true, SkillUse.ADD_BLOCK_DIE));
							ServerUtilBlock.updateDiceDecorations(game);
						}
						if (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE && !actingPlayer.hasBlocked()) {

							commandStatus = dispatchPlayerAction(PlayerAction.BLITZ);

							publishParameter(new StepParameter(StepParameterKey.USING_CHAINSAW, false));
							publishParameter(new StepParameter(StepParameterKey.USING_VOMIT, false));
						}

					} else if (skill.hasSkillProperty(NamedProperties.canIgnoreJumpModifiers)) {
						actingPlayer.setJumpsWithoutModifiers(true);
						UtilServerPlayerMove.updateMoveSquares(getGameState(), true);
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

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fEndTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (fEndPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
		} else if (StringTool.isProvided(fGazeVictimId)) {
			game.setDefenderId(fGazeVictimId);
			actingPlayer.setPlayerAction(PlayerAction.GAZE);
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (ArrayTool.isProvided(fMoveStack)) {
				FieldCoordinate coordinateTo = fMoveStack[0];
				FieldCoordinate[] newMoveStack = new FieldCoordinate[0];
				if (fMoveStack.length > 1) {
					newMoveStack = new FieldCoordinate[fMoveStack.length - 1];
					System.arraycopy(fMoveStack, 1, newMoveStack, 0, newMoveStack.length);
				}
				publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, newMoveStack));
				if (FieldCoordinateBounds.FIELD.isInBounds(coordinateTo)) {
					FieldCoordinate coordinateFrom = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
					publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, coordinateFrom));
					publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, coordinateTo));
					MoveSquare moveSquare = game.getFieldModel().getMoveSquare(coordinateTo);
					actingPlayer.setDodging((moveSquare != null) && moveSquare.isDodging() && !actingPlayer.isJumping());
					actingPlayer.setGoingForIt((moveSquare != null) && moveSquare.isGoingForIt());
					actingPlayer.setHasMoved(true);
					game.getTurnData().setTurnStarted(true);
					switch (actingPlayer.getPlayerAction()) {
						case BLITZ_MOVE:
						case KICK_EM_BLITZ:
							game.getTurnData().setBlitzUsed(true);
							break;
						case FOUL_MOVE:
							if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
								game.getTurnData().setFoulUsed(true);
							}
							break;
						case HAND_OVER_MOVE:
							game.getTurnData().setHandOverUsed(true);
							break;
						case PASS_MOVE:
						case THROW_TEAM_MATE_MOVE:
							game.getTurnData().setPassUsed(true);
							break;
						case KICK_TEAM_MATE_MOVE:
							game.getTurnData().setKtmUsed(true);
							break;
						default:
							break;
					}
					game.setConcessionPossible(false);
					getResult().setNextAction(StepAction.NEXT_STEP);
				}
			}
		}
	}

	private StepCommandStatus dispatchPlayerAction(PlayerAction pPlayerAction) {
		publishParameter(new StepParameter(StepParameterKey.DISPATCH_PLAYER_ACTION, pPlayerAction));
		getResult().setNextAction(StepAction.GOTO_LABEL_AND_REPEAT, fGotoLabelOnEnd);
		return StepCommandStatus.SKIP_STEP;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.MOVE_STACK.addTo(jsonObject, fMoveStack);
		IServerJsonOption.GAZE_VICTIM_ID.addTo(jsonObject, fGazeVictimId);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		return jsonObject;
	}

	@Override
	public StepInitMoving initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(source, jsonObject);
		fGazeVictimId = IServerJsonOption.GAZE_VICTIM_ID.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		return this;
	}

}
