package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
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
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill REALLY STUPID.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepReallyStupid extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepReallyStupid(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.REALLY_STUPID;
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
    if (UtilCards.hasSkill(game, actingPlayer, Skill.REALLY_STUPID)) {
      boolean doRoll = true;
      ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(Skill.REALLY_STUPID); 
      if ((reRolledAction != null) && (reRolledAction == getReRolledAction())) {
        if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          status = ActionStatus.FAILURE;
          cancelPlayerAction();
        }
      } else {
        doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, Skill.REALLY_STUPID);
      }
      if (doRoll) {
        int roll = getGameState().getDiceRoller().rollSkill();
        boolean goodConditions = true;
        if (actingPlayer.getPlayerAction() != PlayerAction.THROW_TEAM_MATE) {
          FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
          Player[] teamMates = UtilPlayer.findAdjacentBlockablePlayers(game, actingPlayer.getPlayer().getTeam(), playerCoordinate);
          goodConditions = false;
          for (Player teamMate : teamMates) {
            if (!UtilCards.hasSkill(game, teamMate, Skill.REALLY_STUPID)) {
              goodConditions = true;
              break;
            }
          }
        }
        int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
        boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
        actingPlayer.markSkillUsed(Skill.REALLY_STUPID);
        if (!successful) {
          status = ActionStatus.FAILURE;
          if (((reRolledAction == null) || (reRolledAction != getReRolledAction())) && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), reRolledAction, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          } else {
            cancelPlayerAction();
          }
        }
        boolean reRolled = ((reRolledAction != null) && (reRolledAction == getReRolledAction()) && (getReRollSource() != null));
        getResult().addReport(new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, Skill.REALLY_STUPID));
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
      game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeConfused(true).changeActive(false));
    }
    game.setPassCoordinate(null);
    getResult().setSound(Sound.DUH);
  }
  
  // ByteArray serialization
  
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
  public StepReallyStupid initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
	
}
