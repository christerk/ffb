package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogConcedeGameParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConcedeGame;
import com.balancedbytes.games.ffb.net.commands.ClientCommandIllegalProcedure;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.report.ReportTimeoutEnforced;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;

/**
 * 
 * @author Kalimar
 */
public abstract class AbstractStep implements IStep {
	
	private GameState fGameState;
	private StepResult fStepResult;
	private String fLabel;

	protected AbstractStep(GameState pGameState) {
		fGameState = pGameState;
		setStepResult(new StepResult());
	}
	
	public void setLabel(String pLabel) {
		fLabel = pLabel;
//		System.out.println("setLabel(" + pLabel + ")");
	}
	
	public String getLabel() {
		return fLabel;
	}

	public GameState getGameState() {
		return fGameState;
	}
	
	private void setStepResult(StepResult pStepResult) {
		fStepResult = pStepResult;
	}

	public StepResult getResult() {
		return fStepResult;
	}
	
	public void init(StepParameterSet pParameterSet) {
		// do nothing, override in subclass if needed
	}
	
	public void start() {
		// do nothing, override in subclass if needed
	}
		
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		switch (pNetCommand.getId()) {
			case CLIENT_CONCEDE_GAME:
				commandStatus = handleConcedeGame((ClientCommandConcedeGame) pNetCommand);
				break;
			case CLIENT_ILLEGAL_PROCEDURE:
				commandStatus = handleIllegalProcedure((ClientCommandIllegalProcedure) pNetCommand);
				break;
			default:
				break;
		}
		return commandStatus;
	}
	
	public boolean setParameter(StepParameter pParameter) {
		// do nothing, override in subclass if needed
		return false;
	}
	
	protected void publishParameter(StepParameter pParameter) {
		if (pParameter != null) {
			DebugLog debugLog = getGameState().getServer().getDebugLog();
			if (debugLog.isLogging(IServerLogLevel.TRACE)) { 
  			StringBuilder trace = new StringBuilder();
  			trace.append(getId()).append(" publishes ").append(pParameter.getKey()).append("=").append(pParameter.getValue());
  			debugLog.log(IServerLogLevel.TRACE, trace.toString());
			}
			setParameter(pParameter);
			getGameState().getStepStack().publishStepParameter(pParameter);
		}
	}
	
	protected void publishParameters(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				publishParameter(parameter);
			}
		}
	}
	
	public void addTo(ByteList pByteList) {
		pByteList.addSmallInt(getId().getId());
		pByteList.addSmallInt(getByteArraySerializationVersion());
		pByteList.addString(getLabel());
		getResult().addTo(pByteList);
	}
	
	public int initFrom(ByteArray pByteArray) {
		StepId stepId = StepId.fromId(pByteArray.getSmallInt());
    if (getId() != stepId) {
      throw new IllegalStateException("Wrong step id. Expected " + getId() + " received " + ((stepId != null) ? stepId : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setLabel(pByteArray.getString());
    setStepResult(new StepResult());
    getResult().initFrom(pByteArray);
    return byteArraySerializationVersion;
	}
	
	private StepCommandStatus handleConcedeGame(ClientCommandConcedeGame pConcedeGameCommand) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    if (pConcedeGameCommand.getStatus() != null) {
    	ChannelManager channelManager = getGameState().getServer().getChannelManager();
    	boolean homeCommand = (channelManager.getChannelOfHomeCoach(getGameState()) == pConcedeGameCommand.getSender());
      boolean awayCommand = (channelManager.getChannelOfAwayCoach(getGameState()) == pConcedeGameCommand.getSender());
      switch (pConcedeGameCommand.getStatus()) {
        case REQUESTED:
          if (game.isConcessionPossible() && ((game.isHomePlaying() && homeCommand) || (!game.isHomePlaying() && awayCommand))) {
            UtilDialog.showDialog(getGameState(), new DialogConcedeGameParameter());
          }
          break;
        case CONFIRMED:
          game.setConcessionPossible(false);
          gameResult.getTeamResultHome().setConceded(game.isHomePlaying() && homeCommand);
          gameResult.getTeamResultAway().setConceded(!game.isHomePlaying() && awayCommand);
          break;
        case DENIED:
          UtilDialog.hideDialog(getGameState());
          break;
      }
	    if (gameResult.getTeamResultHome().hasConceded() || gameResult.getTeamResultAway().hasConceded()) {
        getGameState().getStepStack().clear();
        SequenceGenerator.getInstance().pushEndGameSequence(getGameState(), false);
        getResult().setNextAction(StepAction.NEXT_STEP);
	    }
    	commandStatus = StepCommandStatus.SKIP_STEP;
    }
    return commandStatus;
	}
	
	private StepCommandStatus handleIllegalProcedure(ClientCommandIllegalProcedure pIllegalProcedureCommand) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
    Game game = getGameState().getGame();
    if (game.isTimeoutPossible()) {
      ReportList reports = new ReportList();
  		FantasyFootballServer server = getGameState().getServer();
      String coach = server.getChannelManager().getCoachForChannel(pIllegalProcedureCommand.getSender());
      reports.add(new ReportTimeoutEnforced(coach));
      game.setTimeoutEnforced(true);
      game.setTimeoutPossible(false);
      UtilGame.syncGameModel(getGameState(), reports, null, Sound.WHISTLE);
      commandStatus = StepCommandStatus.EXECUTE_STEP;
    }
    return commandStatus;
	}
	
}
