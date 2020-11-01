package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.action.common.StepReallyStupid.StepState;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in any sequence to handle skill SHADOWING.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * Expects stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepShadowing extends AbstractStepWithReRoll {
	

	public class StepState {
	    public ActionStatus status;
		
		public FieldCoordinate defenderPosition;
		public FieldCoordinate coordinateFrom;
		public boolean usingDivingTackle;
		public Boolean usingShadowing;
	  }
	
	private StepState state;
	
	public StepShadowing(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.SHADOWING;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case COORDINATE_FROM:
					state.coordinateFrom = (FieldCoordinate) pParameter.getValue();
					return true;
				case DEFENDER_POSITION:
					state.defenderPosition = (FieldCoordinate) pParameter.getValue();
					return true;
				case USING_DIVING_TACKLE:
					state.usingDivingTackle = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					return true;
				default:
					break;
			}
		}
		return false;
	}
  
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_PLAYER_CHOICE:
          ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
          ServerSkill usedSkill = (ServerSkill) useSkillCommand.getSkill();
          if (usedSkill != null) { 
        	  commandStatus = usedSkill.applyUseSkillCommandHooks(this, state, useSkillCommand);
        	  
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
    IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, state.defenderPosition);
    IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, state.coordinateFrom);
    IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, state.usingDivingTackle);
    IServerJsonOption.USING_SHADOWING.addTo(jsonObject, state.usingDivingTackle);
    return jsonObject;
  }
  
  @Override
  public StepShadowing initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.defenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(jsonObject);
    state.coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    state.usingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(jsonObject);
    state.usingDivingTackle = IServerJsonOption.USING_SHADOWING.getFrom(jsonObject);
    return this;
  }

}
