package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IDialogParameterMascot;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.ui.swing.JButton;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class DialogExtensionMascot {

	public ReRollSource teamReRollText(IDialogParameterMascot dialogParameter) {
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
		panel.setAlignmentX(Box.CENTER_ALIGNMENT);
		panel.setAlignmentY(Box.TOP_ALIGNMENT);
		panel.add(button);
		panel.setOpaque(false);
		return panel;
	}
}
