package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.MaximumCarnageLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateMaximumCarnage extends AbstractClientStateBlock<MaximumCarnageLogicModule> {
	public ClientStateMaximumCarnage(FantasyFootballClientAwt pClient) {
		super(pClient, new MaximumCarnageLogicModule(pClient));
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("End Action", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		return itemConfigs;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean actionHandled = true;
		if (pActionKey == ActionKey.PLAYER_ACTION_END_MOVE) {
			menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		} else {
			actionHandled = handleResize(pActionKey);
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
