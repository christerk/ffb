package com.fumbbl.ffb.server.db.insert;

import java.util.ArrayList;
import java.util.List;

import com.fumbbl.ffb.PlayerMarker;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.IDbUpdateParameterList;
import com.fumbbl.ffb.util.StringTool;

/**
 *
 * @author Kalimar
 */
public class DbPlayerMarkersInsertParameterList implements IDbUpdateParameterList {

	private List<DbPlayerMarkersInsertParameter> fParameters;

	public DbPlayerMarkersInsertParameterList() {
		fParameters = new ArrayList<>();
	}

	public DbPlayerMarkersInsertParameter[] getParameters() {
		return fParameters.toArray(new DbPlayerMarkersInsertParameter[fParameters.size()]);
	}

	public void addParameter(DbPlayerMarkersInsertParameter pParameter) {
		fParameters.add(pParameter);
	}

	public void initFrom(GameState pGameState) {
		if (pGameState == null) {
			return;
		}
		Team teamHome = pGameState.getGame().getTeamHome();
		Team teamAway = pGameState.getGame().getTeamAway();
		if ((teamHome == null) || !StringTool.isProvided(teamHome.getId()) || (teamAway == null)
				|| !StringTool.isProvided(teamAway.getId())) {
			return;
		}
		for (PlayerMarker playerMarker : pGameState.getGame().getFieldModel().getPlayerMarkers()) {
			Player<?> player = pGameState.getGame().getPlayerById(playerMarker.getPlayerId());
			if (teamHome.hasPlayer(player) && StringTool.isProvided(playerMarker.getHomeText())) {
				addParameter(new DbPlayerMarkersInsertParameter(teamHome.getId(), player.getId(), playerMarker.getHomeText()));
			}
			if (teamAway.hasPlayer(player) && StringTool.isProvided(playerMarker.getAwayText())) {
				addParameter(new DbPlayerMarkersInsertParameter(teamAway.getId(), player.getId(), playerMarker.getAwayText()));
			}
		}
	}

}
