package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogCoinChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogReceiveChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCoinChoice;
import com.balancedbytes.games.ffb.report.ReportCoinThrow;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to choose coin.
 * 
 * Sets stepParameter CHOOSING_TEAM_ID for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepCoinChoice extends AbstractStep {
	
	protected Boolean fCoinChoiceHeads;
	
	public StepCoinChoice(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.COIN_CHOICE;
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
			  case CLIENT_COIN_CHOICE:
			    ClientCommandCoinChoice coinChoiceCommand = (ClientCommandCoinChoice) pNetCommand;
			    fCoinChoiceHeads = coinChoiceCommand.isChoiceHeads();
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
    if (fCoinChoiceHeads == null) {
      UtilDialog.showDialog(getGameState(), new DialogCoinChoiceParameter());
    } else {
      boolean coinThrowHeads = getGameState().getDiceRoller().throwCoin();
      Team choosingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
      getResult().addReport(new ReportCoinThrow(coinThrowHeads, choosingTeam.getCoach(), fCoinChoiceHeads));
      if ((game.isHomePlaying() && (coinThrowHeads != fCoinChoiceHeads) || (!game.isHomePlaying() && (coinThrowHeads == fCoinChoiceHeads)))) {
        choosingTeam = game.getTeamAway();
      } else {
        choosingTeam = game.getTeamHome();
      }
      publishParameter(new StepParameter(StepParameterKey.CHOOSING_TEAM_ID, choosingTeam.getId()));
      UtilDialog.showDialog(getGameState(), new DialogReceiveChoiceParameter(choosingTeam.getId()));
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
  	pByteList.addBoolean(fCoinChoiceHeads);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fCoinChoiceHeads = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.COIN_CHOICE_HEADS.addTo(jsonObject, fCoinChoiceHeads);
    return jsonObject;
  }
  
  @Override
  public StepCoinChoice initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fCoinChoiceHeads = IServerJsonOption.COIN_CHOICE_HEADS.getFrom(jsonObject);
    return this;
  }
  
}
