package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPenaltyShootoutParameter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import java.awt.Component;

/**
 * @author Kalimar
 */
public class DialogPenaltyShootout extends Dialog {

	public DialogPenaltyShootout(FantasyFootballClient pClient, DialogPenaltyShootoutParameter parameter) {
		super(pClient, "Penalty Shootout", true);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		getContentPane().add(verticalHeaderPanel());


		JPanel rollPanel = new JPanel();
		rollPanel.setLayout(new BoxLayout(rollPanel, BoxLayout.Y_AXIS));

		int limit = Math.min(Math.min(parameter.getAwayRolls().size(), parameter.getHomeRolls().size()), parameter.getHomeWon().size());

		rollPanel.add(linePanel("HOME", "AWAY"));

		for (int i = 0; i < limit; i++) {
			rollPanel.add(panel(parameter.getHomeRolls().get(i), parameter.getAwayRolls().get(i), parameter.getHomeWon().get(i)));
		}

		rollPanel.add(panel(parameter.getHomeScore(), parameter.getAwayScore(), parameter.homeTeamWins()));

		getContentPane().add(Box.createHorizontalStrut(5));
		getContentPane().add(rollPanel);

		pack();
		setLocationToCenter();
	}

	private JPanel verticalHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		Component glue = Box.createVerticalGlue();
		headerPanel.add(glue);
		JLabel scoreLabel = new JLabel(dimensionProvider(), "Score");
		scoreLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		headerPanel.add(scoreLabel);
		return headerPanel;
	}

	public DialogId getId() {
		return DialogId.PENALTY_SHOOTOUT;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JPanel linePanel(String home, String away) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(dimensionProvider(), home));
		panel.add(new JLabel(dimensionProvider(), away));
		return panel;
	}

	private JPanel panel(int homeRoll, int awayRoll, boolean homeWin) {
		return linePanel(String.valueOf(homeRoll), String.valueOf(awayRoll));
	}

}
