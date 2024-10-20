package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;

public class CoordinateBasedSteppingStrategy implements  SteppingStrategy {
  private final FieldCoordinate start;
  private final FieldCoordinate end;

  public CoordinateBasedSteppingStrategy(FieldCoordinate start, FieldCoordinate end) {
    this.start = start;
    this.end = end;
  }

  public double findStepping() {
    if ((start == null) || (end == null)) {
      return 0;
    }
    int deltaX = Math.abs(end.getX() - start.getX());
    int deltaY = Math.abs(end.getY() - start.getY());
    int deltaMax = Math.max(deltaX, deltaY);
    if (deltaMax <= 7) {
      return 2;
    } else {
      return 3;
    }
  }

}
