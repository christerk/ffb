package com.balancedbytes.games.ffb.server.step.action.end;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Final step of the feed sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepEndFeeding extends AbstractStep {

  private boolean fEndPlayerAction;
  private boolean fEndTurn;

  public StepEndFeeding(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.END_FEEDING;
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case END_PLAYER_ACTION:
          fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
          pParameter.consume();
          return true;
        case END_TURN:
          fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
          pParameter.consume();
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

  private void executeStep() {
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
    if (fEndTurn) {
      if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
        SequenceGenerator.getInstance().pushEndTurnSequence(getGameState());
      } else {
        UtilServerSteps.changePlayerAction(this, null, null, false);
        SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.END_OF_OWN_TURN, game.isHomePlaying());
      }
    } else if (!fEndPlayerAction && (game.getThrowerAction() != null) && game.getThrowerAction().isPassing()) {
      SequenceGenerator.getInstance().pushPassSequence(getGameState(), game.getPassCoordinate());
    } else if ((game.getTurnMode() == TurnMode.KICKOFF_RETURN) || (game.getTurnMode() == TurnMode.PASS_BLOCK)) {
      publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    } else {
      game.setPassCoordinate(null);
      UtilServerSteps.changePlayerAction(this, null, null, false);
      SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    return jsonObject;
  }

  @Override
  public StepEndFeeding initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    return this;
  }

}
