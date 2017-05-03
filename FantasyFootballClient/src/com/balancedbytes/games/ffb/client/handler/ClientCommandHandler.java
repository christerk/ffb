package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;
import com.balancedbytes.games.ffb.client.ui.GameTitleUpdateTask;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public abstract class ClientCommandHandler {
  
  private FantasyFootballClient fClient;
  
  protected ClientCommandHandler(FantasyFootballClient pClient) {
    fClient = pClient;
  }

  public abstract NetCommandId getId();
  
  public abstract boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode);
  
  public FantasyFootballClient getClient() {
    return fClient;
  }

  protected void playSound(SoundId pSoundId, ClientCommandHandlerMode pMode, boolean pWait) {
    if (pSoundId != null) {
    	// System.out.println("play " + pSound.getName());
      SoundEngine soundEngine = getClient().getUserInterface().getSoundEngine();
      String soundSetting = getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
      if ((pMode == ClientCommandHandlerMode.PLAYING) || ((pMode == ClientCommandHandlerMode.REPLAYING) && getClient().getReplayer().isReplayingSingleSpeedForward())) {
        if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting) || (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting) && !pSoundId.isSpectatorSound())) {
          soundEngine.playSound(pSoundId);
        }
        if (pWait && (pMode == ClientCommandHandlerMode.PLAYING)) {
          long soundLength = soundEngine.getSoundLength(pSoundId);
          if (soundLength > 0) {
            synchronized (this) {
              try {
                Thread.sleep(soundLength);
              } catch (InterruptedException ie) {
                // just continue
              }
            }
          }
        }
      }
    }
  }
    
  protected void refreshFieldComponent() {
    getClient().getUserInterface().invokeAndWait(
      new Runnable() {
        public void run() {
          getClient().getUserInterface().getFieldComponent().refresh();
        }
      }
    );
  }
  
  protected void refreshSideBars() {
    getClient().getUserInterface().invokeAndWait(
      new Runnable() {
        public void run() {
          getClient().getUserInterface().refreshSideBars();
        }
      }
    );
  }
  
  protected void refreshGameMenuBar() {
    getClient().getUserInterface().invokeAndWait(
      new Runnable() {
        public void run() {
          getClient().getUserInterface().getGameMenuBar().refresh();
        }
      }
    );
  }
  
  protected void updateDialog() {
    getClient().getUserInterface().invokeAndWait(
      new Runnable() {
        public void run() {
          getClient().getUserInterface().getDialogManager().updateDialog();
        }
      }
    );
  }
  
  protected void updateGameTitle(GameTitle pGameTitle) {
    getClient().getUserInterface().invokeLater(
      new GameTitleUpdateTask(getClient(), pGameTitle)
    );
  }
  
}
