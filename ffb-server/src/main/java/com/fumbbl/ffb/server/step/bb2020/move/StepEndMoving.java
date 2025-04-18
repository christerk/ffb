package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.generator.*;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

/**
 * Last step in move sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter BLOCK_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter DISPATCH_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step. Expects
 * stepParameter FEEDING_ALLOWED to be set by a preceding step. Expects
 * stepParameter MOVE_STACK to be set by a preceding step.
 * <p>
 * May push a new sequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndMoving extends AbstractStep {

	private boolean fEndTurn, fEndPlayerAction, usingChainsaw;
	private Boolean fFeedingAllowed;
	private FieldCoordinate[] fMoveStack;
	private FieldCoordinate moveStart;
	private PlayerAction fDispatchPlayerAction;
	private String fBlockDefenderId, thrownPlayerId;

	public StepEndMoving(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_MOVING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(this::setParameter);
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case BLOCK_DEFENDER_ID:
					fBlockDefenderId = (String) parameter.getValue();
					consume(parameter);
					return true;
				case DISPATCH_PLAYER_ACTION:
					fDispatchPlayerAction = (PlayerAction) parameter.getValue();
					consume(parameter);
					return true;
				case END_PLAYER_ACTION:
					fEndPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case END_TURN:
					fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case FEEDING_ALLOWED:
					fFeedingAllowed = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case MOVE_START:
					moveStart = (FieldCoordinate) parameter.getValue();
					consume(parameter);
					return true;
				case MOVE_STACK:
					fMoveStack = (FieldCoordinate[]) parameter.getValue();
					consume(parameter);
					return true;
				case USING_CHAINSAW:
					usingChainsaw = parameter.getValue() != null && (boolean) parameter.getValue();
					consume(parameter);
					return true;
				case THROWN_PLAYER_ID:
					thrownPlayerId = (String) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				// commands redirected from initMoving
				// add proper sequence to stack, repeat command once more -->
				case CLIENT_BLOCK:
				case CLIENT_FOUL:
				case CLIENT_HAND_OVER:
				case CLIENT_PASS:
				case CLIENT_THROW_TEAM_MATE:
				case CLIENT_KICK_TEAM_MATE:
					commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill useSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (useSkill.isSkillUsed() && useSkill.getSkill().hasSkillProperty(NamedProperties.canAddBlockDie)) {
						commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
					}
				default:
					break;
				// <--
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		if (fFeedingAllowed == null) {
			fFeedingAllowed = true; // feeding allowed if not specified otherwise
		}
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
		BlitzMove blitzMoveGenerator = (BlitzMove) factory.forName(SequenceGenerator.Type.BlitzMove.name());

		boolean adjacentTarget = false;
		if (game.getFieldModel().getTargetSelectionState() != null ) {
			String targetId = game.getFieldModel().getTargetSelectionState().getSelectedPlayerId();
			if (StringTool.isProvided(targetId)) {
				FieldCoordinate targetCoord = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetId));
				adjacentTarget = targetCoord != null && targetCoord.isAdjacent(game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()));
			}
		}

		if (fEndTurn || fEndPlayerAction) {
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), fFeedingAllowed, true, fEndTurn));
			// block defender set by ball and chain
		} else if (StringTool.isProvided(fBlockDefenderId)) {
			boolean askForBlockKind = false;
			boolean enabled = ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.ALLOW_SPECIAL_BLOCKS_WITH_BALL_AND_CHAIN)).isEnabled();
			if (enabled) {
				PlayerState playerState = game.getFieldModel().getPlayerState(game.getPlayerById(fBlockDefenderId));
				askForBlockKind = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesBlockAlternative) && !playerState.isStunned() && !playerState.isProneOrStunned();
				if (askForBlockKind) {
					game.setDefenderId(fBlockDefenderId);
				}
			}
			((Block) factory.forName(SequenceGenerator.Type.Block.name()))
				.pushSequence(new Block.Builder(getGameState()).withDefenderId(fBlockDefenderId).askForBlockKind(askForBlockKind).build());
			// this may happen on a failed TAKE_ROOT roll
		} else {
			PlayerAction playerAction = actingPlayer.getPlayerAction();
			if (StringTool.isProvided(actingPlayer.getPlayerId()) && (playerAction != null)
				&& !playerAction.isMoving() && !((playerAction == PlayerAction.PASS || playerAction == PlayerAction.HAND_OVER)
				&& !UtilPlayer.hasBall(game, actingPlayer.getPlayer()))) {
				pushSequenceForPlayerAction(playerAction);
			} else if (ArrayTool.isProvided(fMoveStack)) {
				if (PlayerAction.BLITZ_MOVE == playerAction) {
					blitzMoveGenerator.pushSequence(new BlitzMove.SequenceParams(getGameState(), fMoveStack, null, moveStart));
				} else {
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState(), fMoveStack, null, moveStart));
				}
			} else if (UtilPlayer.isNextMovePossible(game, false)
				|| ((PlayerAction.HAND_OVER_MOVE == playerAction) && UtilPlayer.canHandOver(game, actingPlayer.getPlayer()))
				|| ((PlayerAction.PASS_MOVE == playerAction) && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
				|| ((PlayerAction.FOUL_MOVE == playerAction) && UtilPlayer.canFoul(game, actingPlayer.getPlayer()))
				|| ((PlayerAction.GAZE_MOVE == playerAction) && UtilPlayer.isNextToGazeTarget(game, actingPlayer.getPlayer()))
				|| ((PlayerAction.KICK_TEAM_MATE_MOVE == playerAction) && UtilPlayer.canKickTeamMate(game, actingPlayer.getPlayer(), false))
				|| ((PlayerAction.THROW_TEAM_MATE_MOVE == playerAction) && UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), false))
				|| playerAction != null && playerAction.isBlitzMove() && adjacentTarget
			) {
				UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isJumping());
				if (playerAction != null && playerAction.isBlitzMove()) {
					ServerUtilBlock.updateDiceDecorations(game);
					blitzMoveGenerator.pushSequence(new BlitzMove.SequenceParams(getGameState()));
				} else {
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
				}
			} else {
				endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), fFeedingAllowed, true, fEndTurn));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private StepCommandStatus dispatchPlayerAction(PlayerAction pPlayerAction) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), pPlayerAction, actingPlayer.isJumping());
		if (pushSequenceForPlayerAction(pPlayerAction)) {
			getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
		}
		return StepCommandStatus.SKIP_STEP;
	}

	private boolean pushSequenceForPlayerAction(PlayerAction pPlayerAction) {
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		if (pPlayerAction != null) {
			switch (pPlayerAction) {
				case VICIOUS_VINES:
				case BLOCK:
					((Block) factory.forName(SequenceGenerator.Type.Block.name()))
						.pushSequence(new Block.Builder(getGameState()).useChainsaw(usingChainsaw).build());
					return true;
				case BLITZ:
				case BLITZ_MOVE:
				case PUTRID_REGURGITATION_MOVE:
				case KICK_EM_BLITZ:
					((BlitzBlock) factory.forName(SequenceGenerator.Type.BlitzBlock.name()))
						.pushSequence(new BlitzBlock.SequenceParams(getGameState(), usingChainsaw));
					return true;
				case FOUL:
				case FOUL_MOVE:
					((Foul) factory.forName(SequenceGenerator.Type.Foul.name()))
						.pushSequence(new Foul.SequenceParams(getGameState()));
					return true;
				case HAND_OVER:
				case HAND_OVER_MOVE:
				case PASS:
				case PASS_MOVE:
				case HAIL_MARY_PASS:
					((Pass) factory.forName(SequenceGenerator.Type.Pass.name()))
						.pushSequence(new Pass.SequenceParams(getGameState()));
					return true;
				case THROW_TEAM_MATE:
				case THROW_TEAM_MATE_MOVE:
					((ThrowTeamMate) factory.forName(SequenceGenerator.Type.ThrowTeamMate.name()))
						.pushSequence(new ThrowTeamMate.SequenceParams(getGameState(), thrownPlayerId, false));
					return true;
				case KICK_TEAM_MATE:
				case KICK_TEAM_MATE_MOVE:
					((ThrowTeamMate) factory.forName(SequenceGenerator.Type.ThrowTeamMate.name()))
						.pushSequence(new ThrowTeamMate.SequenceParams(getGameState(), thrownPlayerId, true));
					return true;
				case GAZE:
					((Move) factory.forName(SequenceGenerator.Type.Move.name()))
						.pushSequence(new Move.SequenceParams(getGameState()));
					return true;
				default:
					break;
			}
		}
		return false;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.FEEDING_ALLOWED.addTo(jsonObject, fFeedingAllowed);
		IServerJsonOption.MOVE_STACK.addTo(jsonObject, fMoveStack);
		IServerJsonOption.DISPATCH_PLAYER_ACTION.addTo(jsonObject, fDispatchPlayerAction);
		IServerJsonOption.BLOCK_DEFENDER_ID.addTo(jsonObject, fBlockDefenderId);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, thrownPlayerId);
		return jsonObject;
	}

	@Override
	public StepEndMoving initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		fFeedingAllowed = IServerJsonOption.FEEDING_ALLOWED.getFrom(source, jsonObject);
		fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(source, jsonObject);
		fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(source, jsonObject);
		fBlockDefenderId = IServerJsonOption.BLOCK_DEFENDER_ID.getFrom(source, jsonObject);
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		thrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
