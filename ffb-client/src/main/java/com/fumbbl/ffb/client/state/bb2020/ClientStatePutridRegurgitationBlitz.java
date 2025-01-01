package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.bb2020.PutridRegurgitationBlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;


public class ClientStatePutridRegurgitationBlitz extends AbstractClientStateBlitz<PutridRegurgitationBlitzLogicModule> {
	public ClientStatePutridRegurgitationBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new PutridRegurgitationBlitzLogicModule(pClient));
	}

	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			default:
				super.evaluateClick(result, player);
				break;
		}
	}
}
