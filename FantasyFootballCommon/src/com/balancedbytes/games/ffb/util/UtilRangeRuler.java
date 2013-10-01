package com.balancedbytes.games.ffb.util;

import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilRangeRuler {
  
  public static RangeRuler createRangeRuler(Game pGame, Player pThrower, FieldCoordinate pTargetCoordinate, boolean pThrowTeamMate) {
    RangeRuler rangeRuler = null;
    if ((pGame != null) && (pThrower != null) && (pTargetCoordinate != null)) {
      FieldCoordinate throwerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pThrower);
      PassingDistance passingDistance = UtilPassing.findPassingDistance(pGame, throwerCoordinate, pTargetCoordinate, pThrowTeamMate);
      if (passingDistance != null) {
        int minimumRoll = 0;
        Set<PassModifier> passModifiers = PassModifier.findPassModifiers(pGame, pThrower, passingDistance, pThrowTeamMate);
        if (pThrowTeamMate) {
          minimumRoll = minimumRollThrowTeamMate(pThrower, passingDistance, passModifiers);
        } else {
          minimumRoll = minimumRollPass(pThrower, passingDistance, passModifiers);
        }
        rangeRuler = new RangeRuler(pThrower.getId(), pTargetCoordinate, minimumRoll, pThrowTeamMate);
      }
    }
    return rangeRuler;
  }
  
  public static int minimumRollPass(Player pThrower, PassingDistance pPassingDistance, Set<PassModifier> pPassModifiers) {
    int modifierTotal = 0;
    for (PassModifier passModifier : pPassModifiers) {
      modifierTotal += passModifier.getModifier();
    }
    return Math.max(Math.max(2 - (pPassingDistance.getModifier() - modifierTotal), 2), 7 - Math.min(pThrower.getAgility(), 6) - pPassingDistance.getModifier() + modifierTotal);
  }
  
  public static int minimumRollThrowTeamMate(Player pThrower, PassingDistance pPassingDistance, Set<PassModifier> pPassModifiers) {
    int modifierTotal = 0;
    for (PassModifier passModifier : pPassModifiers) {
      modifierTotal += passModifier.getModifier();
    }
    return Math.max(2, 2 - pPassingDistance.getModifier() + modifierTotal);
  }
  
}
