package com.balancedbytes.games.ffb.server.step.game.start;

import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPettyCashParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPettyCash;
import com.balancedbytes.games.ffb.report.ReportPettyCash;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;

/**
 * Step in start game sequence to handle petty cash.
 * 
 * @author Kalimar
 */
public final class StepPettyCash extends AbstractStep {
	
	protected boolean fPettyCashSelectedHome;
	protected boolean fPettyCashSelectedAway;
	
  protected boolean fReportedHome;
  protected boolean fReportedAway;

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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
				case CLIENT_PETTY_CASH:
		      ClientCommandPettyCash pettyCashCommand = (ClientCommandPettyCash) pNetCommand;
		      GameResult gameResult = getGameState().getGame().getGameResult();
		      if (UtilSteps.checkCommandIsFromHomePlayer(getGameState(), pettyCashCommand)) {
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
    if (game.getOptions().getOptionValue(GameOption.PETTY_CASH).isEnabled()) {
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
          UtilDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamHome().getId(), game.getTeamHome().getTeamValue(), game.getTeamHome().getTreasury(), game.getTeamAway().getTeamValue()));
        }
        if (game.getTeamAway().getTeamValue() > game.getTeamHome().getTeamValue()) {
          UtilDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamAway().getId(), game.getTeamAway().getTeamValue(), game.getTeamAway().getTreasury(), game.getTeamHome().getTeamValue()));
        }
      } else if (!fPettyCashSelectedHome) {
        UtilDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamHome().getId(), gameResult.getTeamResultHome().getTeamValue(), game.getTeamHome().getTreasury(), gameResult.getTeamResultAway().getTeamValue()));
      } else if (!fPettyCashSelectedAway) {
        UtilDialog.showDialog(getGameState(), new DialogPettyCashParameter(game.getTeamAway().getId(), gameResult.getTeamResultAway().getTeamValue(), game.getTeamAway().getTreasury(), gameResult.getTeamResultHome().getTeamValue()));
      } else {
      	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
    
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fPettyCashSelectedHome);
  	pByteList.addBoolean(fPettyCashSelectedAway);
  	pByteList.addBoolean(fReportedHome);
  	pByteList.addBoolean(fReportedAway);
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fPettyCashSelectedHome = pByteArray.getBoolean();
  	fPettyCashSelectedAway = pByteArray.getBoolean();
  	fReportedHome = pByteArray.getBoolean();
  	fReportedAway = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
}
