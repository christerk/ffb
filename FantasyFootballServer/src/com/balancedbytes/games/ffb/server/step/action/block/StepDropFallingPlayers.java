package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to drop falling players and handle the skill
 * PILING_ON.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * Sets stepParameter USING_PILING_ON for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepDropFallingPlayers extends AbstractStep {

 
  
  public class StepState {
	  public InjuryResult injuryResultDefender;
	  public Boolean usingPilingOn;
	  public PlayerState oldDefenderState;
	  }
	
	private StepState state;

  public StepDropFallingPlayers(GameState pGameState) {
    super(pGameState);
    
    state = new StepState();
  }

  public StepId getId() {
    return StepId.DROP_FALLING_PLAYERS;
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case OLD_DEFENDER_STATE:
        	state.oldDefenderState = (PlayerState) pParameter.getValue();
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_USE_SKILL:
        	 ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
             ServerSkill usedSkill = (ServerSkill) useSkillCommand.getSkill();
             if (usedSkill != null) {
               StepCommandStatus newStatus = usedSkill.applyUseSkillCommandHooks(this, state, useSkillCommand);
               if (newStatus != null) {
                 commandStatus = newStatus;
               }
             }
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
	getGameState().executeStepHooks(this, state);
  }
  


  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    if (state.injuryResultDefender != null) {
      IServerJsonOption.INJURY_RESULT_DEFENDER.addTo(jsonObject, state.injuryResultDefender.toJsonValue());
    }
    IServerJsonOption.USING_PILING_ON.addTo(jsonObject, state.usingPilingOn);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
    return jsonObject;
  }

  @Override
  public StepDropFallingPlayers initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.injuryResultDefender = null;
    JsonObject injuryResultDefenderObject = IServerJsonOption.INJURY_RESULT_DEFENDER.getFrom(jsonObject);
    if (injuryResultDefenderObject != null) {
    	state.injuryResultDefender = new InjuryResult().initFrom(injuryResultDefenderObject);
    }
    state.usingPilingOn = IServerJsonOption.USING_PILING_ON.getFrom(jsonObject);
    state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}
