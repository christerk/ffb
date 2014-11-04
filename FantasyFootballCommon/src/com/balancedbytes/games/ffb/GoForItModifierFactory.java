package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;


/**
 * 
 * @author Kalimar
 */
public class GoForItModifierFactory implements IRollModifierFactory {
  
  public GoForItModifier forName(String pName) {
    for (GoForItModifier modifier : GoForItModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public GoForItModifier forId(int pId) {
    for (GoForItModifier modifier : GoForItModifier.values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public Set<GoForItModifier> findGoForItModifiers(Game pGame) {
    Set<GoForItModifier> goForItModifiers = new HashSet<GoForItModifier>();
    if (Weather.BLIZZARD == pGame.getFieldModel().getWeather()) {
      goForItModifiers.add(GoForItModifier.BLIZZARD);
    }
    if (UtilCards.isCardActive(pGame, Card.GREASED_SHOES)) {
      goForItModifiers.add(GoForItModifier.GREASED_SHOES);
    }
    return goForItModifiers;
  }
  
  public GoForItModifier[] toArray(Set<GoForItModifier> pGoForItModifierSet) {
    if (pGoForItModifierSet != null) {
      GoForItModifier[] goForItModifierArray = pGoForItModifierSet.toArray(new GoForItModifier[pGoForItModifierSet.size()]);
      Arrays.sort(
        goForItModifierArray,
        new Comparator<GoForItModifier>() {
          public int compare(GoForItModifier pO1, GoForItModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return goForItModifierArray;
    } else {
      return new GoForItModifier[0];
    }
  }
  
}
