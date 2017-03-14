package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle skill BOMBARDIER.
 * 
 * @author Kalimar
 */
public final class StepBombardier extends AbstractStep {

  public StepBombardier(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.BOMBARDIER;
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
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (!game.getTurnMode().isBombTurn()
      && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
      // mark skill used to set active=false when changing players
      actingPlayer.markSkillUsed(Skill.BOMBARDIER); 
      if (game.getTeamHome().hasPlayer(actingPlayer.getPlayer())) {
        if (TurnMode.BLITZ == game.getTurnMode()) {
          game.setTurnMode(TurnMode.BOMB_HOME_BLITZ);
        } else {
          game.setTurnMode(TurnMode.BOMB_HOME);
        }
      } else {
        if (TurnMode.BLITZ == game.getTurnMode()) {
          game.setTurnMode(TurnMode.BOMB_AWAY_BLITZ);
        } else {
          game.setTurnMode(TurnMode.BOMB_AWAY);
        }
      }
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    return super.toJsonValue();
  }

  @Override
  public StepBombardier initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }

}
