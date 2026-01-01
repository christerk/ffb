package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.common.ClientStateBlockExtension;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateBlitz<T extends BlitzLogicModule> extends AbstractClientStateMove<T> {

	protected final ClientStateBlockExtension extension = new ClientStateBlockExtension();

	protected AbstractClientStateBlitz(FantasyFootballClientAwt client, T logicModule) {
		super(client, logicModule);
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		InteractionResult result = logicModule.playerPeek(pPlayer);
		determineCursor(result);
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_BLOCK;
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		return extension.actionKeyPressed(this, pActionKey) || super.actionKeyPressed(pActionKey, menuIndex);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, ClientAction.FUMBLEROOSKIE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, ClientAction.GORED_BY_THE_BULL);
			putAll(genericBlockMapping());
		}};
	}

	@Override
	protected void postPerform(int menuKey) {
		getClient().getUserInterface().getFieldComponent().refresh();
	}

}
