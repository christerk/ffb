package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Player;

public class ClientStatePlaceBall extends ClientState {

	protected ClientStatePlaceBall(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.PLACE_BALL;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			getClient().getCommunication().sendFieldCoordinate(pCoordinate);
		}
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel.getMoveSquare(pCoordinate) != null) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}

		return super.mouseOverField(pCoordinate);
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return super.mouseOverPlayer(pPlayer);
	}
}
