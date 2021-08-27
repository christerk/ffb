package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerInducementUse {

	public static boolean useInducement(GameState pGameState, Team pTeam, InducementType pInducementType, int pNrOfUses) {
		Inducement inducement = null;
		Game game = pGameState.getGame();
		InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet()
				: game.getTurnDataAway().getInducementSet();
		inducement = inducementSet.get(pInducementType);
		if (inducement != null) {
			if ((inducement.getValue() - inducement.getUses()) >= pNrOfUses) {
				inducement.setUses(inducement.getUses() + pNrOfUses);
				inducementSet.addInducement(inducement); // needed to notify the model of the update
			} else {
				inducement = null;
			}
		}
		return (inducement != null);
	}

}
