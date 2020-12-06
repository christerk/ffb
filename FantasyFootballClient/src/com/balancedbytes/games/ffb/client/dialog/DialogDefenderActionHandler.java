package com.balancedbytes.games.ffb.client.dialog;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public class DialogDefenderActionHandler extends DialogHandler {

	private static final Map<PlayerAction, String> sTitleByAction = new HashMap<PlayerAction, String>();
	private static final Map<PlayerAction, String> sDescriptionByAction = new HashMap<PlayerAction, String>();

	static {

		sTitleByAction.put(PlayerAction.DUMP_OFF, "Dump Off");
		sDescriptionByAction.put(PlayerAction.DUMP_OFF, "dump off the ball");

	}

	public DialogDefenderActionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		Team team = game.getTeamHome().hasPlayer(game.getDefender()) ? game.getTeamHome() : game.getTeamAway();
		if ((ClientMode.PLAYER != getClient().getMode()) || (team != game.getTeamHome())) {
			StringBuilder message = new StringBuilder();
			message.append("Waiting for coach to ");
			message.append(sDescriptionByAction.get(game.getDefenderAction())).append(".");
			showStatus(sTitleByAction.get(game.getDefenderAction()), message.toString(), StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
