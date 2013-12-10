package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Last step in select sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter BLOCK_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter DISPATCH_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * Expects stepParameter FOUL_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter GAZE_VICTIM_ID to be set by a preceding step.
 * Expects stepParameter HAIL_MARY_PASS to be set by a preceding step.
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 * Expects stepParameter TARGET_COORDINATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter USING_STAB to be set by a preceding step.
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
				case FOUL_DEFENDER_ID:
					fFoulDefenderId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case GAZE_VICTIM_ID:
					fGazeVictimId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case HAIL_MARY_PASS:
					fHailMaryPass = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case MOVE_STACK:
					fMoveStack = (FieldCoordinate[]) pParameter.getValue();
					pParameter.consume();
					return true;
				case TARGET_COORDINATE:
					fTargetCoordinate = (FieldCoordinate) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case USING_STAB:
					fUsingStab = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false; 
					pParameter.consume();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
	private void executeStep() {
    UtilDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn || fEndPlayerAction) {
  		SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, fEndTurn);
    } else if (actingPlayer.isSufferingBloodLust()) {
    	if (fDispatchPlayerAction != null) {
    		if (!fDispatchPlayerAction.isMoving()) {
    			fDispatchPlayerAction = PlayerAction.MOVE;
    		}
      	dispatchPlayerAction(fDispatchPlayerAction, false);
    	} else {
    		if ((actingPlayer.getPlayerAction() != null) && !actingPlayer.getPlayerAction().isMoving()) {
    			UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MOVE, actingPlayer.isLeaping());
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
    		SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, false);
        break;
      case STAND_UP:
      	if (actingPlayer.getPlayer().hasSkill(Skill.HYPNOTIC_GAZE)) {     		
      		SequenceGenerator.getInstance().pushMoveSequence(getGameState());
      	} else {
      		SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, false);
      	}
        break;
      case STAND_UP_BLITZ:
        game.getTurnData().setBlitzUsed(true);
    		SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, false);
        break;
      default:
        throw new IllegalStateException("Unhandled player action " + pPlayerAction.getName() + ".");
    }
	}
	
	// ByteArray serialization

  public int getByteArraySerializationVersion() {
  	return 2;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  	pByteList.addByte((byte) ((fDispatchPlayerAction != null) ? fDispatchPlayerAction.getId() : 0));
  	if (ArrayTool.isProvided(fMoveStack)) {
  		pByteList.addByte((byte) fMoveStack.length);
  		for (int i = 0; i < fMoveStack.length; i++) {
  			pByteList.addFieldCoordinate(fMoveStack[i]);
  		}
  	} else {
  		pByteList.addByte((byte) 0);
  	}
  	pByteList.addString(fGazeVictimId);
  	pByteList.addString(fBlockDefenderId);
  	pByteList.addBoolean(fUsingStab);
  	pByteList.addString(fFoulDefenderId);
  	pByteList.addFieldCoordinate(fTargetCoordinate);
  	pByteList.addBoolean(fHailMaryPass);
  	pByteList.addString(fThrownPlayerId);
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	fDispatchPlayerAction = new PlayerActionFactory().forId(pByteArray.getByte());
  	fMoveStack = new FieldCoordinate[pByteArray.getByte()];
  	for (int i = 0; i < fMoveStack.length; i++) {
  		fMoveStack[i] = pByteArray.getFieldCoordinate();
  	}
  	fGazeVictimId = pByteArray.getString();
  	fBlockDefenderId = pByteArray.getString();
  	fUsingStab = pByteArray.getBoolean();
  	fFoulDefenderId = pByteArray.getString();
  	fTargetCoordinate = pByteArray.getFieldCoordinate();
  	fHailMaryPass = pByteArray.getBoolean();
  	if (byteArraySerializationVersion < 2) {
  		pByteArray.getString();  // deprecated catcherId
  	}
  	fThrownPlayerId = pByteArray.getString();
  	return byteArraySerializationVersion;
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
    return this;
  }
  
}
