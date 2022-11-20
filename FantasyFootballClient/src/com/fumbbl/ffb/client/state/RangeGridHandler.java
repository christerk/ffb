package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * @author Kalimar
 */
public class RangeGridHandler {

	private final FantasyFootballClient fClient;
	private boolean fShowRangeGrid;
	private final boolean fThrowTeamMate;

	public RangeGridHandler(FantasyFootballClient pClient, boolean pThrowTeamMate) {
		fClient = pClient;
		fThrowTeamMate = pThrowTeamMate;
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void refreshRangeGrid() {
		boolean gridDrawn = false;
		UserInterface userInterface = getClient().getUserInterface();
		if (fShowRangeGrid) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			if ((!fThrowTeamMate && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
				|| (fThrowTeamMate &&
				(actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE
					|| actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE
					|| actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE
					|| actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
			)
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)) {
				FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				if (userInterface.getFieldComponent().getLayerRangeGrid().drawRangeGrid(actingPlayerCoordinate,
					fThrowTeamMate)) {
					userInterface.getFieldComponent().refresh();
				}
				gridDrawn = true;
			}
		}
		if (!gridDrawn && userInterface.getFieldComponent().getLayerRangeGrid().clearRangeGrid()) {
			userInterface.getFieldComponent().refresh();
		}
	}

	public void refreshSettings() {
		String rangeGridSettingProperty = getClient().getProperty(IClientProperty.SETTING_RANGEGRID);
		if (!fShowRangeGrid && IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSettingProperty)) {
			setShowRangeGrid(true);
			refreshRangeGrid();
		}
	}

	public boolean isShowRangeGrid() {
		return fShowRangeGrid;
	}

	public void setShowRangeGrid(boolean pShowRangeGrid) {
		fShowRangeGrid = pShowRangeGrid;
	}

}
