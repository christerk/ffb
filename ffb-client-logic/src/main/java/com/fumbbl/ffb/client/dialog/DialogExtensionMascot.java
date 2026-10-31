package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.model.Game;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.util.List;

import static java.awt.Component.CENTER_ALIGNMENT;

public class DialogExtensionMascot {

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();

	/**
	 * @return the team re-roll options to offer as buttons, see {@link ReRollSourceComposer#teamReRollSources}
	 */
	public List<ReRollSource> teamReRollSources(HasReRollProperties dialogParameter, boolean includeMascot) {
		return reRollSourceComposer.teamReRollSources(dialogParameter, includeMascot);
	}

	public String label(ReRollSource teamReRollSource, Game game) {
		if (teamReRollSource == ReRollSources.TEAM_RE_ROLL) {
			return "Team Re-Roll";
		}
		if (teamReRollSource == ReRollSources.LEADER) {
			return "Leader Re-Roll";
		}
		return teamReRollSource.getName(game);
	}

	public int mnemonic(ReRollSource teamReRollSource) {
		if (teamReRollSource == ReRollSources.MASCOT) {
			return KeyEvent.VK_K;
		}
		if (teamReRollSource == ReRollSources.BRILLIANT_COACHING) {
			return KeyEvent.VK_G;
		}
		if (teamReRollSource == ReRollSources.PUMP_UP_THE_CROWD) {
			return KeyEvent.VK_D;
		}
		if (teamReRollSource == ReRollSources.SHOW_STAR) {
			return KeyEvent.VK_W;
		}
		if (teamReRollSource == ReRollSources.LEADER) {
			return KeyEvent.VK_L;
		}
		return KeyEvent.VK_T;
	}

	public JButton button(ReRollSource teamReRollSource, Game game, DimensionProvider dimensionProvider) {
		int mnemonic = mnemonic(teamReRollSource);
		JButton button = new JButton(dimensionProvider, label(teamReRollSource, game), mnemonic);
		button.setMnemonic(mnemonic);
		button.setAlignmentY(Box.TOP_ALIGNMENT);
		return button;
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
}
