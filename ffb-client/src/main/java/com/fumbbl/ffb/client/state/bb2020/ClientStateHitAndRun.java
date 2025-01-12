package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.HitAndRunLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientStateHitAndRun extends ClientStateAwt<HitAndRunLogicModule> {
	public ClientStateHitAndRun(FantasyFootballClientAwt client) {
		super(client, new HitAndRunLogicModule(client));
	}

	@Override
	public void clickOnField(FieldCoordinate coordinate) {
		logicModule.fieldInteraction(coordinate);
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
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();
		itemConfigs.put(ClientAction.HIT_AND_RUN, new MenuItemConfig("Cancel Hit And Run", IIconProperty.ACTION_HIT_AND_RUN, IPlayerPopupMenuKeys.KEY_HIT_AND_RUN));
		return itemConfigs;
	}

	@Override
	public boolean mouseOverPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerPeek(player);
		determineCursor(result);
		return true;
	}

	@Override
	public boolean mouseOverField(FieldCoordinate coordinate) {
		InteractionResult result = logicModule.fieldPeek(coordinate);
		determineCursor(result);
		return true;
	}


	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_HIT_AND_RUN;
	}

	@Override
	protected String invalidCursor() {
		return IIconProperty.CURSOR_INVALID_HIT_AND_RUN;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_HIT_AND_RUN, ClientAction.HIT_AND_RUN);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		if (pActionKey == ActionKey.PLAYER_ACTION_HIT_AND_RUN) {
			menuItemSelected(player, IPlayerPopupMenuKeys.KEY_HIT_AND_RUN);
			return true;
		}
		return handleResize(pActionKey);
	}

}
