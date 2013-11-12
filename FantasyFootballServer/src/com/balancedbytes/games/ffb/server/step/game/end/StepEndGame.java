package com.balancedbytes.games.ffb.server.step.game.end;

import java.util.Date;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestUploadResults;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Final step in end game sequence.
 * 
 * @author Kalimar
 */
public final class StepEndGame extends AbstractStep {

	public StepEndGame(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_GAME;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

  private void executeStep() {
    Game game = getGameState().getGame();
    GameCache gameCache = getGameState().getServer().getGameCache();
    if (game.getFinished() == null) {
      game.setFinished(new Date());
      getGameState().setStatus(GameStatus.FINISHED);
      getGameState().getStepStack().clear();  // clean up after ourselves
      gameCache.queueDbUpdate(getGameState());
      UtilDialog.showDialog(getGameState(), DialogId.GAME_STATISTICS.createDialogParameter());
      FantasyFootballServer server = getGameState().getServer();
      if ((server.getMode() == ServerMode.FUMBBL) && !game.isTesting()) {
        server.getFumbblRequestProcessor().add(new FumbblRequestUploadResults(getGameState()));
      }
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

	public int getByteArraySerializationVersion() {
		return 1;
	}
	
  // JSON serialization
  
  public JsonObject toJsonValue() {
    return toJsonValueTemp();
  }
  
  public StepEndGame initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    return this;
  }

}
