package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
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

    if (canBeGazed(player)) {
      return InteractionResult.perform();
    }

    return InteractionResult.ignore();
  }

  @Override
  public InteractionResult playerInteraction(Player<?> player) {
    Game game = client.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    if (canBeGazed(player)) {
      client.getCommunication().sendGaze(actingPlayer.getPlayerId(), player);
      return InteractionResult.handled();
    }

    return super.playerInteraction(player);

  }

  private boolean canBeGazed(Player<?> pVictim) {
		boolean result = false;
		if (pVictim != null) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			FieldCoordinate actorCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate victimCoordinate = game.getFieldModel().getPlayerCoordinate(pVictim);
			Team actorTeam = game.getTeamHome().hasPlayer(actingPlayer.getPlayer()) ? game.getTeamHome() : game.getTeamAway();
			Team victimTeam = game.getTeamHome().hasPlayer(pVictim) ? game.getTeamHome() : game.getTeamAway();
			result = (UtilPlayer.canGaze(game, actingPlayer.getPlayer()) && (victimCoordinate != null)
					&& victimCoordinate.isAdjacent(actorCoordinate) && (actorTeam != victimTeam)
					&& (game.getFieldModel().getPlayerState(pVictim).hasTacklezones()));
		}
		return result;
	}
}
