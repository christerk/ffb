package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;

public class ClientStateRaidingParty extends ClientState {
	public ClientStateRaidingParty(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.RAIDING_PARTY;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			getClient().getCommunication().sendFieldCoordinate(pCoordinate);
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_RAID);
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_RAID);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_RAID);
		}

		return true;
	}
}
