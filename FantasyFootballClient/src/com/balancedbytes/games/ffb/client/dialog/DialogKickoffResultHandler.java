package com.balancedbytes.games.ffb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogKickoffResultParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogKickoffResultHandler extends DialogHandler {

	public DialogKickoffResultHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogKickoffResultParameter dialogKickoffResultParameter = (DialogKickoffResultParameter) game
				.getDialogParameter();

		if (dialogKickoffResultParameter.getKickoffResult() != null) {
			List<String> lines = new ArrayList<>();
			switch (dialogKickoffResultParameter.getKickoffResult()) {
			case PERFECT_DEFENCE:
				if (ClientMode.PLAYER == getClient().getMode()) {
					if (game.isHomePlaying()) {
						lines.add("You may re-arrange your players into any other legal defence.");
					} else {
						lines.add("Your opponent may re-arrange his players into any other legal defence.");
					}
				} else {
					StringBuilder message = new StringBuilder();
					message.append("Coach ");
					message.append(game.isHomePlaying() ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach());
					message.append(" may re-arrange his players into any other legal defence.");
					lines.add(message.toString());
				}
				break;
			case HIGH_KICK:
				if (ClientMode.PLAYER == getClient().getMode()) {
					if (!game.isHomePlaying()) {
						lines.add("You may click on one of your players to move him to the square the ball is kicked to.");
						lines.add("Players standing in a tacklezone may not move.");
					} else {
						lines.add("Your opponent may position a player for the catch.");
					}
				} else {
					StringBuilder message = new StringBuilder();
					message.append("Coach ");
					message.append(game.isHomePlaying() ? game.getTeamAway().getCoach() : game.getTeamHome().getCoach());
					message.append(" may position a player for the catch.");
					lines.add(message.toString());
				}
				break;
			case QUICK_SNAP:
				if (ClientMode.PLAYER == getClient().getMode()) {
					if (!game.isHomePlaying()) {
						lines.add("You may move all of your players a single square.");
						lines.add("Players may enter opponent's half.");
					} else {
						lines.add("Your opponent may move all of his players a single square.");
					}
				} else {
					StringBuilder message = new StringBuilder();
					message.append("Coach ");
					message.append(game.isHomePlaying() ? game.getTeamAway().getCoach() : game.getTeamHome().getCoach());
					message.append(" may move all of his players a single square.");
					lines.add(message.toString());
				}
				break;
			case BLITZ:
				if (ClientMode.PLAYER == getClient().getMode()) {
					if (game.isHomePlaying()) {
						lines.add("You receive an extra turn for moving and blitzing.");
						lines.add("Players standing in a tacklezone may not move.");
					} else {
						lines.add("Your opponent receives an extra turn for moving and blitzing.");
					}
				} else {
					StringBuilder message = new StringBuilder();
					message.append("Coach ");
					message.append(game.isHomePlaying() ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach());
					message.append(" receives an extra turn for moving and blitzing.");
					lines.add(message.toString());
				}
				break;
			case GET_THE_REF:
			case RIOT:
			case CHEERING_FANS:
			case WEATHER_CHANGE:
			case BRILLIANT_COACHING:
			case THROW_A_ROCK:
			case PITCH_INVASION:
				lines.add(dialogKickoffResultParameter.getKickoffResult().getDescription());
				break;
			}
			if (lines.size() > 0) {
				setDialog(new DialogInformation(getClient(), dialogKickoffResultParameter.getKickoffResult().getTitle(),
						lines.toArray(new String[lines.size()]), DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
				getDialog().showDialog(this);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.INFORMATION) && (ClientMode.PLAYER == getClient().getMode())) {
			getClient().getCommunication().sendConfirm();
		}
	}

}
