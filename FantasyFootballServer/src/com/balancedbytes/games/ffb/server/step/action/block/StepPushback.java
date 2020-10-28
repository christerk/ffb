package com.balancedbytes.games.ffb.server.step.action.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Pushback;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPushback;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPushback;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.StateStep;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle pushbacks.
 * 
 * Expects stepParameter STARTING_PUSHBACK_SQUARE to be set by a preceding step.
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter DEFENDER_PUSHED for all steps on the stack. Sets
 * stepParameter FOLLOWUP_CHOICE for all steps on the stack. Sets stepParameter
 * STARTING_PUSHBACK_SQUARE for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepPushback extends AbstractStep {

  public class StepState {
    public PlayerState oldDefenderState;
    public PushbackSquare startingPushbackSquare;
    public Boolean grabbing;
    public Map<String, Boolean> sideStepping;
    public Map<String, Boolean> standingFirm;
    public Stack<Pushback> pushbackStack;
    
    // Transients
    public Player defender;
    public boolean doPush;
    public boolean continueStep;
    public boolean freeSquareAroundDefender;
    public PushbackMode pushbackMode;
    public PushbackSquare[] pushbackSquares;
  }

  private StepState state;

  public StepPushback(GameState pGameState) {
    super(pGameState);
    state = new StepState();
    
    state.pushbackStack = new Stack<>();
    state.sideStepping = new HashMap<>();
    state.standingFirm = new HashMap<>();
  }

  public StepId getId() {
    return StepId.PUSHBACK;
  }

  @Override
  public void start() {
    super.start();
    executeStep();
  }

  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
      case CLIENT_USE_SKILL:
        ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
        ServerSkill usedSkill = (ServerSkill) useSkillCommand.getSkill();

        if (usedSkill != null) {
          StepCommandStatus newStatus = usedSkill.applyUseSkillCommandHooks(this, state, useSkillCommand);
          if (newStatus != null) {
            commandStatus = newStatus;
          }
        }
        break;
      case CLIENT_PUSHBACK:
        ClientCommandPushback pushbackCommand = (ClientCommandPushback) pReceivedCommand.getCommand();
        if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
          state.pushbackStack.push(pushbackCommand.getPushback());
        } else {
          state.pushbackStack.push(pushbackCommand.getPushback().transform());
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

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
      case OLD_DEFENDER_STATE:
        state.oldDefenderState = (PlayerState) pParameter.getValue();
        return true;
      case STARTING_PUSHBACK_SQUARE:
        state.startingPushbackSquare = (PushbackSquare) pParameter.getValue();
        return true;
      default:
        break;
      }
    }
    return false;
  }

  private void executeStep() {
    state.doPush = false;
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    FieldModel fieldModel = game.getFieldModel();
    // player chose a coordinate
    if (state.pushbackStack.size() > 0) {
      Pushback lastPushback = state.pushbackStack.pop();
      state.pushbackStack.push(lastPushback);
      state.pushbackSquares = fieldModel.getPushbackSquares();
      for (int i = 0; i < state.pushbackSquares.length; i++) {
        if (!state.pushbackSquares[i].isLocked()) {
          fieldModel.remove(state.pushbackSquares[i]);
          if (state.pushbackSquares[i].getCoordinate().equals(lastPushback.getCoordinate())) {
            publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, state.pushbackSquares[i]));
            state.pushbackSquares[i].setSelected(true);
            state.pushbackSquares[i].setLocked(true);
            fieldModel.add(state.pushbackSquares[i]);
          }
        }
      }
      state.doPush = (fieldModel.getPlayer(lastPushback.getCoordinate()) == null);
    }
    // calculate new pushback squares
    if (!state.doPush && (state.startingPushbackSquare != null)) {

      FieldCoordinate defenderCoordinate = state.startingPushbackSquare.getCoordinate();
      state.defender = fieldModel.getPlayer(defenderCoordinate);
      if (state.defender == null) {
        throw new IllegalStateException("Defender unknown at this point - cannot continue.");
      }
      state.pushbackMode = PushbackMode.REGULAR;
      PushbackSquare[] pushbackSquares = UtilServerPushback.findPushbackSquares(game, state.startingPushbackSquare,
          state.pushbackMode);
      fieldModel.add(pushbackSquares);
      state.freeSquareAroundDefender = false;
      FieldCoordinate[] adjacentSquares = fieldModel.findAdjacentCoordinates(state.startingPushbackSquare.getCoordinate(),
          FieldCoordinateBounds.FIELD, 1, false);
      for (int i = 0; !state.freeSquareAroundDefender && (i < adjacentSquares.length); i++) {
        if (fieldModel.getPlayer(adjacentSquares[i]) == null) {
          state.freeSquareAroundDefender = true;
        }
      }

      boolean stopProcessing = getGameState().executeStepHooks(this, state);
      
      if (!stopProcessing) {
        if (!ArrayTool.isProvided(pushbackSquares)) {
          // Crowdpush
          publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
              UtilServerInjury.handleInjury(this, InjuryType.CROWDPUSH, null, state.defender,
                  state.startingPushbackSquare.getCoordinate(), null, ApothecaryMode.CROWD_PUSH)));
          game.getFieldModel().remove(state.defender);
          if (defenderCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
            game.getFieldModel().setBallCoordinate(null);
            publishParameter(
                new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
            publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, defenderCoordinate));
          }
          publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
          state.doPush = true;
        }
      }
      if (state.startingPushbackSquare == null) {
        getResult().addReport(new ReportPushback(state.defender.getId(), state.pushbackMode));
      }
    }
    if (state.doPush) {
      publishParameter(new StepParameter(StepParameterKey.DEFENDER_PUSHED, true));
      if (state.pushbackStack.size() > 0) {
        while (state.pushbackStack.size() > 0) {
          Pushback pushback = state.pushbackStack.pop();
          Player player = game.getPlayerById(pushback.getPlayerId());
          pushPlayer(player, pushback.getCoordinate());
        }
      }
      fieldModel.clearPushbackSquares();
      publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
      game.setWaitingForOpponent(false);
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void pushPlayer(Player pPlayer, FieldCoordinate pCoordinate) {
    Game game = getGameState().getGame();
    FieldModel fieldModel = game.getFieldModel();
    fieldModel.updatePlayerAndBallPosition(pPlayer, pCoordinate);
    UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
    if (fieldModel.isBallMoving() && pCoordinate.equals(fieldModel.getBallCoordinate())) {
      publishParameter(
          new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
    }
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
    if (state.startingPushbackSquare != null) {
      IServerJsonOption.STARTING_PUSHBACK_SQUARE.addTo(jsonObject, state.startingPushbackSquare.toJsonValue());
    }
    IServerJsonOption.USING_GRAB.addTo(jsonObject, state.grabbing);
    IServerJsonOption.USING_SIDE_STEP.addTo(jsonObject, state.sideStepping);
    IServerJsonOption.USING_STAND_FIRM.addTo(jsonObject, state.standingFirm);
    return jsonObject;
  }

  @Override
  public StepPushback initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    state.startingPushbackSquare = null;
    JsonObject startingPushbackSquareObject = IServerJsonOption.STARTING_PUSHBACK_SQUARE.getFrom(jsonObject);
    if (startingPushbackSquareObject != null) {
      state.startingPushbackSquare = new PushbackSquare().initFrom(startingPushbackSquareObject);
    }
    state.grabbing = IServerJsonOption.USING_GRAB.getFrom(jsonObject);
    state.sideStepping = IServerJsonOption.USING_SIDE_STEP.getFrom(jsonObject);
    state.standingFirm = IServerJsonOption.USING_STAND_FIRM.getFrom(jsonObject);
    return this;
  }
}
