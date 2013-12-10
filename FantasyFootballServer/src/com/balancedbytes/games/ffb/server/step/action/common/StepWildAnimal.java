package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill WILD ANIMAL.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepWildAnimal extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepWildAnimal(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.WILD_ANIMAL;
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
    if (UtilCards.hasSkill(game, actingPlayer, Skill.WILD_ANIMAL)) {
      boolean doRoll = true;
      ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(Skill.WILD_ANIMAL); 
      if ((reRolledAction != null) && (reRolledAction == getReRolledAction())) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          status = ActionStatus.FAILURE;
          cancelPlayerAction();
        }
      } else {
        doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, Skill.WILD_ANIMAL);
      }
      if (doRoll) {
        int roll = getGameState().getDiceRoller().rollSkill();
        boolean goodConditions = (
        	(actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
      		|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
      		|| (actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
      		|| (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
      		|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ)
      	);
        int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
        boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
        actingPlayer.markSkillUsed(Skill.WILD_ANIMAL);
        if (!successful) {
          status = ActionStatus.FAILURE;
          if (((reRolledAction == null) || (reRolledAction != getReRolledAction())) && UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), reRolledAction, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          } else {
            cancelPlayerAction();
          }
        }
        boolean reRolled = ((reRolledAction != null) && (reRolledAction == getReRolledAction()) && (getReRollSource() != null));
        getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, Skill.WILD_ANIMAL));
      }
    }
    if (status == ActionStatus.SUCCESS) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    } else {
    	if (status == ActionStatus.FAILURE) {
  			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
    	}
    }
  }
  
  private void cancelPlayerAction() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    switch (actingPlayer.getPlayerAction()) {
      case BLITZ:
      case BLITZ_MOVE:
        game.getTurnData().setBlitzUsed(true);
        break;
      case PASS:
      case PASS_MOVE:
      case THROW_TEAM_MATE:
      case THROW_TEAM_MATE_MOVE:
        game.getTurnData().setPassUsed(true);
        break;
      case HAND_OVER:
      case HAND_OVER_MOVE:
        game.getTurnData().setHandOverUsed(true);
        break;
      case FOUL:
      case FOUL_MOVE:
        game.getTurnData().setFoulUsed(true);
        break;
      default:
      	break;
    }
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    if (actingPlayer.isStandingUp()) {
      game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.PRONE).changeActive(false));
    } else {
      game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.STANDING).changeActive(false));
    }
    game.setPassCoordinate(null);
    getResult().setSound(Sound.ROAR);
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  // ByteArray serialization
  
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

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepWildAnimal initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
	
}
