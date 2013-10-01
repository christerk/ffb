package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementSet;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilInducementUse {
  
  public static boolean useInducement(GameState pGameState, Team pTeam, InducementType pInducementType, int pNrOfUses) {
    Inducement inducement = null;
    Game game = pGameState.getGame();
    InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
    inducement = inducementSet.get(pInducementType);
    if (inducement != null) {
      if ((inducement.getValue() - inducement.getUses()) >= pNrOfUses) {
        inducement.setUses(inducement.getUses() + pNrOfUses);
        inducementSet.addInducement(inducement);  // needed to notify the model of the update
      } else {
        inducement = null;
      }
    }
    return (inducement != null);
  }
  
}
