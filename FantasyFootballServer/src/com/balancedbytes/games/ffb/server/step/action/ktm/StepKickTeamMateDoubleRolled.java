package com.balancedbytes.games.ffb.server.step.action.ktm;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeKTMCrowd;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class StepKickTeamMateDoubleRolled extends AbstractStep {
  private String fKickedPlayerId;
  private PlayerState fKickedPlayerState;
  private FieldCoordinate fKickedPlayerCoordinate;

  public StepId getId() {
    return StepId.KICK_TM_DOUBLE_ROLLED;
  }
  
  public StepKickTeamMateDoubleRolled(GameState pGameState) {
    super(pGameState);
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case KICKED_PLAYER_ID:
          fKickedPlayerId = (String) pParameter.getValue();
          return true;
        case KICKED_PLAYER_STATE:
          fKickedPlayerState = (PlayerState) pParameter.getValue();
          return true;
        case KICKED_PLAYER_COORDINATE:
          fKickedPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
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
    Player kickedPlayer = game.getPlayerById(fKickedPlayerId);
    if ((kickedPlayer != null) && (fKickedPlayerCoordinate != null) && (fKickedPlayerState != null) && (fKickedPlayerState.getId() > 0)) {
      game.getFieldModel().setPlayerCoordinate(kickedPlayer, fKickedPlayerCoordinate);
      game.getFieldModel().setPlayerState(game.getDefender(), fKickedPlayerState);
      game.setDefenderId(null);
      InjuryResult injury = UtilServerInjury.handleInjury(this, new InjuryTypeKTMCrowd(), null, kickedPlayer, fKickedPlayerCoordinate, null, ApothecaryMode.THROWN_PLAYER);
      publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injury));
      
      if (fKickedPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
        game.getFieldModel().setBallMoving(true);
        publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
        publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
      }
      
    }
    publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_COORDINATE, null));  // avoid reset in end step
    getResult().setNextAction(StepAction.NEXT_STEP);
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
    IServerJsonOption.KICKED_PLAYER_STATE.addTo(jsonObject, fKickedPlayerState);
    IServerJsonOption.KICKED_PLAYER_COORDINATE.addTo(jsonObject, fKickedPlayerCoordinate);
    return jsonObject;
  }
  
  @Override
  public StepKickTeamMateDoubleRolled initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(jsonObject);
    fKickedPlayerState = IServerJsonOption.KICKED_PLAYER_STATE.getFrom(jsonObject);
    fKickedPlayerCoordinate = IServerJsonOption.KICKED_PLAYER_COORDINATE.getFrom(jsonObject);
    return this;
  }
  
}
