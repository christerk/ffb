package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.Set;

import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogInterceptionParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandInterceptorChoice;
import com.balancedbytes.games.ffb.report.ReportInterceptionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
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
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

/**
 * Step in the pass sequence to handle interceptions.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter INTERCEPTOR_ID for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepIntercept extends AbstractStepWithReRoll {
	
	protected String fGotoLabelOnFailure;
	protected String fInterceptorId;
	protected boolean fInterceptorChosen;
	protected TurnMode fOldTurnMode;
	
	public StepIntercept(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INTERCEPT;
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
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_INTERCEPTOR_CHOICE:
	        ClientCommandInterceptorChoice interceptorCommand = (ClientCommandInterceptorChoice) pNetCommand;
	        fInterceptorId = interceptorCommand.getInterceptorId();
	        fInterceptorChosen = true;
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

  private void executeStep() {
    Game game = getGameState().getGame();
    if (game.getThrower() == null) {
    	return;
    }
    // reset range ruler after passBlock
    if (game.getFieldModel().getRangeRuler() == null) {
    	game.getFieldModel().setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
    }
    Player[] possibleInterceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());
    boolean doNextStep = true;
    boolean doIntercept = (possibleInterceptors.length > 0); 
    if (doIntercept) {
      Player interceptor = game.getPlayerById(fInterceptorId);
      if (ReRolledAction.INTERCEPTION == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), interceptor)) {
          doIntercept = false;
        }
      }
      if (doIntercept) {
        if (!fInterceptorChosen) {
          UtilDialog.showDialog(getGameState(), new DialogInterceptionParameter(game.getThrowerId()));
          fOldTurnMode = game.getTurnMode();
          game.setTurnMode(TurnMode.INTERCEPTION);
          doNextStep = false;
        } else if (interceptor != null) {
          switch (intercept(interceptor)) {
            case SUCCESS:
              doIntercept = true;
              break;
            case FAILURE:
              doIntercept = false;
              break;
            default:
              doNextStep = false;
              break;
          }
        } else {
          doIntercept = false;
        }
      }
    }
    if (doNextStep) {
    	if (fOldTurnMode != null) {
    		game.setTurnMode(fOldTurnMode);
    	}
      if (doIntercept) {
      	publishParameter(new StepParameter(StepParameterKey.INTERCEPTOR_ID, fInterceptorId));
        getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
      	publishParameter(new StepParameter(StepParameterKey.INTERCEPTOR_ID, null));
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
      }
    }
  }
  
  private ActionStatus intercept(Player pInterceptor) {
    ActionStatus status = null;
    Game game = getGameState().getGame();
    Set<InterceptionModifier> interceptionModifiers = InterceptionModifier.findInterceptionModifiers(game, pInterceptor);
    int minimumRoll = DiceInterpreter.getInstance().minimumRollInterception(pInterceptor, interceptionModifiers);
    int roll = getGameState().getDiceRoller().rollSkill();
    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
    InterceptionModifier[] interceptionModifierArray = InterceptionModifier.toArray(interceptionModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.CATCH) && (getReRollSource() != null));
    getResult().addReport(new ReportInterceptionRoll(pInterceptor.getId(), successful, roll, minimumRoll, interceptionModifierArray, reRolled, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
    if (successful) {
      status = ActionStatus.SUCCESS;
    } else {
      status = ActionStatus.FAILURE;
      if (getReRolledAction() != ReRolledAction.CATCH) {
        setReRolledAction(ReRolledAction.CATCH);
        if (UtilCards.hasSkill(game, pInterceptor, Skill.CATCH)) {
          setReRollSource(ReRollSource.CATCH);
          UtilReRoll.useReRoll(this, getReRollSource(), pInterceptor);
          status = intercept(pInterceptor);
        } else {
          if (UtilReRoll.askForReRollIfAvailable(getGameState(), pInterceptor, ReRolledAction.INTERCEPTION, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          }
        }
      }
    }
    return status;
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  	pByteList.addString(fInterceptorId);
  	pByteList.addBoolean(fInterceptorChosen);
  	pByteList.addByte((byte) ((fOldTurnMode != null) ? fOldTurnMode.getId() : 0));
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fInterceptorId = pByteArray.getString();
  	fInterceptorChosen = pByteArray.getBoolean();
  	fOldTurnMode = new TurnModeFactory().forId(pByteArray.getByte());
  	return byteArraySerializationVersion;
  }
  
}
