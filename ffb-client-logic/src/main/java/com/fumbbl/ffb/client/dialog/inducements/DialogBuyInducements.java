package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DialogBuyInducements extends AbstractBuyInducementsDialog {

	private final JLabel fGoldLabelAmount;
	private int availableGold;


	public DialogBuyInducements(FantasyFootballClient client, String teamId, int availableGold) {

		super(client, "Buy Inducements", teamId, availableGold, true);

		JPanel fGoldPanel = new JPanel();
		fGoldPanel.setLayout(new BoxLayout(fGoldPanel, BoxLayout.X_AXIS));

		JLabel goldLabel = new JLabel(dimensionProvider(), "Available Gold:");
		goldLabel.setFont(fontCache().font(Font.BOLD, 12, dimensionProvider()));

		fGoldPanel.add(goldLabel);
		fGoldPanel.add(Box.createHorizontalStrut(10));

		fGoldLabelAmount = new JLabel(dimensionProvider(), StringTool.formatThousands(getAvailableGold()));
		fGoldLabelAmount.setFont(fontCache().font(Font.BOLD, 12, dimensionProvider()));

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
	protected void updateGoldValue() {
		fGoldLabelAmount.setText(StringTool.formatThousands(getAvailableGold()));
	}

	@Override
	public int getAvailableGold() {
		return availableGold;
	}

	@Override
	public void setAvailableGold(int availableGold) {
		this.availableGold = availableGold;
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


	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}
}
