package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

public class GazeMoveLogicModule extends MoveLogicModule {
  public GazeMoveLogicModule(FantasyFootballClient client) {
    super(client);
  }

  @Override
  public ClientStateId getId() {
    return ClientStateId.GAZE_MOVE;
  }

  @Override
  public InteractionResult playerPeek(Player<?> player) {
    Game game = client.getGame();

    if (UtilPlayer.isAdjacentGazeTarget(game, player)) {
      return InteractionResult.perform();
    }

    return InteractionResult.ignore();
  }

  @Override
  public InteractionResult playerInteraction(Player<?> player) {
    Game game = client.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    if (UtilPlayer.isAdjacentGazeTarget(game, player)) {
      client.getCommunication().sendGaze(actingPlayer.getPlayerId(), player);
      return InteractionResult.handled();
    }

    return super.playerInteraction(player);

  }
}
