package com.balancedbytes.games.ffb.server.step.game.start;

import com.balancedbytes.games.ffb.dialog.DialogPettyCashParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPettyCash;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportPettyCash;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in start game sequence to handle petty cash.
 * 
 * @author Kalimar
 */
public final class StepPettyCash extends AbstractStep {
	
  private boolean fPettyCashSelectedHome;
  private boolean fPettyCashSelectedAway;
  private boolean fReportedHome;
  private boolean fReportedAway;

	public StepPettyCash(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.PETTY_CASH;
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
				case CLIENT_PETTY_CASH:
		      ClientCommandPettyCash pettyCashCommand = (ClientCommandPettyCash) pReceivedCommand.getCommand();
		      GameResult gameResult = getGameState().getGame().getGameResult();
		      if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
		        gameResult.getTeamResultHome().setPettyCashTransferred(pettyCashCommand.getPettyCash());
		        fPettyCashSelectedHome = true;
		      } else {
		        gameResult.getTeamResultAway().setPettyCashTransferred(pettyCashCommand.getPettyCash());
		        fPettyCashSelectedAway = true;
		      }
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
    GameResult gameResult = game.getGameResult();
    gameResult.getTeamResultHome().setTeamValue(Math.max(gameResult.getTeamResultHome().getTeamValue(), game.getTeamHome().getTeamValue())); 
    gameResult.getTeamResultAway().setTeamValue(Math.max(gameResult.getTeamResultAway().getTeamValue(), game.getTeamAway().getTeamValue())); 
    if (UtilGameOption.isOptionEnabled(game, GameOptionId.PETTY_CASH)) {
      if (game.getTeamHome().getTreasury() < 50000 || (fPettyCashSelectedAway && ((game.getTeamAway().getTeamValue() - game.getTeamHome().getTeamValue()) > game.getTeamHome().getTreasury())) ) {
      	fPettyCashSelectedHome = true;
      }
      if (game.getTeamAway().getTreasury() < 50000  || (fPettyCashSelectedHome && ((game.getTeamHome().getTeamValue() - game.getTeamAway().getTeamValue()) > game.getTeamAway().getTreasury()))) {
      	fPettyCashSelectedAway = true;
      }    
      if (fPettyCashSelectedHome && !fReportedHome) {
      	fReportedHome = true;
        gameResult.getTeamResultHome().setTeamValue(gameResult.getTeamResultHome().getTeamValue() + gameResult.getTeamResultHome().getPettyCashTransferred());
        getResult().addReport(new ReportPettyCash(game.getTeamHome().getId(), gameResult.getTeamResultHome().getPettyCashTransferred()));
      }
      if (fPettyCashSelectedAway && !fReportedAway) {
      	fReportedAway = true;
        gameResult.getTeamResultAway().setTeamValue(gameResult.getTeamResultAway().getTeamValue() + gameResult.getTeamResultAway().getPettyCashTransferred()); 
        getResult().addReport(new ReportPettyCash(game.getTeamAway().getId(), gameResult.getTeamResultAway().getPettyCashTransferred()));
      }
      if (!fPettyCashSelectedHome && !fPettyCashSelectedAway) {
        if (game.getTeamHome().getTeamValue() >= game.getTeamAway().getTeamValue()) {
          UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamHome().getId(), game.getTeamHome().getTeamValue(), game.getTeamHome().getTreasury(), game.getTeamAway().getTeamValue()));
        }
        if (game.getTeamAway().getTeamValue() > game.getTeamHome().getTeamValue()) {
          UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamAway().getId(), game.getTeamAway().getTeamValue(), game.getTeamAway().getTreasury(), game.getTeamHome().getTeamValue()));
        }
      } else if (!fPettyCashSelectedHome) {
        UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamHome().getId(), gameResult.getTeamResultHome().getTeamValue(), game.getTeamHome().getTreasury(), gameResult.getTeamResultAway().getTeamValue()));
      } else if (!fPettyCashSelectedAway) {
        UtilServerDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamAway().getId(), gameResult.getTeamResultAway().getTeamValue(), game.getTeamAway().getTreasury(), gameResult.getTeamResultHome().getTeamValue()));
      } else {
      	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.PETTY_CASH_SELECTED_HOME.addTo(jsonObject, fPettyCashSelectedHome);
    IServerJsonOption.PETTY_CASH_SELECTED_AWAY.addTo(jsonObject, fPettyCashSelectedAway);
    IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);
    IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
    return jsonObject;
  }
  
  @Override
  public StepPettyCash initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fPettyCashSelectedHome = IServerJsonOption.PETTY_CASH_SELECTED_HOME.getFrom(jsonObject);
    fPettyCashSelectedAway = IServerJsonOption.PETTY_CASH_SELECTED_AWAY.getFrom(jsonObject);
    fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(jsonObject);
    fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(jsonObject);
    return this;
  }

}
