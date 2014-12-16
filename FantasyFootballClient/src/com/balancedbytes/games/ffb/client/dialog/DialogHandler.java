package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;
import com.balancedbytes.games.ffb.dialog.DialogId;



/**
 * 
 * @author Kalimar
 */
public abstract class DialogHandler implements IDialogCloseListener {
  
  private FantasyFootballClient fClient;
  private IDialog fDialog;
  
  public DialogHandler(FantasyFootballClient pClient) {
    fClient = pClient;
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
  protected void setDialog(IDialog pDialog) {
    fDialog = pDialog;
  }
  
  public IDialog getDialog() {
    return fDialog;
  }
  
  public abstract void showDialog();
  
  public void updateDialog() {
  	// do nothing, overload in subclasses
  }
  
  public void hideDialog() {
    if (getDialog() != null) {
      getDialog().hideDialog();
    }
    ClientData clientData = getClient().getClientData();
    clientData.clearStatus();
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.refreshSideBars();
  }
  
  protected void showStatus(String pTitle, String pMessage, StatusType pType) {
    ClientData clientData = getClient().getClientData();
    clientData.setStatus(pTitle, pMessage, pType);
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.refreshSideBars();
  }
  
  protected boolean testDialogHasId(IDialog pDialog, DialogId pDialogId) {
    return ((pDialog != null) && (pDialog.getId() != null) && (pDialog.getId() == pDialogId));
  }
  
  protected void playSound(SoundId pSound) {
    if (pSound != null) {
      SoundEngine soundEngine = getClient().getUserInterface().getSoundEngine();
      String soundSetting = getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
      if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting) || (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting) && !pSound.isSpectatorSound())) {
        soundEngine.playSound(pSound);
      }
    }
  }
  
  public boolean isEndTurnAllowedWhileDialogVisible() {
  	return true;
  }
    
}
