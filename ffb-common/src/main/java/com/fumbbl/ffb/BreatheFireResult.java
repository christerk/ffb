package com.fumbbl.ffb;

public enum BreatheFireResult {
  FAILURE("You would be knocked down causing a turnover"), NO_EFFECT("There would be no effect"),
  PRONE("Opponent would be place prone without armour roll"), KNOCK_DOWN("");

  private final String message;

  BreatheFireResult(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
