package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.net.commands.ClientCommandGaze;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerConstant;
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
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBlock;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the select sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END. Needs to be
 * initialized with stepParameter UPDATE_PERSISTENCE.
 *
 * Sets stepParameter BLOCK_DEFENDER_ID for all steps on the stack. Sets
 * stepParameter DISPATCH_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_PLAYER_ACTION for all steps on the stack. Sets
 * stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * FOUL_DEFENDER_ID for all steps on the stack. Sets stepParameter
 * GAZE_VICTIM_ID for all steps on the stack. Sets stepParameter HAIL_MARY_PASS
 * for all steps on the stack. Sets stepParameter MOVE_STACK for all steps on
 * the stack. Sets stepParameter TARGET_COORDINATE for all steps on the stack.
 * Sets stepParameter USING_STAB for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepInitSelecting extends AbstractStep {

  private String fGotoLabelOnEnd;
  private PlayerAction fDispatchPlayerAction;
  private boolean fEndTurn;
  private boolean fEndPlayerAction;

  private transient boolean fUpdatePersistence;

  public StepInitSelecting(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.INIT_SELECTING;
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
          // mandatory
          case UPDATE_PERSISTENCE:
            fUpdatePersistence = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)
        && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
      Game game = getGameState().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      boolean homeCommand = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand);
      switch (pReceivedCommand.getId()) {
        case CLIENT_ACTING_PLAYER:
          ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
          Player selectedPlayer = game.getPlayerById(actingPlayerCommand.getPlayerId());
          if (StringTool.isProvided(actingPlayerCommand.getPlayerId()) && game.getActingTeam() == selectedPlayer.getTeam()) {
            UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(), actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isLeaping());
          } else {
            fEndPlayerAction = true;
          }
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_MOVE:
          ClientCommandMove moveCommand = (ClientCommandMove) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), moveCommand)
              && UtilServerPlayerMove.isValidMove(getGameState(), moveCommand, homeCommand)) {
            publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, UtilServerPlayerMove.fetchMoveStack(getGameState(), moveCommand, homeCommand)));
            fDispatchPlayerAction = PlayerAction.MOVE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_FOUL:
          ClientCommandFoul foulCommand = (ClientCommandFoul) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), foulCommand) && !game.getTurnData().isFoulUsed()) {
            publishParameter(new StepParameter(StepParameterKey.FOUL_DEFENDER_ID, foulCommand.getDefenderId()));
            UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.FOUL, false);
            fDispatchPlayerAction = PlayerAction.FOUL;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_BLOCK:
          ClientCommandBlock blockCommand = (ClientCommandBlock) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blockCommand)) {
            publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockCommand.getDefenderId()));
            publishParameter(new StepParameter(StepParameterKey.USING_STAB, blockCommand.isUsingStab()));
            fDispatchPlayerAction = PlayerAction.BLOCK;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_GAZE:
          ClientCommandGaze gazeCommand = (ClientCommandGaze) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), gazeCommand)) {
            publishParameter(new StepParameter(StepParameterKey.GAZE_VICTIM_ID, gazeCommand.getVictimId()));
            UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.GAZE, false);
            fDispatchPlayerAction = PlayerAction.GAZE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_PASS:
          ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
          boolean passAllowed = !game.getTurnData().isPassUsed() || ((actingPlayer.getPlayer() != null)
              && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB)));
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand) && passAllowed) {
            if (passCommand.getTargetCoordinate() != null) {
              if (game.isHomePlaying()) {
                publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate()));
              } else {
                publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate().transform()));
              }
            }
            if ((actingPlayer.getPlayer() != null) && ((actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS)
                || (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
              fDispatchPlayerAction = actingPlayer.getPlayerAction();
            } else {
              UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.PASS, false);
              fDispatchPlayerAction = PlayerAction.PASS;
            }
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_HAND_OVER:
          ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand) && !game.getTurnData().isHandOverUsed()) {
            Player catcher = game.getPlayerById(handOverCommand.getCatcherId());
            FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
            publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, catcherCoordinate));
            UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, false);
            fDispatchPlayerAction = PlayerAction.HAND_OVER;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_THROW_TEAM_MATE:
          ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand) && !game.getTurnData().isPassUsed()) {
            if (throwTeamMateCommand.getTargetCoordinate() != null) {
              if (game.isHomePlaying()) {
                publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate()));
              } else {
                publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate().transform()));
              }
            }
            publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, throwTeamMateCommand.getThrownPlayerId()));
            UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE, false);
            fDispatchPlayerAction = PlayerAction.THROW_TEAM_MATE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_END_TURN:
          if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
            fEndTurn = true;
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
  public void start() {
    if (fUpdatePersistence) {
      fUpdatePersistence = false;
      GameCache gameCache = getGameState().getServer().getGameCache();
      gameCache.queueDbUpdate(getGameState(), true);
    }
  }

  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (game.isTimeoutEnforced() || fEndTurn) {
      publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fEndPlayerAction) {
      publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fDispatchPlayerAction != null) {
      if (StringTool.isProvided(actingPlayer.getPlayerId()) && (actingPlayer.getPlayerAction() != null)) {
        publishParameter(new StepParameter(StepParameterKey.DISPATCH_PLAYER_ACTION, fDispatchPlayerAction));
        if (actingPlayer.isStandingUp()) {
          prepareStandingUp();
          getResult().setNextAction(StepAction.NEXT_STEP);
        } else {
          getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
        }
      }
    } else {
      prepareStandingUp();
      if ((actingPlayer.getPlayerAction() == PlayerAction.REMOVE_CONFUSION) || (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP)
          || (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ)) {
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }
  }

  private void prepareStandingUp() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if ((actingPlayer.getPlayer() != null) && (actingPlayer.getPlayerAction() != null)) {
      if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) || (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
          || (actingPlayer.getPlayerAction() == PlayerAction.BLOCK) || (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)) {
        UtilBlock.updateDiceDecorations(game);
      }
      if (actingPlayer.getPlayerAction().isMoving()) {
        if (actingPlayer.isStandingUp() && !UtilCards.hasSkill(game, actingPlayer, Skill.JUMP_UP)) {
          actingPlayer.setCurrentMove(Math.min(IServerConstant.MINIMUM_MOVE_TO_STAND_UP, UtilCards.getPlayerMovement(game, actingPlayer.getPlayer())));
          actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
                                                                             // go-for-it
        }
        UtilServerPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
      }
    }
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.DISPATCH_PLAYER_ACTION.addTo(jsonObject, fDispatchPlayerAction);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    return jsonObject;
  }

  @Override
  public StepInitSelecting initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    return this;
  }

}
