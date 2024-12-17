package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.logic.bb2020.KickEmBlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

public class ClientStateKickEmBlitz extends AbstractClientStateBlitz<KickEmBlitzLogicModule> {
	public ClientStateKickEmBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new KickEmBlitzLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLITZ;
	}

	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);

		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			default:
				super.evaluateClickOnPlayer(result, player);
				break;
		}
	}

}
