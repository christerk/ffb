package com.balancedbytes.games.ffb.server.inducements.bb2020;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import static com.balancedbytes.games.ffb.inducement.bb2020.CardHandlerKey.DISTRACT;

@RulesCollection(RulesCollection.Rules.BB2020)
public class DistractHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return DISTRACT;
	}

	@Override
	public boolean activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		Team otherTeam = UtilPlayer.findOtherTeam(game, player);
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		FieldCoordinate[] adjacentCoordinates = game.getFieldModel().findAdjacentCoordinates(playerCoordinate,
			FieldCoordinateBounds.FIELD, 3, false);
		for (FieldCoordinate coordinate : adjacentCoordinates) {
			Player<?> otherPlayer = game.getFieldModel().getPlayer(coordinate);
			if ((otherPlayer != null) && otherTeam.hasPlayer(otherPlayer)) {
				game.getFieldModel().addCardEffect(otherPlayer, CardEffect.DISTRACTED);
			}
		}
		return true;
	}

	@Override
	public void deactivate(Card card, IStep step, Player<?> unused) {
		Game game = step.getGameState().getGame();
		Player<?>[] players = game.getFieldModel().findPlayers(CardEffect.DISTRACTED);
		for (Player<?> player : players) {
			game.getFieldModel().removeCardEffect(player, CardEffect.DISTRACTED);
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (!player.hasSkillProperty(NamedProperties.appliesConfusion) && playerState.isConfused()) {
				game.getFieldModel().setPlayerState(player, playerState.changeConfused(false));
			}
		}
	}
}
