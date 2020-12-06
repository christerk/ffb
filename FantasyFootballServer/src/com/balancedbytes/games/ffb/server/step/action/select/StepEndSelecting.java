package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Last step in select sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter BLOCK_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter DISPATCH_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step. Expects
 * stepParameter FOUL_DEFENDER_ID to be set by a preceding step. Expects
 * stepParameter GAZE_VICTIM_ID to be set by a preceding step. Expects
 * stepParameter HAIL_MARY_PASS to be set by a preceding step. Expects
 * stepParameter MOVE_STACK to be set by a preceding step. Expects stepParameter
 * TARGET_COORDINATE to be set by a preceding step. Expects stepParameter
 * THROWN_PLAYER_ID to be set by a preceding step. Expects stepParameter
 * USING_STAB to be set by a preceding step.
 * 
 * Will push a new sequence on the stack.
 *
 * @author Kalimar
 */
public final class StepEndSelecting extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private PlayerAction fDispatchPlayerAction;
	// moveSequence
	private FieldCoordinate[] fMoveStack;
	private String fGazeVictimId;
	// blockSequence
	private String fBlockDefenderId;
	private Boolean fUsingStab;
	// foulSequence
	private String fFoulDefenderId;
	// passSequence + throwTeamMateSequence
	private FieldCoordinate fTargetCoordinate;
	private boolean fHailMaryPass;
	private String fThrownPlayerId;
	private String fKickedPlayerId;
	private int fNumDice;

	public StepEndSelecting(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_SELECTING;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case BLOCK_DEFENDER_ID:
				fBlockDefenderId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case DISPATCH_PLAYER_ACTION:
				fDispatchPlayerAction = (PlayerAction) pParameter.getValue();
				consume(pParameter);
				return true;
			case END_PLAYER_ACTION:
				fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case FOUL_DEFENDER_ID:
				fFoulDefenderId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case GAZE_VICTIM_ID:
				fGazeVictimId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case HAIL_MARY_PASS:
				fHailMaryPass = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case MOVE_STACK:
				fMoveStack = (FieldCoordinate[]) pParameter.getValue();
				consume(pParameter);
				return true;
			case TARGET_COORDINATE:
				fTargetCoordinate = (FieldCoordinate) pParameter.getValue();
				consume(pParameter);
				return true;
			case THROWN_PLAYER_ID:
				fThrownPlayerId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case KICKED_PLAYER_ID:
				fKickedPlayerId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case NR_OF_DICE:
				fNumDice = (pParameter.getValue() != null) ? (Integer) pParameter.getValue() : 0;
				consume(pParameter);
				return true;
			case USING_STAB:
				fUsingStab = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fEndTurn || fEndPlayerAction) {
			SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, true, fEndTurn);
		} else if (actingPlayer.isSufferingBloodLust()) {
			if (fDispatchPlayerAction != null) {
				if (!fDispatchPlayerAction.isMoving()) {
					fDispatchPlayerAction = PlayerAction.MOVE;
				}
				dispatchPlayerAction(fDispatchPlayerAction, false);
			} else {
				if ((actingPlayer.getPlayerAction() != null) && !actingPlayer.getPlayerAction().isMoving()) {
					UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MOVE,
							actingPlayer.isLeaping());
				}
				dispatchPlayerAction(actingPlayer.getPlayerAction(), false);
			}
		} else if (fDispatchPlayerAction != null) {
			dispatchPlayerAction(fDispatchPlayerAction, true);
		} else {
			dispatchPlayerAction(actingPlayer.getPlayerAction(), false);
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void dispatchPlayerAction(PlayerAction pPlayerAction, boolean pWithParameter) {
		if (pPlayerAction == null) {
			SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
			return;
		}
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (pPlayerAction) {
		case PASS:
		case HAIL_MARY_PASS:
		case THROW_BOMB:
		case HAIL_MARY_BOMB:
		case HAND_OVER:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushPassSequence(getGameState(), fTargetCoordinate);
			} else {
				SequenceGenerator.getInstance().pushPassSequence(getGameState());
			}
			break;
		case THROW_TEAM_MATE:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushThrowTeamMateSequence(getGameState(), fThrownPlayerId, fTargetCoordinate);
			} else {
				SequenceGenerator.getInstance().pushThrowTeamMateSequence(getGameState());
			}
			break;
		case KICK_TEAM_MATE:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushKickTeamMateSequence(getGameState(), fNumDice, fKickedPlayerId);
			} else {
				SequenceGenerator.getInstance().pushKickTeamMateSequence(getGameState());
			}
			break;
		case BLITZ:
		case BLOCK:
		case MULTIPLE_BLOCK:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushBlockSequence(getGameState(), fBlockDefenderId, fUsingStab, null);
			} else {
				SequenceGenerator.getInstance().pushBlockSequence(getGameState());
			}
			break;
		case FOUL:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushFoulSequence(getGameState(), fFoulDefenderId);
			} else {
				SequenceGenerator.getInstance().pushFoulSequence(getGameState());
			}
			break;
		case MOVE:
		case FOUL_MOVE:
		case PASS_MOVE:
		case THROW_TEAM_MATE_MOVE:
		case KICK_TEAM_MATE_MOVE:
		case HAND_OVER_MOVE:
		case GAZE:
		case BLITZ_MOVE:
			if (pWithParameter) {
				SequenceGenerator.getInstance().pushMoveSequence(getGameState(), fMoveStack, fGazeVictimId);
			} else {
				SequenceGenerator.getInstance().pushMoveSequence(getGameState());
			}
			break;
		case REMOVE_CONFUSION:
			actingPlayer.setHasMoved(true);
			SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, true, false);
			break;
		case STAND_UP:
			if (UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
				SequenceGenerator.getInstance().pushMoveSequence(getGameState());
			} else {
				SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, true, false);
			}
			break;
		case STAND_UP_BLITZ:
			game.getTurnData().setBlitzUsed(true);
			SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, true, false);
			break;
		default:
			throw new IllegalStateException("Unhandled player action " + pPlayerAction.getName() + ".");
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.DISPATCH_PLAYER_ACTION.addTo(jsonObject, fDispatchPlayerAction);
		IServerJsonOption.MOVE_STACK.addTo(jsonObject, fMoveStack);
		IServerJsonOption.GAZE_VICTIM_ID.addTo(jsonObject, fGazeVictimId);
		IServerJsonOption.BLOCK_DEFENDER_ID.addTo(jsonObject, fBlockDefenderId);
		IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		IServerJsonOption.FOUL_DEFENDER_ID.addTo(jsonObject, fFoulDefenderId);
		IServerJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		IServerJsonOption.HAIL_MARY_PASS.addTo(jsonObject, fHailMaryPass);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
		IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNumDice);
		return jsonObject;
	}

	@Override
	public StepEndSelecting initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
		fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(jsonObject);
		fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(jsonObject);
		fGazeVictimId = IServerJsonOption.GAZE_VICTIM_ID.getFrom(jsonObject);
		fBlockDefenderId = IServerJsonOption.BLOCK_DEFENDER_ID.getFrom(jsonObject);
		fUsingStab = IServerJsonOption.USING_STAB.getFrom(jsonObject);
		fFoulDefenderId = IServerJsonOption.FOUL_DEFENDER_ID.getFrom(jsonObject);
		fTargetCoordinate = IServerJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
		fHailMaryPass = IServerJsonOption.HAIL_MARY_PASS.getFrom(jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
		fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(jsonObject);
		fNumDice = IServerJsonOption.NR_OF_DICE.getFrom(jsonObject);
		return this;
	}

}
