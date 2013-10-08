package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public class UtilDisturbingPresence {

  public static int findOpposingDisturbingPresences(Game pGame, Player pPlayer) {
    int foulAppearances = 0;
    FieldModel fieldModel = pGame.getFieldModel();
    FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
    Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
    for (Player opposingPlayer : otherTeam.getPlayers()) {
      FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(opposingPlayer);
      if (UtilCards.hasSkill(pGame, opposingPlayer, Skill.DISTURBING_PRESENCE) && FieldCoordinateBounds.FIELD.isInBounds(coordinate) && (playerCoordinate.distanceInSteps(coordinate) <= 3)) {
        // System.out.println(opposingPlayer.getName() + ": " + playerCoordinate.distanceInSteps(coordinate));
        foulAppearances++;
      }
    }
    return foulAppearances;
  }
  
}
