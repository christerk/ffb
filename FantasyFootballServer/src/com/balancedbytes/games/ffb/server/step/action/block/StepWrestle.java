package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill WRESTLE.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepWrestle extends AbstractStep {
		
	public class StepState {
	    public ActionStatus status;
	    public Boolean continueOnFailure;
	    
	    public Boolean usingWrestleDefender;
		public Boolean usingWrestleAttacker;
	  }
	
	private StepState state;
	
	public StepWrestle(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}
	
	public StepId getId() {
		return StepId.WRESTLE;
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
    IServerJsonOption.USING_WRESTLE_ATTACKER.addTo(jsonObject, state.usingWrestleAttacker);
    IServerJsonOption.USING_WRESTLE_DEFENDER.addTo(jsonObject, state.usingWrestleDefender);
    return jsonObject;
  }
  
  @Override
  public StepWrestle initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.usingWrestleAttacker = IServerJsonOption.USING_WRESTLE_ATTACKER.getFrom(jsonObject);
    state.usingWrestleDefender = IServerJsonOption.USING_WRESTLE_DEFENDER.getFrom(jsonObject);
    return this;
  }

}
