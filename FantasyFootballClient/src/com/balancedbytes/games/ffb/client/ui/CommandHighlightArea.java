package com.balancedbytes.games.ffb.client.ui;

/**
 * 
 * @author Kalimar
 */
public class CommandHighlightArea {

  private int fCommandNr;
  private int fStartPosition;
  private int fEndPosition;

  public CommandHighlightArea(int pCommandNr) {
    fCommandNr = pCommandNr;
  }

  public int getCommandNr() {
    return fCommandNr;
  }

  public int getStartPosition() {
    return fStartPosition;
  }

  public void setStartPosition(int pStartPosition) {
    fStartPosition = pStartPosition;
  }

  public int getEndPosition() {
    return fEndPosition;
  }

  public void setEndPosition(int pEndPosition) {
    fEndPosition = pEndPosition;
  }

}


