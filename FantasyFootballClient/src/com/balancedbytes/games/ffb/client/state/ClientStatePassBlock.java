package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.dialog.DialogInformation;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.client.util.UtilClientActionKeys;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class ClientStatePassBlock extends ClientStateMove {

	private DialogInformation fInfoDialog;

	protected ClientStatePassBlock(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS_BLOCK;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			createAndShowPopupMenuForPlayer(pPlayer);
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			movePlayer(pCoordinate);
		}
	}

	public void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_JUMP:
				if ((actingPlayer.getPlayer() != null)
						&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap)
						&& UtilPlayer.isNextMovePossible(game, false)) {
					communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_MOVE:
				communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, false);
				break;
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				communication.sendActingPlayer(null, null, false);
				break;
			}
		}
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayer() == null) && (playerState != null) && playerState.isAbleToMove()) {
			JMenuItem moveAction = new JMenuItem("Move Action",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if ((actingPlayer.getPlayer() != null)
				&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap)
				&& UtilPlayer.isNextMovePossible(game, false)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem("Don't Jump",
						new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem("Jump",
						new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			}
		}
		if (game.getActingPlayer().getPlayer() == pPlayer) {
			String endMoveActionLabel = null;
			if (!actingPlayer.hasActed()) {
				endMoveActionLabel = "Deselect Player";
			} else {
				Set<FieldCoordinate> validEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
				if (validEndCoordinates.contains(game.getFieldModel().getPlayerCoordinate(pPlayer))) {
					endMoveActionLabel = "End Move";
				}
			}
			if (endMoveActionLabel != null) {
				JMenuItem endMoveAction = new JMenuItem(endMoveActionLabel,
						new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
				endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
				endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
				menuItemList.add(endMoveAction);
			}
		}
		if (menuItemList.size() > 0) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();
		switch (pActionKey) {
		case PLAYER_SELECT:
			if (selectedPlayer != null) {
				createAndShowPopupMenuForPlayer(selectedPlayer);
			}
			break;
		case PLAYER_CYCLE_RIGHT:
			selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, true);
			if (selectedPlayer != null) {
				hideSelectSquare();
				FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
				showSelectSquare(selectedCoordinate);
				getClient().getClientData().setSelectedPlayer(selectedPlayer);
				userInterface.refreshSideBars();
			}
			break;
		case PLAYER_CYCLE_LEFT:
			selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, false);
			if (selectedPlayer != null) {
				hideSelectSquare();
				FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
				showSelectSquare(selectedCoordinate);
				getClient().getClientData().setSelectedPlayer(selectedPlayer);
				userInterface.refreshSideBars();
			}
			break;
		case PLAYER_ACTION_MOVE:
			menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
			break;
		case PLAYER_ACTION_JUMP:
			menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_JUMP);
			break;
		default:
			actionHandled = false;
			break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() != null) {
			Set<FieldCoordinate> validEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			if (!validEndCoordinates.contains(playerCoordinate)) {
				fInfoDialog = new DialogInformation(getClient(), "End Turn not possible",
						new String[] { "You cannot end the turn before the acting player has reached a valid destination!" },
						DialogInformation.OK_DIALOG, IIconProperty.GAME_REF);
				fInfoDialog.showDialog(new IDialogCloseListener() {
					public void dialogClosed(IDialog pDialog) {
						fInfoDialog.hideDialog();
					}
				});
				return;
			}
		}
		getClient().getCommunication().sendEndTurn();
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

}
