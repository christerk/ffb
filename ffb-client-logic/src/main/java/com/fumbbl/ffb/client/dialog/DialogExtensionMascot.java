package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import static java.awt.Component.CENTER_ALIGNMENT;

public class DialogExtensionMascot {

	public ReRollSource teamReRollText(HasReRollProperties dialogParameter) {
		if (dialogParameter.hasProperty(ReRollProperty.BRILLIANT_COACHING)) {
			return ReRollSources.BRILLIANT_COACHING;
		}

		if (dialogParameter.hasProperty(ReRollProperty.PUMP_UP_THE_CROWD)) {
			return ReRollSources.PUMP_UP_THE_CROWD;
		}

		if (dialogParameter.hasProperty(ReRollProperty.SHOW_STAR)) {
			return ReRollSources.SHOW_STAR;
		}

		if (dialogParameter.hasProperty(ReRollProperty.MASCOT)) {
			return ReRollSources.MASCOT;
		}

		return ReRollSources.TEAM_RE_ROLL;

	}

	public JPanel wrapperPanel(JButton button) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.setAlignmentY(Box.TOP_ALIGNMENT);
		panel.add(button);
		panel.setOpaque(false);
		return panel;
	}

	public JCheckBox checkBox(String text, int mnemonic, Color color, DimensionProvider dimensionProvider,
		ActionListener actionListener, KeyListener keyListener) {
		JCheckBox checkbox = new JCheckBox(dimensionProvider, text + " ( " + (char) mnemonic + " )");
		checkbox.setMnemonic(mnemonic);
		checkbox.addKeyListener(keyListener);
		checkbox.addActionListener(actionListener);
		checkbox.setAlignmentX(CENTER_ALIGNMENT);
		checkbox.setForeground(color);
		checkbox.setOpaque(false);
		checkbox.setFocusPainted(false);
		return checkbox;
	}
}
