package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in the pass sequence to handle skill SAFE_THROW.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter INTERCEPTOR_ID to be set by a preceding step.
 * 
 * Sets stepParameter INTERCEPTOR_ID for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepSafeThrow extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	private String fInterceptorId;
	
	public StepSafeThrow(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.SAFE_THROW;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
  }
  
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case INTERCEPTOR_ID:
					fInterceptorId = (String) pParameter.getValue();
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
    Player interceptor = game.getPlayerById(fInterceptorId);
    if ((game.getThrower() == null) || (interceptor == null)) {
    	return;
    }
    boolean doNextStep = true;
    boolean safeThrowSuccessful = false;
    boolean doSafeThrow = (UtilCards.hasSkill(game, game.getThrower(), Skill.SAFE_THROW) && !UtilCards.hasSkill(game, interceptor, Skill.VERY_LONG_LEGS));
    if (doSafeThrow) {
      if (ReRolledAction.SAFE_THROW == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
          doSafeThrow = false;
        }
      }
      if (doSafeThrow) {
        int roll = getGameState().getDiceRoller().rollSkill();
        int minimumRoll = DiceInterpreter.getInstance().minimumRollSafeThrow(game.getThrower());
        safeThrowSuccessful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
        boolean reRolled = ((getReRolledAction() == ReRolledAction.SAFE_THROW) && (getReRollSource() != null));
        getResult().addReport(new ReportSkillRoll(ReportId.SAFE_THROW_ROLL, game.getThrowerId(), safeThrowSuccessful, roll, minimumRoll, reRolled));
        if (!safeThrowSuccessful && (getReRolledAction() != ReRolledAction.SAFE_THROW) && UtilReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledAction.SAFE_THROW, minimumRoll, false)) {
          doNextStep = false;
        }
      }
    }
    if (doNextStep) {
      if (safeThrowSuccessful) {
      	publishParameter(new StepParameter(StepParameterKey.INTERCEPTOR_ID, null));
      	getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
        game.getFieldModel().setRangeRuler(null);
        FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
        FieldCoordinate interceptorCoordinate = null;
        if (interceptor != null) {
          interceptorCoordinate = game.getFieldModel().getPlayerCoordinate(interceptor);
        }
        if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
        	getResult().setAnimation(new Animation(AnimationType.THROW_BOMB, startCoordinate, game.getPassCoordinate(), interceptorCoordinate));
        } else {
        	getResult().setAnimation(new Animation(AnimationType.PASS, startCoordinate, game.getPassCoordinate(), interceptorCoordinate));
        }
        UtilGame.syncGameModel(this);
        if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
        	game.getFieldModel().setBombCoordinate(interceptorCoordinate);
        	game.getFieldModel().setBombMoving(false);
        } else {
        	game.getFieldModel().setBallCoordinate(interceptorCoordinate);
        	game.getFieldModel().setBallMoving(false);
        }
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
      }
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  	pByteList.addString(fInterceptorId);
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fInterceptorId = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
}
