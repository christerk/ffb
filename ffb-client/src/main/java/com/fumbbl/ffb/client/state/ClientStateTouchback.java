package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.TouchbackLogicModule;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ClientStateTouchback extends ClientStateAwt<TouchbackLogicModule> {

	protected ClientStateTouchback(FantasyFootballClientAwt pClient) {
		super(pClient, new TouchbackLogicModule(pClient));
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (isClickable()) {
			determineCursor(logicModule.playerPeek(pPlayer));
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		if (isClickable()) {
			determineCursor(logicModule.fieldPeek(pCoordinate));
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
		if (isClickable()) {
			logicModule.playerInteraction(pPlayer);
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (isClickable()) {
			logicModule.fieldInteraction(pCoordinate);
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}
}
