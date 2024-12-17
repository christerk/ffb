package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
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

	@Override
	public ClientStateId getId() {
		return ClientStateId.PUTRID_REGURGITATION_BLITZ;
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

	@Override
	protected JMenuItem createPutridRegurgitationItem(IconCache iconCache) {
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
		if (PlayerAction.PUTRID_REGURGITATION_MOVE == actingPlayer.getPlayerAction()) {
			JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Putrid Regurgitation",
				createMenuIcon(iconCache, IIconProperty.ACTION_VOMIT));
			menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, 0));
			return menuItem;
		}
		return createMoveMenuItem(iconCache);
	}
}
