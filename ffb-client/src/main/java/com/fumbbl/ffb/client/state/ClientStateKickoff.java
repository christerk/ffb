package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.KickoffLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateKickoff extends ClientStateAwt<KickoffLogicModule> {

	protected ClientStateKickoff(FantasyFootballClientAwt pClient) {
		super(pClient, new KickoffLogicModule(pClient));
	}

	public ClientStateId getId() {
		return logicModule.getId();
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case HANDLED:
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case HANDLED:
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}


	@Override
	public void postEndTurn() {
		if (logicModule.turnIsEnding()) {
			SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
			sideBarHome.refresh();
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return true;
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
