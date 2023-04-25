package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStateHandOver extends ClientStateMove {

	protected ClientStateHandOver(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.HAND_OVER;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			handOver(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		Player<?> catcher = game.getFieldModel().getPlayer(catcherPosition);
		if (catcher != null) {
			return handOver(catcher);
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (canPlayerGetHandOver(pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
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

	public boolean canPlayerGetHandOver(Player<?> pCatcher) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			FieldModel fieldModel = game.getFieldModel();
			FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate catcherCoordinate = fieldModel.getPlayerCoordinate(pCatcher);
			PlayerState catcherState = fieldModel.getPlayerState(pCatcher);
			return (throwerCoordinate.isAdjacent(catcherCoordinate) && (catcherState != null)
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace()))
				&& (catcherState.hasTacklezones()
				&& (game.getTeamHome() == pCatcher.getTeam() || actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER)));
		}
		return false;
	}

	private boolean handOver(Player<?> pCatcher) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && canPlayerGetHandOver(pCatcher)) {
			getClient().getCommunication().sendHandOver(actingPlayer.getPlayerId(), pCatcher);
			return true;
		}
		return false;
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			if ((PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction())) {
				JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Hand Over Ball (any player)",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			} else if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
				JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Regular Hand Over / Move",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			}
		}

		if (isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			}
		}

		addEndActionLabel(iconCache, menuItemList, actingPlayer);

		if (isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

}
