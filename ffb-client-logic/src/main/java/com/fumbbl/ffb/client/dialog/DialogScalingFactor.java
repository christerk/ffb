package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogScalingFactor extends Dialog implements ChangeListener, ActionListener {

	private static final int SLIDER_FACTOR = 20;
	private final JSlider fSlider;
	private final JSlider guiSlider;
	private final JLabel guiLabel;
	private final double initialFactor;
	private final double initialGuiRatio;
	private final Dimension initialContentSize;
	private final Timer previewTimer;
	private boolean previewApplied;
	private Double appliedGuiRatio;
	private double factor;

	private Double appliedFactor;
	private final JLabel fSettingLabel;

	public DialogScalingFactor(FantasyFootballClient pClient) {

		super(pClient, "Scale", true);

		String property = pClient.getProperty(CommonProperty.SETTING_SCALE_FACTOR);
		factor = StringTool.isProvided(property) ? Double.parseDouble(property) : 1.0;
		if (factor < LayoutSettings.MIN_SCALE_FACTOR) {
			factor = LayoutSettings.MIN_SCALE_FACTOR;
		}
		if (factor > LayoutSettings.MAX_SCALE_FACTOR) {
			factor = LayoutSettings.MAX_SCALE_FACTOR;
		}
		initialFactor = dimensionProvider().getLayoutSettings().getScale();
		initialGuiRatio = dimensionProvider().getLayoutSettings().getGuiRatio();
		initialContentSize = new Dimension(pClient.getUserInterface().getDesktop().getSize());
		previewTimer = new Timer(100, event -> preview());
		previewTimer.setRepeats(false);

		fSlider = new JSlider();
		fSlider.setMinimum((int) (LayoutSettings.MIN_SCALE_FACTOR * SLIDER_FACTOR));
		fSlider.setMaximum((int) (LayoutSettings.MAX_SCALE_FACTOR * SLIDER_FACTOR));
		fSlider.setValue((int) (factor * SLIDER_FACTOR));
		fSlider.addChangeListener(this);
		guiSlider = new JSlider((int) (LayoutSettings.MIN_GUI_RATIO * SLIDER_FACTOR),
			(int) (LayoutSettings.MAX_GUI_RATIO * SLIDER_FACTOR));
		guiSlider.setValue((int) Math.round(dimensionProvider().getLayoutSettings().getGuiRatio() * SLIDER_FACTOR));
		guiLabel = new JLabel(dimensionProvider(), "100%");
		guiSlider.addChangeListener(event -> {
			updateSettingLabel();
			queuePreview();
		});

		fSettingLabel = new JLabel(dimensionProvider(), "500%");

		fSettingLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JButton fTestButton = new JButton(dimensionProvider(), "Confirm");
		fTestButton.addActionListener(this);
		JButton cancelButton = new JButton(dimensionProvider(), "Cancel");
		cancelButton.addActionListener(event -> internalFrameClosing(null));

		JPanel settingPanel = new JPanel(new GridBagLayout());
		settingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		settingPanel.add(new JLabel(dimensionProvider(), "Overall scale"), constraints);
		constraints.gridy = 1;
		settingPanel.add(new JLabel(dimensionProvider(), "GUI scale"), constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		settingPanel.add(fSlider, constraints);
		constraints.gridy = 1;
		settingPanel.add(guiSlider, constraints);
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.EAST;
		settingPanel.add(fSettingLabel, constraints);
		constraints.gridy = 1;
		settingPanel.add(guiLabel, constraints);
		constraints.gridy = 2;
		JPanel buttons = new JPanel();
		buttons.add(cancelButton);
		buttons.add(fTestButton);
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		settingPanel.add(buttons, constraints);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(settingPanel);

		pack();

		setLocationToCenter();

		updateSettingLabel();

	}

	public DialogId getId() {
		return DialogId.SCALING_FACTOR;
	}

	public void stateChanged(ChangeEvent pE) {
		factor = ((double)fSlider.getValue())/ SLIDER_FACTOR;
		updateSettingLabel();
		queuePreview();
	}

	private void queuePreview() {
		if (!previewTimer.isRunning()) {
			previewTimer.start();
		}
	}

	private void preview() {
		double ratio = (double) guiSlider.getValue() / SLIDER_FACTOR;
		double resize = factor / initialFactor;
		Dimension contentSize = new Dimension((int) Math.round(initialContentSize.width * resize),
			(int) Math.round(initialContentSize.height * resize));
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.previewScale(factor, ratio, contentSize);
		previewApplied = true;
		Dimension desktop = userInterface.getDesktop().getSize();
		setLocation(Math.max(0, Math.min(getX(), desktop.width - getWidth())),
			Math.max(0, Math.min(getY(), desktop.height - getHeight())));
	}

	private void updateSettingLabel() {
		fSettingLabel.setText((int) (factor * 100) + "%");
		guiLabel.setText((guiSlider.getValue() * 100 / SLIDER_FACTOR) + "%");
		fSlider.repaint();
	}

	public void actionPerformed(ActionEvent pE) {
		previewTimer.stop();
		preview();
		appliedFactor = factor;
		appliedGuiRatio = (double) guiSlider.getValue() / SLIDER_FACTOR;
		internalFrameClosing(null);
	}

	@Override
	public void hideDialog() {
		previewTimer.stop();
		if (appliedFactor == null && previewApplied) {
			getClient().getUserInterface().previewScale(initialFactor, initialGuiRatio, initialContentSize);
			previewApplied = false;
		}
		super.hideDialog();
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public Double getFactor() {
		return appliedFactor;
	}

	public Double getGuiRatio() {
		return appliedGuiRatio;
	}
}
