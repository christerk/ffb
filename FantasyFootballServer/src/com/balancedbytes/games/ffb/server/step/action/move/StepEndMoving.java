package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Last step in move sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter BLOCK_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter DISPATCH_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * Expects stepParameter FEEDING_ALLOWED to be set by a preceding step.
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 * 
 * May push a new sequence on the stack.
 * 
 * @author Kalimar
 */
public class StepEndMoving extends AbstractStep {
	
	private boolean fEndTurn;
  private boolean fEndPlayerAction;
  private Boolean fFeedingAllowed;
  private FieldCoordinate[] fMoveStack;
  private PlayerAction fDispatchPlayerAction;
  private String fBlockDefenderId;
	
	public StepEndMoving(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_MOVING;
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
				case FEEDING_ALLOWED:
					fFeedingAllowed = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					consume(pParameter);
					return true;
				case MOVE_STACK:
					fMoveStack = (FieldCoordinate[]) pParameter.getValue();
					consume(pParameter);
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
          commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
          break;
        case CLIENT_FOUL:
          commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
          break;
        case CLIENT_HAND_OVER:
          commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
          break;
        case CLIENT_PASS:
          commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
          break;
        case CLIENT_THROW_TEAM_MATE:
          commandStatus = dispatchPlayerAction(fDispatchPlayerAction);
          break;
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
    	fFeedingAllowed = true;  // feeding allowed if not specified otherwise
    }
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn || fEndPlayerAction) {
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), fFeedingAllowed, true, fEndTurn);
    // block defender set by ball and chain
    } else if (StringTool.isProvided(fBlockDefenderId)) {
    	SequenceGenerator.getInstance().pushBlockSequence(getGameState(), fBlockDefenderId, false, null);
    // this may happen on a failed TAKE_ROOT roll
    } else if (StringTool.isProvided(actingPlayer.getPlayerId())
    		&& (actingPlayer.getPlayerAction() != null)
    		&& !actingPlayer.getPlayerAction().isMoving()
    		&& !(actingPlayer.getPlayerAction() == PlayerAction.PASS && !UtilPlayer.hasBall(game, actingPlayer.getPlayer())) ) {
    	pushSequenceForPlayerAction(actingPlayer.getPlayerAction());
    } else if (ArrayTool.isProvided(fMoveStack)) {
    	SequenceGenerator.getInstance().pushMoveSequence(getGameState(), fMoveStack, null);
    } else if (UtilPlayer.isNextMovePossible(game, false)
    	|| ((PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canHandOver(game, actingPlayer.getPlayer()))
      || ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
      || ((PlayerAction.FOUL_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canFoul(game, actingPlayer.getPlayer()))
      || ((PlayerAction.MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canGaze(game, actingPlayer.getPlayer())
      || ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), false)))) {
      UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
    	SequenceGenerator.getInstance().pushMoveSequence(getGameState());
    } else {
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), fFeedingAllowed, true, fEndTurn);
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	private StepCommandStatus dispatchPlayerAction(PlayerAction pPlayerAction) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), pPlayerAction, actingPlayer.isLeaping());
		if (pushSequenceForPlayerAction(pPlayerAction)) {
	    getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
		}
    return StepCommandStatus.SKIP_STEP;
	}
	
	private boolean pushSequenceForPlayerAction(PlayerAction pPlayerAction) {
		if (pPlayerAction != null) {
			switch (pPlayerAction) {
				case BLOCK:
				case BLITZ:
				case BLITZ_MOVE:
  	      SequenceGenerator.getInstance().pushBlockSequence(getGameState());
  	      return true;
				case FOUL:
				case FOUL_MOVE:
  	      SequenceGenerator.getInstance().pushFoulSequence(getGameState());
  	      return true;
				case HAND_OVER:
				case HAND_OVER_MOVE:
				case PASS:
				case PASS_MOVE:
				case HAIL_MARY_PASS:
  	      SequenceGenerator.getInstance().pushPassSequence(getGameState());
  	      return true;
				case THROW_TEAM_MATE:
				case THROW_TEAM_MATE_MOVE:
  	      SequenceGenerator.getInstance().pushThrowTeamMateSequence(getGameState());
  	      return true;
				case GAZE:
  	      SequenceGenerator.getInstance().pushMoveSequence(getGameState());
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
    return jsonObject;
  }
  
  @Override
  public StepEndMoving initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    fFeedingAllowed = IServerJsonOption.FEEDING_ALLOWED.getFrom(jsonObject);
    fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(jsonObject);
    fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(jsonObject);
    fBlockDefenderId = IServerJsonOption.BLOCK_DEFENDER_ID.getFrom(jsonObject);
    return this;
  }

}
