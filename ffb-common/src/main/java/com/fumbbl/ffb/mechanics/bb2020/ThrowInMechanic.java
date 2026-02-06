package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ThrowInMechanic extends com.fumbbl.ffb.mechanics.ThrowInMechanic {
  
  @Override
  public int distance(int[] distanceRoll) {
    return distanceRoll[0] + distanceRoll[1] + 1;
  }

  @Override
  public boolean isCornerThrowIn(FieldCoordinate pStartCoordinate) {
    return false;
  }

  @Override
  public Direction interpretThrowInDirectionRoll(FieldCoordinate pStartCoordinate, int roll,
    boolean isCornerThrowIn) {
    // Endzone Home Team
    if (pStartCoordinate.getX() < 1) {
      return interpretThrowInDirectionRoll(Direction.EAST, roll);
    }
    // Endzone Away Team
    if (pStartCoordinate.getX() > 24) {
      return interpretThrowInDirectionRoll(Direction.WEST, roll);
    }
    // Lower Sideline
    if (pStartCoordinate.getY() > 13) {
      return interpretThrowInDirectionRoll(Direction.NORTH, roll);
    }
    // Upper Sideline
    if (pStartCoordinate.getY() < 1) {
      return interpretThrowInDirectionRoll(Direction.SOUTH, roll);
    }

    throw new IllegalStateException("Unable to determine throwInDirection.");
  }
  
}
