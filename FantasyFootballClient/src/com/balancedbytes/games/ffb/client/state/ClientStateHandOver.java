package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.util.UtilClientActionKeys;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
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
		FieldCoordinate catcherPosition = UtilClientActionKeys.findMoveCoordinate(getClient(), playerPosition, pActionKey);
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
				JMenuItem handOverAction = new JMenuItem("Hand Over Ball (any player)",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			} else if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
				JMenuItem handOverAction = new JMenuItem("Regular Hand Over / Move",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			}
		}

		if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap)
			&& UtilPlayer.isNextMovePossible(game, true)) {
			if (actingPlayer.isLeaping()) {
				JMenuItem leapAction = new JMenuItem("Don't Leap",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				leapAction.setMnemonic(IPlayerPopupMenuKeys.KEY_LEAP);
				leapAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LEAP, 0));
				menuItemList.add(leapAction);
			} else {
				JMenuItem leapAction = new JMenuItem("Leap",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_LEAP)));
				leapAction.setMnemonic(IPlayerPopupMenuKeys.KEY_LEAP);
				leapAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LEAP, 0));
				menuItemList.add(leapAction);
			}
		}

		String endMoveActionLabel = actingPlayer.hasActed() ? "End Move" : "Deselect Player";
		JMenuItem endMoveAction = new JMenuItem(endMoveActionLabel,
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
		endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
		endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
		menuItemList.add(endMoveAction);

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

}
