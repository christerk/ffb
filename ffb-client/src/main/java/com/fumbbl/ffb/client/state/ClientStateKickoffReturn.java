package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.KickoffReturnLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateKickoffReturn extends AbstractClientStateMove<KickoffReturnLogicModule> {

	protected ClientStateKickoffReturn(FantasyFootballClientAwt pClient) {
		super(pClient, new KickoffReturnLogicModule(pClient));
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForPlayer(pPlayer);
				break;
			default:
				break;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case HANDLED:
				getClient().getGame().getFieldModel().clearMoveSquares();
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
		}};
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((actingPlayer.getPlayer() == null) && (playerState != null) && playerState.isAbleToMove()) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if (actingPlayer.getPlayer() == pPlayer) {
			String endMoveActionLabel = game.getActingPlayer().hasActed() ? "End Move" : "Deselect Player";
			JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), endMoveActionLabel,
				createMenuIcon(iconCache, IIconProperty.ACTION_END_MOVE));
			endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
			endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
			menuItemList.add(endMoveAction);
		}
		if (!menuItemList.isEmpty()) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
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
	public void postEndTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

}
