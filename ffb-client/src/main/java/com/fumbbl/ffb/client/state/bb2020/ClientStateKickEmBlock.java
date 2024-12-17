package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.logic.bb2020.KickEmBlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

public class ClientStateKickEmBlock extends AbstractClientStateBlock<KickEmBlockLogicModule> {
	public ClientStateKickEmBlock(FantasyFootballClientAwt pClient) {
		super(pClient, new KickEmBlockLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLOCK;
	}

	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);

		switch (result.getKind()) {
			default:
				super.evaluateClickOnPlayer(result, player);
				break;
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
