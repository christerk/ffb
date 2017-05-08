package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill DODGE.
 *
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBlockDodge extends AbstractStep {

  private Boolean fUsingDodge;
  private PlayerState fOldDefenderState;

  public StepBlockDodge(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.BLOCK_DODGE;
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
          if (Skill.DODGE == useSkillCommand.getSkill()) {
            fUsingDodge = useSkillCommand.isSkillUsed();
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
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
          fOldDefenderState = (PlayerState) pParameter.getValue();
          return true;
        default:
          break;
      }
    }
    return false;
  }

  private void executeStep() {
    findDodgeChoice();
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    if (fUsingDodge == null) {
      UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.DODGE, 0), true);
    } else {
      getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.DODGE, fUsingDodge, SkillUse.AVOID_FALLING));
      if (fUsingDodge) {
        game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
      } else {
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
        game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
      }
      publishParameters(UtilBlockSequence.initPushback(this));
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void findDodgeChoice() {

    // ask for dodge only when:
    // 1: The push is a potential chainpush, the three "opposite" squares are
    // occupied.
    // 2: It is the first turn after kickoff and a defending player has the
    // potential to be pushed over the middle-line into the attackers half
    // 3: There is a possibility that you would be pushed next to the sideline.
    // Which is you are standing one square away from sideline and the opponent
    // is pushing from the same row or from the row more infield.

    if (fUsingDodge == null) {

      boolean chainPush = false;
      boolean sidelinePush = false;
      boolean attackerHalfPush = false;
      Game game = getGameState().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();

      Player attacker = actingPlayer.getPlayer();
      FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
      FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
      PushbackSquare startingSquare = UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate, game.isHomePlaying());

      PushbackSquare[] regularPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare, PushbackMode.REGULAR);
      if (ArrayTool.isProvided(regularPushbackSquares)) {
        for (PushbackSquare pushbackSquare : regularPushbackSquares) {
          FieldCoordinate coordinate = pushbackSquare.getCoordinate();
          if (game.getFieldModel().getPlayer(coordinate) != null) {
            chainPush = true;
          }
        }
      }

      PushbackSquare[] grabPushbackSquares = regularPushbackSquares;
      if ((actingPlayer.getPlayerAction() == PlayerAction.BLOCK) && UtilCards.hasSkill(game, attacker, Skill.GRAB)
          && !UtilCards.hasSkill(game, game.getDefender(), Skill.SIDE_STEP)) {
        grabPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare, PushbackMode.GRAB);
      }
      if (ArrayTool.isProvided(regularPushbackSquares)) {
        for (PushbackSquare pushbackSquare : grabPushbackSquares) {
          FieldCoordinate coordinate = pushbackSquare.getCoordinate();
          if (FieldCoordinateBounds.SIDELINE_LOWER.isInBounds(coordinate) || FieldCoordinateBounds.SIDELINE_UPPER.isInBounds(coordinate)
              || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(coordinate) || FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(coordinate)) {
            sidelinePush = true;
          }
          if ((game.getTeamHome().hasPlayer(attacker) && FieldCoordinateBounds.HALF_HOME.isInBounds(coordinate)
              && game.getTurnDataHome().isFirstTurnAfterKickoff())
              || (game.getTeamAway().hasPlayer(attacker) && FieldCoordinateBounds.HALF_AWAY.isInBounds(coordinate)
                  && game.getTurnDataAway().isFirstTurnAfterKickoff())) {
            attackerHalfPush = true;
          }
        }
      }

      if (!chainPush && !sidelinePush && !attackerHalfPush) {
        fUsingDodge = true;
      }

    }

  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.USING_DODGE.addTo(jsonObject, fUsingDodge);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    return jsonObject;
  }

  @Override
  public StepBlockDodge initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingDodge = IServerJsonOption.USING_DODGE.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}
