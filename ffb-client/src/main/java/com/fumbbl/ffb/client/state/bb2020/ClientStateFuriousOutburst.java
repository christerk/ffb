package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.bb2020.FuriousOutburstLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientStateFuriousOutburst extends ClientStateAwt<FuriousOutburstLogicModule> {

	public ClientStateFuriousOutburst(FantasyFootballClientAwt pClient) {
		super(pClient, new FuriousOutburstLogicModule(pClient));
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		logicModule.fieldInteraction(pCoordinate);
	}

	@Override
	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForActingPlayer(result.getActionContext());
				break;
			default:
				break;
		}
	}

	@Override
	public boolean mouseOverPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerPeek(pPlayer);
		determineCursor(result);
		return true;
	}

	@Override
	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		determineCursor(result);
		return super.mouseOverField(pCoordinate);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		if (pActionKey == ActionKey.PLAYER_ACTION_END_MOVE) {
			menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
			return true;
		}
		return handleResize(pActionKey);
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_FURIOUS;
	}

	@Override
	protected String invalidCursor() {
		return IIconProperty.CURSOR_INVALID_FURIOUS;
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = new HashMap<>();
		Map<ClientAction, MenuItemConfig> hasActed = new HashMap<>();
		influences.put(Influences.HAS_ACTED, hasActed);
		hasActed.put(ClientAction.END_MOVE, new MenuItemConfig("End Action", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		return itemConfigs;
	}
}
