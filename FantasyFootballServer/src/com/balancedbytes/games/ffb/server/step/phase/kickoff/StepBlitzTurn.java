package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * 
 * Expects stepParameter END_TURN to be set by a preceding step. (parameter is
 * consumed on TurnMode.BLITZ)
 * 
 * @author Kalimar
 */
public final class StepBlitzTurn extends AbstractStep {

  private boolean fEndTurn;

  public StepBlitzTurn(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.BLITZ_TURN;
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    Game game = getGameState().getGame();
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case END_TURN:
          fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
          if (game.getTurnMode() == TurnMode.BLITZ) {
            pParameter.consume();
          }
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }

  private void executeStep() {

    Game game = getGameState().getGame();

    if (game.getTurnMode() == TurnMode.BLITZ) {

      if (fEndTurn) {
        game.setTurnMode(TurnMode.KICKOFF);
      }

    } else {

      game.setTurnMode(TurnMode.BLITZ);
      Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
      UtilKickoffSequence.pinPlayersInTacklezones(getGameState(), blitzingTeam);
      if (game.isTurnTimeEnabled()) {
        UtilServerTimer.stopTurnTimer(getGameState());
        game.setTurnTime(0);
        UtilServerTimer.startTurnTimer(getGameState());
      }
      game.startTurn();
      UtilServerGame.updateLeaderReRolls(this);
      // insert select sequence into kickoff sequence after this step
      getGameState().pushCurrentStepOnStack();
      SequenceGenerator.getInstance().pushSelectSequence(getGameState(), true);

    }

    getResult().setNextAction(StepAction.NEXT_STEP);

  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    return jsonObject;
  }

  @Override
  public StepBlitzTurn initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    return this;
  }

}
