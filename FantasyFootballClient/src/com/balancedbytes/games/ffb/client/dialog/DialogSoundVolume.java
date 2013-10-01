package com.balancedbytes.games.ffb.client.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.util.StringTool;

@SuppressWarnings("serial")
public class DialogSoundVolume extends Dialog implements ChangeListener, ActionListener {
  
  private JSlider fSlider;
  private int fVolume;
  private JLabel fSettingLabel;
  private JButton fTestButton;
  
  public DialogSoundVolume(FantasyFootballClient pClient) {
    
    super(pClient, "Sound Volume Setting", true);
    
    String volumeProperty = pClient.getProperty(IClientProperty.SETTING_SOUND_VOLUME);
    fVolume = StringTool.isProvided(volumeProperty) ? Integer.parseInt(volumeProperty) : 70;

    fSlider = new JSlider();
    fSlider.setMinimum(0);
    fSlider.setMaximum(100);
    fSlider.setValue(fVolume);
    fSlider.addChangeListener(this);
    
    fSettingLabel = new JLabel(Integer.toString(fVolume));
    
    fTestButton = new JButton("Test");
    fTestButton.addActionListener(this);
    
    JPanel settingPanel = new JPanel();
    settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.X_AXIS));
    settingPanel.add(fSlider);
    settingPanel.add(fSettingLabel);
    settingPanel.add(Box.createHorizontalStrut(5));
    settingPanel.add(fTestButton);
    settingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    getContentPane().add(settingPanel);
    
    pack();
    
    setLocationToCenter();    

  }

  public DialogId getId() {
    return DialogId.SOUND_VOLUME;
  }
  
  public void stateChanged(ChangeEvent pE) {
    fVolume = fSlider.getValue();
    fSettingLabel.setText(Integer.toString(fVolume));
  }
  
  public void actionPerformed(ActionEvent pE) {
    SoundEngine soundEngine = getClient().getUserInterface().getSoundEngine();
    soundEngine.setVolume(getVolume());
    soundEngine.playSound(Sound.DING);    
  }
  
  public void internalFrameClosing(InternalFrameEvent pE) {
    if (getCloseListener() != null) {
      getCloseListener().dialogClosed(this);
    }
  }
  
  public int getVolume() {
    return fVolume;
  }

}
