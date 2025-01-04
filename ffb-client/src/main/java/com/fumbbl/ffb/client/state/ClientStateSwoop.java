package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.SwoopLogicModule;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class ClientStateSwoop extends AbstractClientStateMove<SwoopLogicModule> {

	protected ClientStateSwoop(FantasyFootballClientAwt pClient) {
		super(pClient, new SwoopLogicModule(pClient));
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		logicModule.fieldInteraction(pCoordinate);
	}

	public void clickOnPlayer(Player<?> player) {
		logicModule.playerInteraction(player);
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		UserInterface userInterface = getClient().getUserInterface();
		determineCursor(logicModule.playerPeek(pPlayer));
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		return true;
	}

	@Override
	public void leaveState() {
		// clear marked players
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
	}
}
