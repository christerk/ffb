package com.balancedbytes.games.ffb.server.step.game.start;

import java.util.Date;

import com.balancedbytes.games.ffb.GameOptionValue;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestCreateGamestate;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * Step to init the start game sequence.
 * 
 * @author Kalimar
 */
public final class StepInitStartGame extends AbstractStep {
	
	private boolean fFumbblGameCreated;
		
	public StepInitStartGame(GameState pGameState) {
		super(pGameState);
    getResult().setSynchronize(false);
	}
	
	public StepId getId() {
		return StepId.INIT_START_GAME;
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
			Game game = getGameState().getGame();
			switch (pNetCommand.getId()) {
		    case INTERNAL_SERVER_FUMBBL_GAME_CREATED:
		    	fFumbblGameCreated = true;
	        commandStatus = StepCommandStatus.EXECUTE_STEP;
	        break;
        case CLIENT_START_GAME:
          if (game.getStarted() == null) {
            game.setStarted(new Date(0));
          } else {
            game.setStarted(new Date());
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
  	FantasyFootballServer server = getGameState().getServer();
  	GameCache gameCache = server.getGameCache();
    if ((game.getStarted() != null) && (game.getStarted().getTime() > 0)) {
      gameCache.removeMappingForGameId(getGameState().getId());
      if (server.getMode() == ServerMode.FUMBBL) {
      	if (fFumbblGameCreated) {
      		leaveStep();
      	} else {
          server.getFumbblRequestProcessor().add(new FumbblRequestCreateGamestate(getGameState()));
      	}
      } else {
    		leaveStep();
      }
    }
  }
  
  private void leaveStep() {
    Game game = getGameState().getGame();
  	FantasyFootballServer server = getGameState().getServer();
  	GameCache gameCache = server.getGameCache();
    if ((game != null) && (game.getTeamHome() != null) && (game.getTeamAway() != null)) {
    	getGameState().setStatus(GameStatus.ACTIVE);
    	gameCache.queueDbUpdate(getGameState());
      // log start game -->
      StringBuilder logEntry = new StringBuilder();
      logEntry.append("START GAME ").append(StringTool.print(game.getTeamHome().getName())).append(" vs. ").append(StringTool.print(game.getTeamAway().getName()));
      server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), logEntry.toString());
      if (game.getOptions() != null) {
      	StringBuilder optionValues = new StringBuilder();
        for (GameOptionValue option : game.getOptions().getOptionValues()) {
        	if (option.getValue() != option.getOption().getDefaultValue()) {
        		optionValues.append(" ").append(option.getOption().getName()).append("=").append(option.getValue());
        	}
    		}
        if (optionValues.length() > 0) {
          server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "Options" + optionValues.toString());
        } else {
          server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "Default Options");
        }
      }
  		// <-- log start game
    }
		getResult().setNextAction(StepAction.NEXT_STEP);
  }
    
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fFumbblGameCreated);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fFumbblGameCreated = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}
