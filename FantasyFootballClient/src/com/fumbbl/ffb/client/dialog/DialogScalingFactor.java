package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogScalingFactor extends Dialog implements ChangeListener, ActionListener {

	private final JSlider fSlider;
	private double factor;

	private Double appliedFactor;
	private final JLabel fSettingLabel;

	public DialogScalingFactor(FantasyFootballClient pClient) {

		super(pClient, "Scaling Factor", true);

		String property = pClient.getProperty(CommonProperty.SETTING_SCALE_FACTOR);
		factor = StringTool.isProvided(property) ? Double.parseDouble(property) : 1.0;
		if (factor < 0.5) {
			factor = 0.5;
		}
		if (factor > 2.5) {
			factor = 2.5;
		}

		fSlider = new JSlider();
		fSlider.setMinimum(10);
		fSlider.setMaximum(50);
		fSlider.setValue((int) (factor * 20));
		fSlider.addChangeListener(this);
		fSlider.setMinimumSize(fSlider.getPreferredSize());
		fSlider.setMaximumSize(fSlider.getPreferredSize());

		fSettingLabel = new JLabel(dimensionProvider(), "500%");

		fSettingLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JButton fTestButton = new JButton(dimensionProvider(), "Apply");
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
		return DialogId.SCALING_FACTOR;
	}

	public void stateChanged(ChangeEvent pE) {
		factor = ((double)fSlider.getValue())/20;
		updateSettingLabel();
	}

	private void updateSettingLabel() {
		fSettingLabel.setText((int) (factor * 100) + "%");
		fSlider.repaint();
	}

	public void actionPerformed(ActionEvent pE) {
		if (factor != dimensionProvider().getScale()) {
			dimensionProvider().setScale(factor);
			appliedFactor = factor;
			getClient().getUserInterface().getIconCache().clear();
			getClient().getUserInterface().getFontCache().clear();
			getClient().getUserInterface().initComponents(true);
			internalFrameClosing(null);
			new DialogScalingFactor(getClient()).showDialog(getCloseListener());
		}
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public Double getFactor() {
		return appliedFactor;
	}
}
