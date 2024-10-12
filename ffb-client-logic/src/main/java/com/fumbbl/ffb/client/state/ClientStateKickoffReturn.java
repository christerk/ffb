package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientStateKickoffReturn extends ClientStateMove {

	protected ClientStateKickoffReturn(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.KICKOFF_RETURN;
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

	public void menuItemSelected(Player<?> player, int pMenuKey) {
		if (player != null) {
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_MOVE:
				communication.sendActingPlayer(player, PlayerAction.MOVE, false);
				break;
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				communication.sendActingPlayer(null, null, false);
				break;
			}
		}
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((actingPlayer.getPlayer() == null) && (playerState != null) && playerState.isAbleToMove()) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if (actingPlayer.getPlayer() == pPlayer) {
			String endMoveActionLabel = game.getActingPlayer().hasActed() ? "End Move" : "Deselect Player";
			JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), endMoveActionLabel,
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
			endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
			endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
			menuItemList.add(endMoveAction);
		}
		if (menuItemList.size() > 0) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
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
		default:
			actionHandled = false;
			break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

}
