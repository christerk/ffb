package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class StepSwarming extends AbstractStep {

  private boolean fEndTurn;
  private boolean handleKickingTeam;

  public StepSwarming(GameState pGameState) {
    super(pGameState);
  }

  @Override
  public void start() {
    executeStep();
  }

  @Override
  public StepId getId() {
    return StepId.SWARMING;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    super.init(pParameterSet);
    if (pParameterSet != null) {
      for (StepParameter parameter: pParameterSet.values()) {
        if (parameter.getKey() == StepParameterKey.HANDLE_KICKING_TEAM) {
          handleKickingTeam = (boolean) parameter.getValue();
        }
      }
    }
  }

  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (pReceivedCommand.getCommand().getId() == NetCommandId.CLIENT_END_TURN) {
      fEndTurn = true;
      executeStep();
    }
    return commandStatus;
  }

  private void executeStep() {

  }

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.HANDLE_KICKING_TEAM.addTo(jsonObject, handleKickingTeam);
    return jsonObject;
  }

  @Override
  public StepSwarming initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    handleKickingTeam = IServerJsonOption.HANDLE_KICKING_TEAM.getFrom(jsonObject);
    return this;
  }
}
