package com.balancedbytes.games.ffb.client.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.client.util.UtilClientStateBlocking;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
public class ClientStateBlock extends ClientState {

	protected ClientStateBlock(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.BLOCK;
	}

	public void enterState() {
		super.enterState();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == pPlayer) {
			if (actingPlayer.isSufferingBloodLust()) {
				createAndShowPopupMenuForBlockingPlayer();
			} else if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
				getClient().getCommunication().sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.BLITZ_MOVE,
						actingPlayer.isLeaping());
			} else {
				createAndShowPopupMenuForBlockingPlayer();
//        getClient().getCommunication().sendActingPlayer(null, null, false);
			}
		} else {
			UtilClientStateBlocking.showPopupOrBlockPlayer(this, pPlayer, false);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (UtilPlayer.isBlockable(getClient().getGame(), pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return true;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.isSufferingBloodLust()) {
			boolean actionHandled = true;
			switch (pActionKey) {
			case PLAYER_SELECT:
				createAndShowPopupMenuForBlockingPlayer();
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
				break;
			default:
				actionHandled = false;
				break;
			}
			return actionHandled;
		} else {
			return UtilClientStateBlocking.actionKeyPressed(this, pActionKey, false);
		}
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		getClient().getCommunication().sendEndTurn();
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				getClient().getCommunication().sendActingPlayer(null, null, false);
				break;
			case IPlayerPopupMenuKeys.KEY_MOVE:
				getClient().getCommunication().sendActingPlayer(pPlayer, PlayerAction.MOVE, actingPlayer.isLeaping());
				break;
			default:
				UtilClientStateBlocking.menuItemSelected(this, pPlayer, pMenuKey);
				break;
			}
		}
	}

	private void createAndShowPopupMenuForBlockingPlayer() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItemList = new ArrayList<>();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		if (actingPlayer.isSufferingBloodLust()) {
			JMenuItem moveAction = new JMenuItem("Move",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		String endMoveActionLabel = actingPlayer.hasActed() ? "End Move" : "Deselect Player";
		JMenuItem endMoveAction = new JMenuItem(endMoveActionLabel,
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
		endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
		endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
		menuItemList.add(endMoveAction);
		createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}

}
