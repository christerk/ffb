package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DialogBuyInducements extends AbstractBuyInducementsDialog {

	private final JLabel fGoldLabelAmount;


	public DialogBuyInducements(FantasyFootballClient client, String teamId, int availableGold) {

		super(client, "Buy Inducements", teamId, availableGold, true);

		JPanel fGoldPanel = new JPanel();
		fGoldPanel.setLayout(new BoxLayout(fGoldPanel, BoxLayout.X_AXIS));

		JLabel goldLabel = new JLabel("Available Gold:");
		goldLabel.setFont(new Font("Sans Serif", Font.BOLD, 12));

		fGoldPanel.add(goldLabel);
		fGoldPanel.add(Box.createHorizontalStrut(10));

		fGoldLabelAmount = new JLabel(StringTool.formatThousands(getAvailableGold()));
		fGoldLabelAmount.setFont(new Font("Sans Serif", Font.BOLD, 12));

		fGoldPanel.add(fGoldLabelAmount);
		fGoldPanel.add(Box.createHorizontalGlue());

		fGoldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(fGoldPanel);
		GameOptions gameOptions = client.getGame().getOptions();
		getContentPane().add(buildInducementPanel(gameOptions));
		getContentPane().add(buttonPanel());

		pack();
		setLocationToCenter();
		Point p = getLocation();
		setLocation(p.x, 10);
	}

	public DialogId getId() {
		return DialogId.BUY_INDUCEMENTS;
	}

	@Override
	protected void setGoldValue(int pValue) {
		fGoldLabelAmount.setText(StringTool.formatThousands(pValue));
	}


	public void actionPerformed(ActionEvent pActionEvent) {
		if ((pActionEvent.getSource() == okButton)) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		} else if (pActionEvent.getSource() == resetButton) {
			resetPanels();
		} else {
			recalculateGold();
		}
	}



	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = false;
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_R:
				resetPanels();
				break;
			case KeyEvent.VK_O:
				keyHandled = true;
				break;
			default:
				keyHandled = false;
				break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}


}
