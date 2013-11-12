package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Initialization step in end game sequence.
 * 
 * @author Kalimar
 */
public final class StepInitEndGame extends AbstractStep {

	public StepInitEndGame(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_END_GAME;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		if (gameResult.getTeamResultHome().hasConceded()) {
		  int scoreDiffAway = gameResult.getTeamResultAway().getScore() - gameResult.getTeamResultHome().getScore();
		  if (scoreDiffAway <= 0) {
		    gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + Math.abs(scoreDiffAway) + 1);
		  }
		}
		if (gameResult.getTeamResultAway().hasConceded()) {
		  int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
		  if (scoreDiffHome <= 0) {
		    gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + Math.abs(scoreDiffHome) + 1);
		  }
		}
    game.setTurnMode(TurnMode.END_GAME);
    game.setConcessionPossible(false);
    if (game.getFinished() == null) {
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
  
  public StepInitEndGame initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    return this;
  }

}
