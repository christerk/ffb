package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogDefenderActionParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill DUMP_OFF.
 * 
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepDumpOff extends AbstractStep {
	
	private Boolean fUsingDumpOff;
	private FieldCoordinate fDefenderPosition;
	private TurnMode fOldTurnMode;

	public StepDumpOff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.DUMP_OFF;
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
          if (Skill.DUMP_OFF == useSkillCommand.getSkill()) {
            fUsingDumpOff = useSkillCommand.isSkillUsed();
            commandStatus = StepCommandStatus.EXECUTE_STEP;
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
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case DEFENDER_POSITION:
					fDefenderPosition = (FieldCoordinate) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
  private void executeStep() {
    
  	Game game = getGameState().getGame();
    
    if (game.getTurnMode() == TurnMode.DUMP_OFF) {
    	game.setTurnMode(fOldTurnMode);
      getResult().setNextAction(StepAction.NEXT_STEP);
    	
    } else if (fUsingDumpOff == null) {
      if (UtilCards.hasSkill(game, game.getDefender(), Skill.DUMP_OFF) && (fDefenderPosition != null) && fDefenderPosition.equals(game.getFieldModel().getBallCoordinate())
        && !(game.getFieldModel().getPlayerState(game.getDefender()).isConfused() || game.getFieldModel().getPlayerState(game.getDefender()).isHypnotized())) {
        UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.DUMP_OFF, 0));
        getResult().setNextAction(StepAction.CONTINUE);
      } else {
      	fUsingDumpOff = false;
        getResult().setNextAction(StepAction.NEXT_STEP);
      }

    } else if (fUsingDumpOff) {
    	fOldTurnMode = game.getTurnMode();
    	game.setTurnMode(TurnMode.DUMP_OFF);
    	game.setThrowerId(game.getDefenderId());
    	game.setThrowerAction(PlayerAction.DUMP_OFF);
    	game.setDefenderAction(PlayerAction.DUMP_OFF);
      UtilServerDialog.showDialog(getGameState(), new DialogDefenderActionParameter());
    	getGameState().pushCurrentStepOnStack();
      SequenceGenerator.getInstance().pushPassSequence(getGameState());
      getResult().setNextAction(StepAction.NEXT_STEP);
    
    } else {
      getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.DUMP_OFF, false, null));
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
    
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_DUMP_OFF.addTo(jsonObject, fUsingDumpOff);
    IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, fDefenderPosition);
    IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, fOldTurnMode);
    return jsonObject;
  }
  
  @Override
  public StepDumpOff initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingDumpOff = IServerJsonOption.USING_DUMP_OFF.getFrom(jsonObject);
    fDefenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(jsonObject);
    fOldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(jsonObject);
    return this;
  }

}
