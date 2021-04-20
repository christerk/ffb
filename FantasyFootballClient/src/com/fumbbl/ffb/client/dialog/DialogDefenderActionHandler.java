package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.BlitzState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class DialogDefenderActionHandler extends DialogHandler {

	private static final Map<PlayerAction, String> sTitleByAction = new HashMap<>();
	private static final Map<PlayerAction, String> sDescriptionByAction = new HashMap<>();

	static {

		sTitleByAction.put(PlayerAction.DUMP_OFF, "Dump Off");
		sDescriptionByAction.put(PlayerAction.DUMP_OFF, "dump off the ball");

	}

	public DialogDefenderActionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		Player<?> defender;
		BlitzState blitzState = game.getFieldModel().getBlitzState();
		if (blitzState == null) {
			defender = game.getDefender();
		} else {
			defender = game.getPlayerById(blitzState.getSelectedPlayerId());
		}

		Team team = game.getTeamHome().hasPlayer(defender) ? game.getTeamHome() : game.getTeamAway();
		if ((ClientMode.PLAYER != getClient().getMode()) || (team != game.getTeamHome())) {
			String message = "Waiting for coach to " +
				sDescriptionByAction.get(game.getDefenderAction()) + ".";
			showStatus(sTitleByAction.get(game.getDefenderAction()), message, StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
