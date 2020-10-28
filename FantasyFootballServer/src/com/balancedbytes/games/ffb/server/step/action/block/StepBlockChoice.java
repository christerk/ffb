package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportBlockChoice;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle the block choice.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_DODGE. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_JUGGERNAUT. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_PUSHBACK.
 *
 * Expects stepParameter BLOCK_DICE_INDEX to be set by a preceding step. Expects
 * stepParameter BLOCK_RESULT to be set by a preceding step. Expects
 * stepParameter BLOCK_ROLL to be set by a preceding step. Expects stepParameter
 * NR_OF_BLOCK_DICE to be set by a preceding step. Expects stepParameter
 * OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBlockChoice extends AbstractStep {

  private String fGotoLabelOnDodge;
  private String fGotoLabelOnJuggernaut;
  private String fGotoLabelOnPushback;

  private int fNrOfDice;
  private int[] fBlockRoll;
  private int fDiceIndex;
  private BlockResult fBlockResult;
  private PlayerState fOldDefenderState;

  public StepBlockChoice(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.BLOCK_CHOICE;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
        // mandatory
          case GOTO_LABEL_ON_DODGE:
            fGotoLabelOnDodge = (String) parameter.getValue();
            break;
          // mandatory
          case GOTO_LABEL_ON_JUGGERNAUT:
            fGotoLabelOnJuggernaut = (String) parameter.getValue();
            break;
          // mandatory
          case GOTO_LABEL_ON_PUSHBACK:
            fGotoLabelOnPushback = (String) parameter.getValue();
            break;
          default:
            break;
        }
      }
    }
    if (!StringTool.isProvided(fGotoLabelOnDodge)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_DODGE + " is not initialized.");
    }
    if (!StringTool.isProvided(fGotoLabelOnJuggernaut)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT + " is not initialized.");
    }
    if (!StringTool.isProvided(fGotoLabelOnPushback)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_PUSHBACK + " is not initialized.");
    }
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

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case DICE_INDEX:
          fDiceIndex = (Integer) pParameter.getValue();
          return true;
        case BLOCK_RESULT:
          fBlockResult = (BlockResult) pParameter.getValue();
          return true;
        case BLOCK_ROLL:
          fBlockRoll = (int[]) pParameter.getValue();
          return true;
        case NR_OF_DICE:
          fNrOfDice = (Integer) pParameter.getValue();
          return true;
        case OLD_DEFENDER_STATE:
          fOldDefenderState = (PlayerState) pParameter.getValue();
          return true;
        default:
          break;
      }
    }
    return false;
  }

  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    UtilServerDialog.hideDialog(getGameState());
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
    switch (fBlockResult) {
      case SKULL:
        game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), attackerState.changeBase(PlayerState.FALLING));
        game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
        getResult().setNextAction(StepAction.NEXT_STEP);
        break;
      case BOTH_DOWN:
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnJuggernaut);
        break;
      case POW_PUSHBACK:
        if (UtilCards.hasSkill(game, game.getDefender(), ServerSkill.DODGE)) {
          if (UtilCards.hasSkill(game, actingPlayer, ServerSkill.TACKLE)
              && (!UtilCards.hasSkill(game, actingPlayer, ServerSkill.BALL_AND_CHAIN) || actingPlayer.getPlayer().getTeam() != game.getDefender().getTeam())) {
            if (UtilGameOption.isOptionEnabled(game, GameOptionId.RIGHT_STUFF_CANCELS_TACKLE)
                && UtilCards.hasSkill(game, game.getDefender(), ServerSkill.RIGHT_STUFF)) {
              getResult().addReport(new ReportSkillUse(game.getDefenderId(), ServerSkill.RIGHT_STUFF, true, SkillUse.CANCEL_TACKLE));
              getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnDodge);
            } else {
              getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), ServerSkill.TACKLE, true, SkillUse.CANCEL_DODGE));
              game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
              publishParameters(UtilBlockSequence.initPushback(this));
              getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
            }
          } else {
            getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnDodge);
          }
        } else {
          game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
          publishParameters(UtilBlockSequence.initPushback(this));
          getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
        }
        break;
      case POW:
        game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
        publishParameters(UtilBlockSequence.initPushback(this));
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
        break;
      case PUSHBACK:
        game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
        publishParameters(UtilBlockSequence.initPushback(this));
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
        break;
      default:
        break;
    }
    getResult().addReport(new ReportBlockChoice(fNrOfDice, fBlockRoll, fDiceIndex, fBlockResult, game.getDefenderId()));
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_DODGE.addTo(jsonObject, fGotoLabelOnDodge);
    IServerJsonOption.GOTO_LABEL_ON_JUGGERNAUT.addTo(jsonObject, fGotoLabelOnJuggernaut);
    IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.addTo(jsonObject, fGotoLabelOnPushback);
    IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    IServerJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    IServerJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
    IServerJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    return jsonObject;
  }

  @Override
  public StepBlockChoice initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnDodge = IServerJsonOption.GOTO_LABEL_ON_DODGE.getFrom(jsonObject);
    fGotoLabelOnJuggernaut = IServerJsonOption.GOTO_LABEL_ON_JUGGERNAUT.getFrom(jsonObject);
    fGotoLabelOnPushback = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    fNrOfDice = IServerJsonOption.NR_OF_DICE.getFrom(jsonObject);
    fBlockRoll = IServerJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    fDiceIndex = IServerJsonOption.DICE_INDEX.getFrom(jsonObject);
    fBlockResult = (BlockResult) IServerJsonOption.BLOCK_RESULT.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}
