package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateBlitz extends ClientStateMove {

	protected ClientStateBlitz(FantasyFootballClientAwt pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.BLITZ;
	}

	public void enterState() {
		super.enterState();
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SUPER:
				super.clickOnPlayer(pPlayer);
				break;
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			case PERFORM:
				// TODO this needs to be split and probably integrated into logic module
				UtilClientStateBlocking.showPopupOrBlockPlayer(this, pPlayer, true);
				break;
			default:
				break;
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		switch (logicModule.playerPeek(pPlayer)) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
				break;
			case RESET:
				UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
				break;
			default:
				break;
		}
		return true;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		return UtilClientStateBlocking.actionKeyPressed(this, pActionKey, true) || super.actionKeyPressed(pActionKey);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, ClientAction.FUMBLEROOSKIE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
			put(IPlayerPopupMenuKeys.KEY_STAB, ClientAction.STAB);
			put(IPlayerPopupMenuKeys.KEY_CHAINSAW, ClientAction.CHAINSAW);
			put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, ClientAction.GORED_BY_THE_BULL);
		}};
	}

	@Override
	protected void postPerform() {
		getClient().getUserInterface().getFieldComponent().refresh();
	}
}
