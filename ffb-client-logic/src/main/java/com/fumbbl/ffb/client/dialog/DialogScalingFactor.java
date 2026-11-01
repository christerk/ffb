package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DialogScalingFactor extends Dialog {

	private static final int SLIDER_FACTOR = 20;
	private final JSlider overallSlider;
	private final JSlider guiSlider;
	private final JLabel guiLabel;
	private final JLabel overallLabel;
	private final double initialFactor;
	private final double initialGuiRatio;
	private final Dimension initialContentSize;
	private final Timer previewTimer;
	private Dimension referenceContentSize;
	private Dimension lastPreviewContentSize;
	private double referenceFactor;
	private double lastPreviewFactor;
	private boolean previewApplied;
	private Double appliedGuiRatio;
	private Double appliedFactor;

	public DialogScalingFactor(FantasyFootballClient client) {

		super(client, "Scale", true);

		initialFactor = dimensionProvider().getLayoutSettings().getScale();
		initialGuiRatio = dimensionProvider().getLayoutSettings().getGuiRatio();
		initialContentSize = new Dimension(client.getUserInterface().getDesktop().getSize());
		referenceContentSize = new Dimension(initialContentSize);
		lastPreviewContentSize = new Dimension(initialContentSize);
		referenceFactor = initialFactor;
		lastPreviewFactor = initialFactor;
		previewTimer = new Timer(100, event -> preview());
		previewTimer.setRepeats(false);

		overallSlider = new JSlider((int) (LayoutSettings.MIN_SCALE_FACTOR * SLIDER_FACTOR),
			(int) (LayoutSettings.MAX_SCALE_FACTOR * SLIDER_FACTOR));
		overallSlider.setValue((int) Math.round(initialFactor * SLIDER_FACTOR));
		guiSlider = new JSlider((int) (LayoutSettings.MIN_GUI_RATIO * SLIDER_FACTOR),
			(int) (LayoutSettings.MAX_GUI_RATIO * SLIDER_FACTOR));
		guiSlider.setValue((int) Math.round(dimensionProvider().getLayoutSettings().getGuiRatio() * SLIDER_FACTOR));
		guiLabel = new JLabel(dimensionProvider(), "100%");
		overallLabel = new JLabel(dimensionProvider(), "500%");
		overallLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		overallSlider.addChangeListener(event -> {
			updateSettingLabel();
			queuePreview();
		});
		guiSlider.addChangeListener(event -> {
			updateSettingLabel();
			queuePreview();
		});

		JButton confirmButton = new JButton(dimensionProvider(), "Confirm");
		confirmButton.addActionListener(event -> confirm());
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
		settingPanel.add(overallSlider, constraints);
		constraints.gridy = 1;
		settingPanel.add(guiSlider, constraints);
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.EAST;
		settingPanel.add(overallLabel, constraints);
		constraints.gridy = 1;
		settingPanel.add(guiLabel, constraints);
		constraints.gridy = 2;
		JPanel buttons = new JPanel();
		buttons.add(cancelButton);
		buttons.add(confirmButton);
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

	private void queuePreview() {
		if (!previewTimer.isRunning()) {
			previewTimer.start();
		}
	}

	private void preview() {
		double factor = (double) overallSlider.getValue() / SLIDER_FACTOR;
		double ratio = (double) guiSlider.getValue() / SLIDER_FACTOR;
		UserInterface userInterface = getClient().getUserInterface();
		Dimension currentContentSize = userInterface.getDesktop().getSize();
		if (!currentContentSize.equals(lastPreviewContentSize)) {
			referenceContentSize = new Dimension(currentContentSize);
			referenceFactor = lastPreviewFactor;
		}
		double resize = factor / referenceFactor;
		Dimension contentSize = new Dimension((int) Math.round(referenceContentSize.width * resize),
			(int) Math.round(referenceContentSize.height * resize));
		userInterface.previewScale(factor, ratio, contentSize);
		previewApplied = true;
		Dimension desktop = userInterface.getDesktop().getSize();
		lastPreviewContentSize = new Dimension(desktop);
		lastPreviewFactor = factor;
		setLocation(Math.max(0, Math.min(getX(), desktop.width - getWidth())),
			Math.max(0, Math.min(getY(), desktop.height - getHeight())));
	}

	private void updateSettingLabel() {
		overallLabel.setText((overallSlider.getValue() * 100 / SLIDER_FACTOR) + "%");
		guiLabel.setText((guiSlider.getValue() * 100 / SLIDER_FACTOR) + "%");
	}

	private void confirm() {
		previewTimer.stop();
		preview();
		appliedFactor = (double) overallSlider.getValue() / SLIDER_FACTOR;
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
