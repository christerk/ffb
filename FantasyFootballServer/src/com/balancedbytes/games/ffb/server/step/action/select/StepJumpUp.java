package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
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
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in select sequence to handle JUMP_UP skill.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter DISPATCH_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *  
 * @author Kalimar
 */
public final class StepJumpUp extends AbstractStepWithReRoll {
	
  private String fGotoLabelOnFailure;
	
	public StepJumpUp(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.JUMP_UP;
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
  	if (!StringTool.isProvided(fGotoLabelOnFailure)) {
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
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    if ((actingPlayer.isStandingUp() && !actingPlayer.hasMoved() && UtilCards.hasUnusedSkill(game, actingPlayer, Skill.JUMP_UP)) || (ReRolledAction.JUMP_UP == getReRolledAction())) {
      actingPlayer.setHasMoved(true);
      game.setConcessionPossible(false);
      actingPlayer.markSkillUsed(Skill.JUMP_UP);
      if ((PlayerAction.BLOCK == actingPlayer.getPlayerAction()) || (PlayerAction.MULTIPLE_BLOCK == actingPlayer.getPlayerAction())) {
        if (ReRolledAction.JUMP_UP == getReRolledAction()) {
          if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
            game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.PRONE).changeActive(false));
            publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
            getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
            return;
          }
        }
        int minimumRoll = DiceInterpreter.getInstance().minimumRollJumpUp(actingPlayer.getPlayer());
        int roll = getGameState().getDiceRoller().rollSkill();
        boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
        boolean reRolled = ((getReRolledAction() == ReRolledAction.JUMP_UP) && (getReRollSource() != null));
        getResult().addReport(new ReportSkillRoll(ReportId.JUMP_UP_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled));
        if (successful) {
          actingPlayer.setStandingUp(false);
          getResult().setNextAction(StepAction.NEXT_STEP);
          return;
        } else {
          if ((getReRolledAction() == ReRolledAction.JUMP_UP) || !UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.JUMP_UP, minimumRoll, false)) {
            game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.PRONE).changeActive(false));
            publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
            getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
          } else {
            getResult().setNextAction(StepAction.CONTINUE);
          }
          return;
        }
      }
    }
    getResult().setNextAction(StepAction.NEXT_STEP);            
  }
  
  // ByteArray serialization
  
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
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepJumpUp initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
  	
}
