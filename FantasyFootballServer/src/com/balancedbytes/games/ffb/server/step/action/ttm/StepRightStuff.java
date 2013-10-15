package com.balancedbytes.games.ffb.server.step.action.ttm;

import java.util.Set;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;

/**
 * Step in ttm sequence to handle skill RIGHT_STUFF (landing roll).
 * 
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepRightStuff extends AbstractStepWithReRoll {
	
	protected Boolean fThrownPlayerHasBall;
	protected String fThrownPlayerId;
	
	public StepRightStuff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.RIGHT_STUFF;
	}
	
  @Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case THROWN_PLAYER_HAS_BALL:				
					fThrownPlayerHasBall = (Boolean) pParameter.getValue();
					return true;
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
    // skip right stuff step when player has been thrown out of bounds
  	if ((thrownPlayer != null) && game.getFieldModel().getPlayerState(thrownPlayer).getBase() == PlayerState.FALLING) {
  		publishParameter(new StepParameter(StepParameterKey.END_TURN, fThrownPlayerHasBall));
	    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));  // avoid reset in end step
  		getResult().setNextAction(StepAction.NEXT_STEP);
  		return;
  	}
    boolean doRoll = true;
    if (ReRolledAction.RIGHT_STUFF == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
        doRoll = false;
      }
    }
    if (doRoll) {
      Set<RightStuffModifier> rightStuffModifiers = RightStuffModifier.findRightStuffModifiers(game, thrownPlayer);
      int minimumRoll = DiceInterpreter.getInstance().minimumRollRightStuff(thrownPlayer, rightStuffModifiers);
      int roll = getGameState().getDiceRoller().rollSkill();
      boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
      RightStuffModifier[] rightStuffModifiersArray = RightStuffModifier.toArray(rightStuffModifiers);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.RIGHT_STUFF) && (getReRollSource() != null));
      getResult().addReport(new ReportSkillRoll(ReportId.RIGHT_STUFF_ROLL, fThrownPlayerId, successful, roll, minimumRoll, reRolled, rightStuffModifiersArray));
      if (fThrownPlayerHasBall) {
        game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(thrownPlayer));
      }
      if (successful) {
      	if (fThrownPlayerHasBall) {
	      	if (UtilSteps.checkTouchdown(getGameState())) {
	      		publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	      	}
      	} else {
	        if (game.getFieldModel().getPlayerCoordinate(thrownPlayer).equals(game.getFieldModel().getBallCoordinate())) {
	          game.getFieldModel().setBallMoving(true);
	          publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
	        }
      	}
  	    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));  // avoid reset in end step
      	getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
        if (getReRolledAction() != ReRolledAction.RIGHT_STUFF) {
          setReRolledAction(ReRolledAction.RIGHT_STUFF);
          doRoll = UtilReRoll.askForReRollIfAvailable(getGameState(), thrownPlayer, ReRolledAction.RIGHT_STUFF, minimumRoll, false);
        } else {
          doRoll = false;
        }
      }
    }
    if (!doRoll) {
      FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(thrownPlayer);
      InjuryResult injuryResultThrownPlayer = UtilInjury.handleInjury(this, InjuryType.TTM_LANDING, null, thrownPlayer, playerCoordinate, null, ApothecaryMode.THROWN_PLAYER);
      publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultThrownPlayer));
      publishParameters(UtilInjury.dropPlayer(this, thrownPlayer));
  		publishParameter(new StepParameter(StepParameterKey.END_TURN, fThrownPlayerHasBall));
	    publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));  // avoid reset in end step
  		getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fThrownPlayerHasBall);
  	pByteList.addString(fThrownPlayerId);
  }

	@Override
	public int initFrom(ByteArray pByteArray) {
		int byteArraySerializationVersion = super.initFrom(pByteArray);
		fThrownPlayerHasBall = pByteArray.getBoolean();
		fThrownPlayerId = pByteArray.getString();
		return byteArraySerializationVersion;
	}
	
}
