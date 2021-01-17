package com.balancedbytes.games.ffb.client.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.client.state.IPlayerPopupMenuKeys;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
public class UtilClientStateBlocking {

	public static boolean actionKeyPressed(ClientState pClientState, ActionKey pActionKey, boolean pDoBlitz) {
		boolean actionHandled = false;
		Game game = pClientState.getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (pActionKey) {
		case PLAYER_ACTION_BLOCK:
			menuItemSelected(pClientState, actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BLOCK);
			actionHandled = true;
			break;
		case PLAYER_ACTION_STAB:
			menuItemSelected(pClientState, actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_STAB);
			actionHandled = true;
			break;
		default:
			FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(pClientState.getClient(), playerPosition,
					pActionKey);
			Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
			actionHandled = showPopupOrBlockPlayer(pClientState, defender, pDoBlitz);
			break;
		}
		return actionHandled;
	}

	public static boolean menuItemSelected(ClientState pClientState, Player<?> pPlayer, int pMenuKey) {
		boolean handled = false;
		if (pPlayer != null) {
			Game game = pClientState.getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_BLOCK:
				handled = true;
				block(pClientState, actingPlayer.getPlayerId(), pPlayer, false);
				break;
			case IPlayerPopupMenuKeys.KEY_STAB:
				handled = true;
				block(pClientState, actingPlayer.getPlayerId(), pPlayer, true);
				break;
			}
		}
		return handled;
	}

	public static boolean showPopupOrBlockPlayer(ClientState pClientState, Player<?> pDefender, boolean pDoBlitz) {
		if (pDefender == null) {
			return false;
		}
		boolean handled = false;
		Game game = pClientState.getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilPlayer.isBlockable(game, pDefender) && (!pDoBlitz || UtilPlayer.isNextMovePossible(game, false))) {
			handled = true;
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pDefender);
			if (actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canPerformArmourRollInsteadOfBlock)) {
				createAndShowStabPopupMenu(pClientState, pDefender);
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				block(pClientState, actingPlayer.getPlayerId(), pDefender, false);
			} else {
				handled = false;
			}
		}
		return handled;
	}

	private static void createAndShowStabPopupMenu(ClientState pClientState, Player<?> pPlayer) {
		IconCache iconCache = pClientState.getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		JMenuItem stabAction = new JMenuItem("Stab Opponent",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAB)));
		stabAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAB);
		stabAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAB, 0));
		menuItemList.add(stabAction);
		JMenuItem blockAction = new JMenuItem("Block Opponent",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLOCK)));
		blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
		blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
		menuItemList.add(blockAction);
		pClientState.createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
		pClientState.showPopupMenuForPlayer(pPlayer);
	}

	private static void block(ClientState pClientState, String pActingPlayerId, Player<?> pDefender, boolean pUsingStab) {
		Game game = pClientState.getClient().getGame();
		game.getFieldModel().clearDiceDecorations();
		pClientState.getClient().getUserInterface().getFieldComponent().refresh();
		pClientState.getClient().getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab);
	}

}
