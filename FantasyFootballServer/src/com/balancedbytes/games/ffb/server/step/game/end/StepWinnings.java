package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.dialog.DialogWinningsReRollParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.report.ReportWinningsRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to roll winnings.
 * 
 * Needs to be initialized with stepParameter AUTOMATIC_RE_ROLL.
 * 
 * @author Kalimar
 */
public final class StepWinnings extends AbstractStepWithReRoll {
	
	private boolean fAutomaticReRoll;

	public StepWinnings(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.WINNINGS;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				// mandatory
  				case AUTOMATIC_RE_ROLL:
  					fAutomaticReRoll = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
  					break;
					default:
						break;
  			}
  		}
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
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    if ((getReRolledAction() == null) || ((getReRolledAction() == ReRolledAction.WINNINGS) && (getReRollSource() != null))) {
      ReportWinningsRoll reportWinnings = rollWinnings();
      if (fAutomaticReRoll) {
      	// roll winnings, reroll on a 1 or 2 -->
        GameResult gameResult = game.getGameResult();
        int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
        if (((scoreDiffHome > 0) && (reportWinnings.getWinningsRollHome() < 3)) || ((scoreDiffHome < 0) && (reportWinnings.getWinningsRollAway() < 3))) {
        	reportWinnings = rollWinnings();
        }
        // <--
      	UtilServerDialog.hideDialog(getGameState());
      }
      getResult().addReport(reportWinnings);
    }
    if (game.getDialogParameter() == null) {
      getResult().addReport(concedeWinnings());
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private ReportWinningsRoll rollWinnings() {
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult(); 
    int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
    int winningsHome = 0;
    int rollHome = 0;
    if ((getReRolledAction() == null) || (scoreDiffHome > 0)) {
      rollHome = getGameState().getDiceRoller().rollWinnings();
      winningsHome = rollHome + gameResult.getTeamResultHome().getFame();
      if (scoreDiffHome >= 0) {
        winningsHome++;
      }
      gameResult.getTeamResultHome().setWinnings(winningsHome * 10000);
    }
    int winningsAway = 0;
    int rollAway = 0;
    if ((getReRolledAction() == null) || (scoreDiffHome < 0)) {
      rollAway = getGameState().getDiceRoller().rollWinnings();
      winningsAway = rollAway + gameResult.getTeamResultAway().getFame();
      if (scoreDiffHome <= 0) {
        winningsAway++;
      }
      gameResult.getTeamResultAway().setWinnings(winningsAway * 10000);
    }
    if (getReRolledAction() == null) {
      if (scoreDiffHome > 0) {
        UtilServerDialog.showDialog(getGameState(), new DialogWinningsReRollParameter(game.getTeamHome().getId(), rollHome));
      }
      if (scoreDiffHome < 0) {
        UtilServerDialog.showDialog(getGameState(), new DialogWinningsReRollParameter(game.getTeamAway().getId(), rollAway));
      }
    }
    return new ReportWinningsRoll(rollHome, gameResult.getTeamResultHome().getWinnings(), rollAway, gameResult.getTeamResultAway().getWinnings());
  }
  
  private ReportWinningsRoll concedeWinnings() {
    ReportWinningsRoll report = null;
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult(); 
    if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
      gameResult.getTeamResultAway().setWinnings(gameResult.getTeamResultAway().getWinnings() + gameResult.getTeamResultHome().getWinnings());
      gameResult.getTeamResultHome().setWinnings(0);
      report = new ReportWinningsRoll(0, gameResult.getTeamResultHome().getWinnings(), 0, gameResult.getTeamResultAway().getWinnings());
    }
    if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
      gameResult.getTeamResultHome().setWinnings(gameResult.getTeamResultHome().getWinnings() + gameResult.getTeamResultAway().getWinnings());
      gameResult.getTeamResultAway().setWinnings(0);
      report = new ReportWinningsRoll(0, gameResult.getTeamResultHome().getWinnings(), 0, gameResult.getTeamResultAway().getWinnings());
    }
    return report;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.AUTOMATIC_RE_ROLL.addTo(jsonObject, fAutomaticReRoll);
    return jsonObject;
  }
  
  @Override
  public StepWinnings initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fAutomaticReRoll = IServerJsonOption.AUTOMATIC_RE_ROLL.getFrom(jsonObject);
    return this;
  }

}
