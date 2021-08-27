package com.fumbbl.ffb.client.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

public class DialogSoundVolume extends Dialog implements ChangeListener, ActionListener {

	private JSlider fSlider;
	private int fVolume;
	private JLabel fSettingLabel;
	private JButton fTestButton;

	public DialogSoundVolume(FantasyFootballClient pClient) {

		super(pClient, "Sound Volume Setting", true);

		String volumeProperty = pClient.getProperty(IClientProperty.SETTING_SOUND_VOLUME);
		fVolume = StringTool.isProvided(volumeProperty) ? Integer.parseInt(volumeProperty) : 50;
		if (fVolume < 1) {
			fVolume = 1;
		}
		if (fVolume > 100) {
			fVolume = 100;
		}

		fSlider = new JSlider();
		fSlider.setMinimum(1);
		fSlider.setMaximum(100);
		fSlider.setValue(fVolume);
		fSlider.addChangeListener(this);
		fSlider.setMinimumSize(fSlider.getPreferredSize());
		fSlider.setMaximumSize(fSlider.getPreferredSize());

		fSettingLabel = new JLabel("100%");
		fSettingLabel.setMinimumSize(fSettingLabel.getPreferredSize());
		fSettingLabel.setMaximumSize(fSettingLabel.getPreferredSize());
		fSettingLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		fTestButton = new JButton("Test");
		fTestButton.addActionListener(this);

		JPanel settingPanel = new JPanel();
		settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.X_AXIS));
		settingPanel.add(fSlider);
		settingPanel.add(Box.createHorizontalStrut(5));
		settingPanel.add(fSettingLabel);
		settingPanel.add(Box.createHorizontalStrut(5));
		settingPanel.add(fTestButton);
		settingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(settingPanel);

		pack();

		setLocationToCenter();

		updateSettingLabel();

	}

	public DialogId getId() {
		return DialogId.SOUND_VOLUME;
	}

	public void stateChanged(ChangeEvent pE) {
		fVolume = fSlider.getValue();
		updateSettingLabel();
	}

	private void updateSettingLabel() {
		fSettingLabel.setText(fVolume + "%");
	}

	public void actionPerformed(ActionEvent pE) {
		SoundEngine soundEngine = getClient().getUserInterface().getSoundEngine();
		soundEngine.setVolume(getVolume());
		soundEngine.playSound(SoundId.DING);
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
