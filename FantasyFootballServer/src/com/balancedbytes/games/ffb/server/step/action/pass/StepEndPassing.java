package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Final step of the pass sequence.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * Expects stepParameter HAIL_MARY_PASS to be set by a preceding step.
 * Expects stepParameter INTERCEPTOR_ID to be set by a preceding step.
 * Expects stepParameter PASS_ACCURATE to be set by a preceding step.
 * Expects stepParameter PASS_FUMBLE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepEndPassing extends AbstractStep {

  private String fInterceptorId;	
  private String fCatcherId;
  private boolean fPassAccurate;
  private boolean fPassFumble;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;

	public StepEndPassing(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.END_PASSING;
	}
	
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
	  		case CATCHER_ID:
	  			fCatcherId = (String) pParameter.getValue();
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
				case INTERCEPTOR_ID:
					fInterceptorId = (String) pParameter.getValue();
					pParameter.consume();
					return true;
				case PASS_ACCURATE:
					fPassAccurate = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					pParameter.consume();
					return true;
				case PASS_FUMBLE:
					fPassFumble = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
	
  // TODO: what happens here in the case of dump-off interception?

	private void executeStep() {
		UtilDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    game.getFieldModel().setRangeRuler(null);
    ActingPlayer actingPlayer = game.getActingPlayer();
    // failed confusion roll on throw bomb -> end player action
    if (fEndPlayerAction && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, fEndTurn);
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    // throw bomb mode -> start bomb sequence
    if (game.getTurnMode().isBombTurn()) {
    	if (StringTool.isProvided(fInterceptorId)) {
    		SequenceGenerator.getInstance().pushBombSequence(getGameState(), fInterceptorId, fPassFumble);
    	} else {
    		SequenceGenerator.getInstance().pushBombSequence(getGameState(), fCatcherId, fPassFumble);
    	}
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    // failed animosity may try to choose a new target
    if (actingPlayer.isSufferingAnimosity() && !fEndPlayerAction && (game.getPassCoordinate() == null)) {
    	SequenceGenerator.getInstance().pushPassSequence(getGameState());
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    Player catcher = game.getPlayerById(fCatcherId);
    // completions and passing statistic
    if ((game.getThrower() != null) && (catcher != null) && UtilPlayer.hasBall(game, catcher) && game.getThrower().getTeam().hasPlayer(catcher)) {
    	PlayerResult throwerResult = game.getGameResult().getPlayerResult(game.getThrower());
    	if (fPassAccurate) {
    		throwerResult.setCompletions(throwerResult.getCompletions() + 1);
    	}
      FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
      FieldCoordinate endCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
      int deltaX = 0;
      if (game.isHomePlaying()) {
        deltaX = endCoordinate.getX() - startCoordinate.getX();
      } else {
        deltaX = startCoordinate.getX() - endCoordinate.getX();
      }
      throwerResult.setPassing(throwerResult.getPassing() + deltaX);
    }
    if (fEndTurn || fEndPlayerAction || ((game.getThrower() == actingPlayer.getPlayer()) && actingPlayer.isSufferingBloodLust() && !actingPlayer.hasFed())) {
    	fEndTurn |= (UtilSteps.checkTouchdown(getGameState()) || (catcher == null) || UtilPlayer.findOtherTeam(game, game.getThrower()).hasPlayer(catcher) || fPassFumble);
    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, fEndTurn);
    } else {
	    if (StringTool.isProvided(fInterceptorId)) {
	      catcher = game.getPlayerById(fInterceptorId);
	      GameResult gameResult = game.getGameResult();
	      PlayerResult catcherResult = gameResult.getPlayerResult(catcher);
	      catcherResult.setInterceptions(catcherResult.getInterceptions() + 1);
	      FieldCoordinate interceptorCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
	      game.getFieldModel().setBallCoordinate(interceptorCoordinate);
	      game.getFieldModel().setBallMoving(false);
	    } else {
	      catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
	    }
	    if (game.getThrower() == actingPlayer.getPlayer()) {
	    	fEndTurn |= (UtilSteps.checkTouchdown(getGameState()) || (catcher == null) || UtilPlayer.findOtherTeam(game, game.getThrower()).hasPlayer(catcher) || fPassFumble);
	    	SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), true, fEndTurn);
	    } else {
      	game.setDefenderAction(null);  // reset dump-off action
	    }
    }
  	getResult().setNextAction(StepAction.NEXT_STEP);
  }
	
	// ByteArray serialization
	
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fInterceptorId = pByteArray.getString();
  	fCatcherId = pByteArray.getString();
  	fPassAccurate = pByteArray.getBoolean();
  	fPassFumble = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    IServerJsonOption.PASS_ACCURATE.addTo(jsonObject, fPassAccurate);
    IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    return jsonObject;
  }
  
  @Override
  public StepEndPassing initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fInterceptorId = IServerJsonOption.INTERCEPTOR_ID.getFrom(jsonObject);
    fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
    fPassAccurate = IServerJsonOption.PASS_ACCURATE.getFrom(jsonObject);
    fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    return this;
  }

}
