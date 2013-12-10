package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReceiveChoice;
import com.balancedbytes.games.ffb.report.ReportReceiveChoice;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to determine receive choice.
 * 
 * Expects stepParameter CHOOSING_TEAM_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepReceiveChoice extends AbstractStep {
	
  private String fChoosingTeamId;
  private Boolean fReceiveChoice;
	
	public StepReceiveChoice(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.RECEIVE_CHOICE;
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case CHOOSING_TEAM_ID:
					fChoosingTeamId = (String) pParameter.getValue();
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
				case CLIENT_RECEIVE_CHOICE:
          ClientCommandReceiveChoice receiveChoiceCommand = (ClientCommandReceiveChoice) pNetCommand;
          fReceiveChoice = receiveChoiceCommand.isChoiceReceive();
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
    if (fReceiveChoice != null) {
    	UtilDialog.hideDialog(getGameState());
      if (game.getTeamHome().getId().equals(fChoosingTeamId)) {
        game.setHomePlaying(!fReceiveChoice);
        getResult().addReport(new ReportReceiveChoice(game.getTeamHome().getId(), fReceiveChoice));
      } else {
        game.setHomePlaying(fReceiveChoice);
        getResult().addReport(new ReportReceiveChoice(game.getTeamAway().getId(), fReceiveChoice));
      }
      game.setHomeFirstOffense(!game.isHomePlaying());
      game.setSetupOffense(false);
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
    
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fChoosingTeamId);
  	pByteList.addBoolean(fReceiveChoice);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fChoosingTeamId = pByteArray.getString();
  	fReceiveChoice = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
    IServerJsonOption.RECEIVE_CHOICE.addTo(jsonObject, fReceiveChoice);
    return jsonObject;
  }
  
  @Override
  public StepReceiveChoice initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fChoosingTeamId = IServerJsonOption.CHOOSING_TEAM_ID.getFrom(jsonObject);
    fReceiveChoice = IServerJsonOption.RECEIVE_CHOICE.getFrom(jsonObject);
    return this;
  }
  
}
