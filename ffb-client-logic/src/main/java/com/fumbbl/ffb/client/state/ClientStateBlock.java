package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
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
					actingPlayer.isJumping());
			} else {
				createAndShowPopupMenuForBlockingPlayer();
			}
		} else {
			block(pPlayer);
		}
	}

	protected void block(Player<?> pPlayer) {
		UtilClientStateBlocking.showPopupOrBlockPlayer(this, pPlayer, false);
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
				case PLAYER_ACTION_LOOK_INTO_MY_EYES:
					menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
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
		getClient().getCommunication().sendEndTurn(game.getTurnMode());
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
					getClient().getCommunication().sendActingPlayer(pPlayer, PlayerAction.MOVE, actingPlayer.isJumping());
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
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_MOVE), RenderContext.ON_PITCH);
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		addEndActionLabel(iconCache, menuItemList);

		if (isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			menuItemList.add(createLookIntoMyEyesItem(iconCache));
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (isBlackInkAvailable(actingPlayer)) {
			menuItemList.add(createBlackInkItem(iconCache));
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			menuItemList.add(createCatchOfTheDayItem(iconCache));
		}
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			menuItemList.add(createThenIStartedBlastinItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}

}
