package com.balancedbytes.games.ffb.server.util;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilCatchScatterThrowIn {
  
  public static Player[] findDivingCatchers(GameState pGameState, Team pTeam, FieldCoordinate pCoordinate) {
	  Set<Player> divingCatchPlayers = new HashSet<Player>(); 
    Game game = pGameState.getGame();
    Player[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(game, pTeam, pCoordinate, false);
    for (Player player : adjacentPlayers) {
      if (UtilCards.hasSkill(game, player, Skill.DIVING_CATCH)) {
      	divingCatchPlayers.add(player);
      }
    }
    Player[] playerArray = divingCatchPlayers.toArray(new Player[divingCatchPlayers.size()]);
    UtilPlayer.sortByPlayerNr(playerArray);
    return playerArray;
  }

	public static FieldCoordinate findScatterCoordinate(FieldCoordinate pStartCoordinate, Direction pScatterDirection, int pScatterDistance) {
	  if (pStartCoordinate == null) {
	    throw new IllegalArgumentException("Parameter startCoordinate must not be null.");
	  }
	  if (pScatterDirection == null) {
	    throw new IllegalArgumentException("Parameter scatterDirection must not be null.");
	  }
	  switch (pScatterDirection) {
	    case NORTH:
	      return pStartCoordinate.add(0, -pScatterDistance);
	    case NORTHEAST:
	      return pStartCoordinate.add(pScatterDistance, -pScatterDistance);
	    case EAST:
	      return pStartCoordinate.add(pScatterDistance, 0);
	    case SOUTHEAST:
	      return pStartCoordinate.add(pScatterDistance, pScatterDistance);
	    case SOUTH:
	      return pStartCoordinate.add(0, pScatterDistance);
	    case SOUTHWEST:
	      return pStartCoordinate.add(-pScatterDistance, pScatterDistance);
	    case WEST:
	      return pStartCoordinate.add(-pScatterDistance, 0);
	    case NORTHWEST:
	      return pStartCoordinate.add(-pScatterDistance, -pScatterDistance);
	    default:
	      throw new IllegalStateException("Unable to determine scatterCoordinate.");
	  }
	}

}
