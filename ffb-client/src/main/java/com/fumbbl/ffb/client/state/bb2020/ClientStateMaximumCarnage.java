package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.MaximumCarnageLogicModule;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientStateMaximumCarnage extends AbstractClientStateBlock<MaximumCarnageLogicModule> {
	public ClientStateMaximumCarnage(FantasyFootballClientAwt pClient) {
		super(pClient, new MaximumCarnageLogicModule(pClient));
	}

	@Override
	protected void createAndShowPopupMenuForBlockingPlayer() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItemList = new ArrayList<>();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		addEndActionLabel(iconCache, menuItemList);
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean actionHandled = true;
		if (pActionKey == ActionKey.PLAYER_ACTION_END_MOVE) {
			menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		} else {
			actionHandled = false;
		}
		return actionHandled;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		Map<Integer, ClientAction> actions = new HashMap<>();
		actions.put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
		return actions;
	}
}
