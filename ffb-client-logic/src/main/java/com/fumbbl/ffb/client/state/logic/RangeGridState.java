package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.UtilPlayer;

public class RangeGridState {

	private final FantasyFootballClient client;

	private boolean showRangeGrid;
	private final boolean throwTeamMate;

	public RangeGridState(FantasyFootballClient client, boolean throwTeamMate) {
		this.client = client;
		this.throwTeamMate = throwTeamMate;
	}

	public boolean isShowRangeGrid() {
		return showRangeGrid;
	}

	public void setShowRangeGrid(boolean showRangeGrid) {
		this.showRangeGrid = showRangeGrid;
	}

	public InteractionResult refreshRangeGrid() {
		if (showRangeGrid) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			if ((!throwTeamMate && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
				|| (throwTeamMate &&
				(actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE
					|| actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE
					|| actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE
					|| actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
			)
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)) {
				FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				return InteractionResult.perform().with(actingPlayerCoordinate);
			}
		}
		return InteractionResult.reset();
	}

	public InteractionResult refreshSettings() {
		String rangeGridSettingProperty = client.getProperty(CommonProperty.SETTING_RANGEGRID);
		if (!showRangeGrid && IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSettingProperty)) {
			setShowRangeGrid(true);
			return refreshRangeGrid();
		}
		return InteractionResult.ignore();
	}

}
