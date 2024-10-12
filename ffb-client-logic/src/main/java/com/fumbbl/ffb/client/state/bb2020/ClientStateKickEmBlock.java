package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientStateBlock;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

public class ClientStateKickEmBlock extends ClientStateBlock {
	public ClientStateKickEmBlock(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLOCK;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
			&& game.getFieldModel().getPlayerState(pPlayer).isProneOrStunned()
			&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(pPlayer))) {
			UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, true, false, false);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (UtilPlayer.isKickable(getClient().getGame(), pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}
}
