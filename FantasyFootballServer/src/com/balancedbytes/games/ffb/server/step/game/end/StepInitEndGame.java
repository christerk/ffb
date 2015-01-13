package com.balancedbytes.games.ffb.server.step.game.end;

import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Initialization step in end game sequence.
 * 
 * @author Kalimar
 */
public final class StepInitEndGame extends AbstractStep {
  
  private String fGotoLabelOnEnd;

	public StepInitEndGame(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_END_GAME;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
          // mandatory
          case GOTO_LABEL_ON_END:
            fGotoLabelOnEnd = (String) parameter.getValue();
            break;
          default:
            break;
        }
      }
    }
    if (!StringTool.isProvided(fGotoLabelOnEnd)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
    }
  }


	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    if (game.getFinished() != null) {
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
      return;
    }
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
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    return jsonObject;
  }
  
  @Override
  public StepInitEndGame initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    return this;
  }

}
