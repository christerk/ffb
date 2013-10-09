package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
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
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to handle skill TAKE ROOT.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepTakeRoot extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepTakeRoot(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.TAKE_ROOT;
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}
	
  private void executeStep() {
  	ActionStatus status = ActionStatus.SUCCESS;
  	boolean continueOnFailure = false;
    Game game = getGameState().getGame();
    if (!game.getTurnMode().checkNegatraits()) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    if (playerState.isConfused()) {
    	game.getFieldModel().setPlayerState(actingPlayer.getPlayer() , playerState.changeConfused(false));
    }
    if (playerState.isHypnotized()) {
    	game.getFieldModel().setPlayerState(actingPlayer.getPlayer() , playerState.changeHypnotized(false));
    }
    if (UtilCards.hasSkill(game, actingPlayer, Skill.TAKE_ROOT) && !playerState.isRooted()) {
      boolean doRoll = true;
      ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(Skill.TAKE_ROOT); 
      if ((reRolledAction != null) && (reRolledAction == getReRolledAction())) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          status = ActionStatus.FAILURE;
          continueOnFailure = cancelPlayerAction();
        }
      } else {
        doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, Skill.TAKE_ROOT);
      }
      if (doRoll) {
        int roll = getGameState().getDiceRoller().rollSkill();
        int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(true);
        boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
        actingPlayer.markSkillUsed(Skill.TAKE_ROOT);
        if (!successful) {
          status = ActionStatus.FAILURE;
          if (((reRolledAction == null) || (reRolledAction != getReRolledAction())) && UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), reRolledAction, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          } else {
            continueOnFailure = cancelPlayerAction();
          }
        }
        boolean reRolled = ((reRolledAction != null) && (reRolledAction == getReRolledAction()) && (getReRollSource() != null));
        getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), Skill.TAKE_ROOT, successful, roll, minimumRoll, reRolled));
      }
    }
    if (status == ActionStatus.SUCCESS) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    } else {
    	if (status == ActionStatus.FAILURE) {
    		if (continueOnFailure) {
    			getResult().setNextAction(StepAction.NEXT_STEP);
    		} else {
      		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
    		}
    	}
    }
  }
  
  private boolean cancelPlayerAction() {
  	boolean continueOnFailure = false;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    actingPlayer.setGoingForIt(false);
    actingPlayer.setCurrentMove(actingPlayer.getPlayer().getMovement());
    switch (actingPlayer.getPlayerAction()) {
      case BLITZ:
      case BLITZ_MOVE:
  			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
  			game.getTurnData().setBlitzUsed(true);
        break;
      case PASS_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.PASS, actingPlayer.isLeaping());
        break;
      case THROW_TEAM_MATE_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE, actingPlayer.isLeaping());
        break;
      case HAND_OVER_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, actingPlayer.isLeaping());
        break;
      case FOUL_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.FOUL, actingPlayer.isLeaping());
        break;
      case PASS:
      case THROW_TEAM_MATE:
      case HAND_OVER:
      case FOUL:
      case BLOCK:
      case MULTIPLE_BLOCK:
      	continueOnFailure = true;
      	break;
    	default:
    		break;
    }
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeRooted(true));
    getResult().setSound(Sound.ROOT);
    return continueOnFailure;
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  }

	@Override
	public int initFrom(ByteArray pByteArray) {
		int byteArraySerializationVersion = super.initFrom(pByteArray);
		fGotoLabelOnFailure = pByteArray.getString();
		return byteArraySerializationVersion;
	}
	
}
