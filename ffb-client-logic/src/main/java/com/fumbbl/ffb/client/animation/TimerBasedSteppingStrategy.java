package com.fumbbl.ffb.client.animation;

import java.awt.*;

public class TimerBasedSteppingStrategy implements  SteppingStrategy {
  private final Dimension start;
  private final Dimension end;

  private final long duration;
  private final long timer;

  public TimerBasedSteppingStrategy(Dimension start, Dimension end, long duration, int timer) {
    this.start = start;
    this.end = end;
    this.duration = duration;
    this.timer = timer;
  }

  public double findStepping() {
    if ((start == null) || (end == null)) {
      return 0;
    }
    int deltaX = Math.abs(end.width - start.width);
    int deltaY = Math.abs(end.height - start.height);
    int deltaMax = Math.max(deltaX, deltaY);

    return (double) deltaMax * (double) timer / (double) duration;
  }

}
