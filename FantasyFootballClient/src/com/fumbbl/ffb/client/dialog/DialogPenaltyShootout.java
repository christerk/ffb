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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogPenaltyShootout extends Dialog {

	public DialogPenaltyShootout(FantasyFootballClient pClient, DialogPenaltyShootoutParameter parameter) {
		super(pClient, "Penalty Shootout", true);

		int limit = Math.min(Math.min(parameter.getAwayRolls().size(), parameter.getHomeRolls().size()), parameter.getHomeWon().size());

		getContentPane().setLayout(new GridLayout(limit + 2, 3));

		//getContentPane().add(verticalHeaderPanel());


		linePanel("", "HOME", "AWAY").forEach(getContentPane()::add);

		for (int i = 0; i < limit; i++) {
			panel(parameter.getHomeRolls().get(i), parameter.getAwayRolls().get(i), parameter.getHomeWon().get(i)).forEach(getContentPane()::add);
		}

		panel("Score", parameter.getHomeScore(), parameter.getAwayScore(), parameter.homeTeamWins()).forEach(getContentPane()::add);

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

	private List<JLabel> linePanel(String header, String home, String away) {
		List<JLabel> panel = new ArrayList<>();
		panel.add(new JLabel(dimensionProvider(), header));
		panel.add(new JLabel(dimensionProvider(), home));
		panel.add(new JLabel(dimensionProvider(), away));
		return panel;
	}

	private List<JLabel> panel(int homeRoll, int awayRoll, boolean homeWin) {
		return panel("", homeRoll, awayRoll, homeWin);
	}

	private List<JLabel> panel(String header, int homeRoll, int awayRoll, boolean homeWin) {

		return linePanel(header, String.valueOf(homeRoll), String.valueOf(awayRoll));
	}

}
