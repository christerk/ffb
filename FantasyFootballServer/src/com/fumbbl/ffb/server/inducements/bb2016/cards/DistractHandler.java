package com.fumbbl.ffb.server.inducements.bb2016.cards;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilPlayer;

import static com.fumbbl.ffb.inducement.bb2016.CardHandlerKey.DISTRACT;

@RulesCollection(RulesCollection.Rules.BB2016)
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
