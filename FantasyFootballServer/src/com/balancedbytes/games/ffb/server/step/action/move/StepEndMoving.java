package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilPlayerMove;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

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
					pParameter.consume();
					return true;
				case DISPATCH_PLAYER_ACTION:
					fDispatchPlayerAction = (PlayerAction) pParameter.getValue();
					pParameter.consume();
					return true;
				case END_PLAYER_ACTION:
					fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case END_TURN:
					fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case FEEDING_ALLOWED:
					fFeedingAllowed = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case MOVE_STACK:
					fMoveStack = (FieldCoordinate[]) pParameter.getValue();
					pParameter.consume();
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
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
		UtilDialog.hideDialog(getGameState());
    fEndTurn |= UtilSteps.checkTouchdown(getGameState());
    if (fFeedingAllowed == null) {
    	fFeedingAllowed = true;  // feeding allowed if not specified otherwise
    }
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn || fEndPlayerAction) {
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), fFeedingAllowed, fEndTurn);
    // block defender set by ball and chain
    } else if (StringTool.isProvided(fBlockDefenderId)) {
    	SequenceGenerator.getInstance().pushBlockSequence(getGameState(), fBlockDefenderId, false, null);
    // this may happen on a failed TAKE_ROOT roll
    } else if (StringTool.isProvided(actingPlayer.getPlayerId()) && (actingPlayer.getPlayerAction() != null) && !actingPlayer.getPlayerAction().isMoving()) {
    	pushSequenceForPlayerAction(actingPlayer.getPlayerAction());
    } else if (ArrayTool.isProvided(fMoveStack)) {
    	SequenceGenerator.getInstance().pushMoveSequence(getGameState(), fMoveStack, null);
    } else if (UtilPlayer.isNextMovePossible(game, false)
    	|| ((PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canHandOver(game, actingPlayer.getPlayer()))
      || ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
      || ((PlayerAction.FOUL_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canFoul(game, actingPlayer.getPlayer()))
      || ((PlayerAction.MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canGaze(game, actingPlayer.getPlayer())
      || ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer())))) {
      UtilPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
    	SequenceGenerator.getInstance().pushMoveSequence(getGameState());
    } else {
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), fFeedingAllowed, fEndTurn);
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	private StepCommandStatus dispatchPlayerAction(PlayerAction pPlayerAction) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), pPlayerAction, actingPlayer.isLeaping());
		if (pushSequenceForPlayerAction(pPlayerAction)) {
	    getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT_COMMAND);
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
	
	public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addByte((byte) ((fDispatchPlayerAction != null) ? fDispatchPlayerAction.getId() : 0));
  	if (ArrayTool.isProvided(fMoveStack)) {
  		pByteList.addByte((byte) fMoveStack.length);
  		for (int i = 0; i < fMoveStack.length; i++) {
  			pByteList.addFieldCoordinate(fMoveStack[i]);
  		}
  	} else {
  		pByteList.addByte((byte) 0);
  	}
  	pByteList.addBoolean(fFeedingAllowed);
  	pByteList.addBoolean(fEndPlayerAction);
  	pByteList.addBoolean(fEndTurn);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fDispatchPlayerAction = new PlayerActionFactory().forId(pByteArray.getByte());
  	fMoveStack = new FieldCoordinate[pByteArray.getByte()];
  	for (int i = 0; i < fMoveStack.length; i++) {
  		fMoveStack[i] = pByteArray.getFieldCoordinate();
  	}
  	fFeedingAllowed = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}
