package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientStateBlitz;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


public class ClientStatePutridRegurgitationBlitz extends ClientStateBlitz {
	public ClientStatePutridRegurgitationBlitz(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.PUTRID_REGURGITATION_BLITZ;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				createAndShowPopupMenuForActingPlayer();
			} else {
				if (PlayerAction.PUTRID_REGURGITATION_BLITZ == actingPlayer.getPlayerAction()
					&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)) {
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, false, true, false);
				}
			}
		}
	}

	@Override
	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			ClientCommunication communication = getClient().getCommunication();
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			if (isPutridRegurgitationAvailable()) {
				switch (pMenuKey) {
					case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
						communication.sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.PUTRID_REGURGITATION_BLITZ, actingPlayer.isJumping());
						break;
					case IPlayerPopupMenuKeys.KEY_MOVE:
						communication.sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.PUTRID_REGURGITATION_MOVE, actingPlayer.isJumping());
						break;
					default:
						super.menuItemSelected(pPlayer, pMenuKey);
						break;
				}
			} else {
				super.menuItemSelected(pPlayer, pMenuKey);
			}
		}
	}

	@Override
	protected boolean isPutridRegurgitationAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return actingPlayer.hasBlocked() && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(game.getActingTeam()), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
	}

	@Override
	protected JMenuItem createPutridRegurgitationItem(IconCache iconCache) {
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
		if (PlayerAction.PUTRID_REGURGITATION_MOVE == actingPlayer.getPlayerAction()) {
			JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Putrid Regurgitation",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_VOMIT)));
			menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, 0));
			return menuItem;
		}
		return createMoveMenuItem(iconCache);
	}
}
