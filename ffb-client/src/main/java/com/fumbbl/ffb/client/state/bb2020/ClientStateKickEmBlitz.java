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

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);

		switch (result.getKind()) {
			case SUPER:
				super.clickOnPlayer(pPlayer);
				break;
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			default:
				break;
		}
	}

}
