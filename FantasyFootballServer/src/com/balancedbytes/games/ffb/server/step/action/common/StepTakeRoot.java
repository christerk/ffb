package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
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
        if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
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
          if (((reRolledAction == null) || (reRolledAction != getReRolledAction())) && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), reRolledAction, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          } else {
            continueOnFailure = cancelPlayerAction();
          }
        }
        boolean reRolled = ((reRolledAction != null) && (reRolledAction == getReRolledAction()) && (getReRollSource() != null));
        getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, Skill.TAKE_ROOT));
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
    actingPlayer.setDodging(false);
    actingPlayer.setCurrentMove(UtilCards.getPlayerMovement(game, actingPlayer.getPlayer()));
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
      case KICK_TEAM_MATE_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.KICK_TEAM_MATE, actingPlayer.isLeaping());
        break;
      case HAND_OVER_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, actingPlayer.isLeaping());
        break;
      case FOUL_MOVE:
        UtilActingPlayer.changeActingPlayer(game, actingPlayer.getPlayerId(), PlayerAction.FOUL, actingPlayer.isLeaping());
        break;
      case PASS:
      case THROW_TEAM_MATE:
      case KICK_TEAM_MATE:
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
    getResult().setSound(SoundId.ROOT);
    return continueOnFailure;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }

  @Override
  public StepTakeRoot initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
	
}
