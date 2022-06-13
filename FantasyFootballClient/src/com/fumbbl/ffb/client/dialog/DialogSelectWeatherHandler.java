package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSelectWeatherParameter;
import com.fumbbl.ffb.model.Game;

public class DialogSelectWeatherHandler extends DialogHandler {

	public DialogSelectWeatherHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogSelectWeatherParameter parameter = (DialogSelectWeatherParameter) game.getDialogParameter();
		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogSelectWeather(getClient(), parameter.getOptions()));
			getDialog().showDialog(this);

		} else {
			showStatus("Select Weather", "Waiting for coach to select new weather.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if ((pDialog != null) && (pDialog.getId() == DialogId.SELECT_WEATHER)) {
			DialogSelectWeather dialog = ((DialogSelectWeather) pDialog);
			getClient().getCommunication().sendSelectedWeather(dialog.getModifier(), dialog.getWeatherName());
		}
	}

}
