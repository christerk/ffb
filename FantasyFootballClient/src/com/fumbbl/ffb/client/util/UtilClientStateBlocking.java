package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class UtilClientStateBlocking {

	public static boolean actionKeyPressed(ClientState pClientState, ActionKey pActionKey, boolean pDoBlitz) {
		boolean actionHandled;
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
			case PLAYER_ACTION_CHAINSAW:
				menuItemSelected(pClientState, actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_CHAINSAW);
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
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAB:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, true, false);
					break;
				case IPlayerPopupMenuKeys.KEY_CHAINSAW:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, true);
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
			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesBlockAlternative)) {
				createAndShowBlockOptionsPopupMenu(pClientState, actingPlayer.getPlayer(), pDefender);
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				block(pClientState, actingPlayer.getPlayerId(), pDefender, false, false);
			} else {
				handled = false;
			}
		}
		return handled;
	}

	public static void createAndShowBlockOptionsPopupMenu(ClientState pClientState, Player<?> attacker, Player<?> defender) {
		IconCache iconCache = pClientState.getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (attacker.hasSkillProperty(NamedProperties.canPerformArmourRollInsteadOfBlock)) {
			JMenuItem stabAction = new JMenuItem("Stab Opponent",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAB)));
			stabAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAB);
			stabAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAB, 0));
			menuItemList.add(stabAction);
		}
		if (attacker.hasSkillProperty(NamedProperties.providesChainsawBlockAlternative)) {
			JMenuItem chainsawAction = new JMenuItem("Chainsaw",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_CHAINSAW)));
			chainsawAction.setMnemonic(IPlayerPopupMenuKeys.KEY_CHAINSAW);
			chainsawAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CHAINSAW, 0));
			menuItemList.add(chainsawAction);
		}
		JMenuItem blockAction = new JMenuItem("Block Opponent",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLOCK)));
		blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
		blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
		menuItemList.add(blockAction);
		pClientState.createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		pClientState.showPopupMenuForPlayer(defender);
	}

	private static void block(ClientState pClientState, String pActingPlayerId, Player<?> pDefender, boolean pUsingStab, boolean usingChainsaw) {
		pClientState.getClient().getUserInterface().getFieldComponent().refresh();
		pClientState.getClient().getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab, usingChainsaw);
	}

}
