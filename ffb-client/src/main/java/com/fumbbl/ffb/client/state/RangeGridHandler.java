package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.RangeGridState;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;

public class RangeGridHandler {

	private final FantasyFootballClient fClient;
	private final RangeGridState rangeGridState;
	private final boolean fThrowTeamMate;

	public RangeGridHandler(FantasyFootballClient pClient, boolean pThrowTeamMate) {
		fClient = pClient;
		fThrowTeamMate = pThrowTeamMate;
		rangeGridState = new RangeGridState(pClient, pThrowTeamMate);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void refreshRangeGrid() {
		evaluateRefresh(rangeGridState.refreshRangeGrid());
	}

	public void refreshSettings() {
		evaluateRefresh(rangeGridState.refreshSettings());
	}

	private void evaluateRefresh(InteractionResult result) {
		UserInterface userInterface = getClient().getUserInterface();
		switch (result.getKind()) {
			case PERFORM:
				if (userInterface.getFieldComponent().getLayerRangeGrid().drawRangeGrid(result.getCoordinate(), fThrowTeamMate)) {
					userInterface.getFieldComponent().refresh();
				}
				break;
			case DESELECT:
				if (userInterface.getFieldComponent().getLayerRangeGrid().clearRangeGrid()) {
					userInterface.getFieldComponent().refresh();
				}
				break;
			default:
				break;
		}
	}

	public boolean isShowRangeGrid() {
		return rangeGridState.isShowRangeGrid();
	}

	public void setShowRangeGrid(boolean pShowRangeGrid) {
		rangeGridState.setShowRangeGrid(pShowRangeGrid);
	}

}
