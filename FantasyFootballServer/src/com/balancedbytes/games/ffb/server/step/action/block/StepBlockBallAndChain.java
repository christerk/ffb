package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
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
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the block sequence to handle skill BALL_AND_CHAIN.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_PUSHBACK.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter STARTING_PUSHBACK_SQUARE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepBlockBallAndChain extends AbstractStep {

  private String fGotoLabelOnPushback;
  private PlayerState fOldDefenderState;

  public StepBlockBallAndChain(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.BLOCK_BALL_AND_CHAIN;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
        // mandatory
        case GOTO_LABEL_ON_PUSHBACK:
          fGotoLabelOnPushback = (String) parameter.getValue();
          break;
        default:
          break;
        }
      }
    }
    if (!StringTool.isProvided(fGotoLabelOnPushback)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_PUSHBACK + " is not initialized.");
    }
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
      case OLD_DEFENDER_STATE:
        fOldDefenderState = (PlayerState) pParameter.getValue();
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
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN) && (fOldDefenderState != null) && fOldDefenderState.isProne()) {
      publishParameters(UtilBlockSequence.initPushback(this));
      game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState.changeBase(PlayerState.FALLING));
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
    } else {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fGotoLabelOnPushback = pByteArray.getString();
    int oldDefenderStateId = pByteArray.getSmallInt();
    fOldDefenderState = (oldDefenderStateId > 0) ? new PlayerState(oldDefenderStateId) : null;
    return byteArraySerializationVersion;
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.addTo(jsonObject, fGotoLabelOnPushback);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    return jsonObject;
  }

  @Override
  public StepBlockBallAndChain initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnPushback = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}