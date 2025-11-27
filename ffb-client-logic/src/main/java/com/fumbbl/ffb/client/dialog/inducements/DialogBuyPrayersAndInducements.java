package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JTabbedPane;
import com.fumbbl.ffb.dialog.DialogBuyPrayersAndInducementsParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

public class DialogBuyPrayersAndInducements extends AbstractBuyInducementsDialog {

	private final Font boldFont;
	private final JLabel labelAvailableTreasury;
	private final JLabel labelAvailablePettyCash;
	private JLabel labelHint;
	private final JPanel hintPanel;
	private final int treasury;
	private final int pettyCash;
	private int availableGold, maximumGold;
	private final boolean superInitialized;

	public DialogBuyPrayersAndInducements(FantasyFootballClient pClient,
																				DialogBuyPrayersAndInducementsParameter pParameter) {

		super(pClient, "Buy Prayers And Inducements", pParameter.getTeamId(), pParameter.getAvailableGold(), false);
		superInitialized = true;
		treasury = pParameter.getTreasury();
		pettyCash = pParameter.getPettyCash();

		FontCache fontCache = pClient.getUserInterface().getFontCache();

		boldFont = fontCache.font(Font.BOLD, 12, dimensionProvider());

		hintPanel = hintPanel();

		labelAvailableTreasury = new JLabel(dimensionProvider());
		labelAvailablePettyCash = new JLabel(dimensionProvider());
		labelAvailableTreasury.setFont(boldFont);
		labelAvailablePettyCash.setFont(boldFont);
		labelAvailableTreasury.setForeground(Color.RED);

		GameOptions gameOptions = pClient.getGame().getOptions();

		maximumGold = availableGold = pParameter.getAvailableGold();
		updateGoldValue();

		JPanel verticalMainPanel = verticalMainPanel(horizontalMainPanel(gameOptions));

		setLayout(new BorderLayout());
		add(verticalMainPanel, BorderLayout.CENTER);
		showDialog();


		setLocationToCenter();
		Point p = getLocation();
		setLocation(p.x, 10);
	}

	private JPanel verticalMainPanel(JTabbedPane horizontalMainPanel) {
		JPanel verticalMainPanel = new JPanel();
		verticalMainPanel.setLayout(new BoxLayout(verticalMainPanel, BoxLayout.Y_AXIS));
		verticalMainPanel.add(goldPanel());
		verticalMainPanel.add(horizontalMainPanel);
		verticalMainPanel.add(hintPanel);
		verticalMainPanel.add(buttonPanel());
		return verticalMainPanel;
	}

	private JTabbedPane horizontalMainPanel(GameOptions gameOptions) {
		JTabbedPane tabbedPane = new JTabbedPane(dimensionProvider());
		int border = dimensionProvider().scale(10);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		Font oldFont = tabbedPane.getFont();
		tabbedPane.setFont(fontCache().font(Font.BOLD, oldFont.getSize(), dimensionProvider()));
		tabbedPane.setForeground(new Color(89, 89, 89));
		tabbedPane.addTab("Inducements", buildLeftPanel(gameOptions, false));
		tabbedPane.addTab("Stars/Mercs", buildRightPanel(gameOptions, true));

		return tabbedPane;
	}

	private JPanel hintPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalStrut(dimensionProvider().scale(5)));
		labelHint = new JLabel(dimensionProvider());
		labelHint.setFont(boldFont);
		labelHint.setForeground(Color.RED);
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.add(labelHint);
		labelPanel.add(Box.createHorizontalGlue());
		panel.add(labelPanel);
		panel.add(Box.createVerticalStrut(dimensionProvider().scale(5)));
		return panel;
	}

	private JPanel goldPanel() {
		JPanel availableGoldPanel = new JPanel();
		availableGoldPanel.setLayout(new BoxLayout(availableGoldPanel, BoxLayout.X_AXIS));
		availableGoldPanel.add(Box.createHorizontalGlue());
		if (pettyCash > 0) {
			availableGoldPanel.add(labelAvailablePettyCash);
			availableGoldPanel.add(Box.createHorizontalGlue());
		}
		availableGoldPanel.add(labelAvailableTreasury);
		availableGoldPanel.add(Box.createHorizontalGlue());
		return availableGoldPanel;
	}

	private void showDialog() {
		super.okButton.setEnabled(true);
		validate();
		pack();
	}


	protected void updateGoldValue() {
		if (superInitialized) {
			int spentTreasury;
			if (pettyCash == 0) {
				labelAvailableTreasury.setText("Treasury: " + StringTool.formatThousands(availableGold) + " gp");
				spentTreasury = maximumGold - availableGold;
			} else {
				int availablePettyCash = Math.max(0, availableGold - treasury);
				int availableTreasury = Math.min(availableGold, treasury);
				labelAvailableTreasury.setText("Treasury: " + StringTool.formatThousands(availableTreasury) + " gp");
				labelAvailablePettyCash.setText("Petty Cash: " + StringTool.formatThousands(availablePettyCash) + " gp");
				spentTreasury = treasury - availableTreasury;
			}
			if (spentTreasury > 0) {
				labelHint.setText("You will spend " + StringTool.formatThousands(spentTreasury) + " gp from your treasury!");
				hintPanel.setVisible(true);
			} else {
				hintPanel.setVisible(false);
			}
			validate();
			pack();
		}
	}

	public DialogId getId() {
		return DialogId.BUY_PRAYERS_AND_INDUCEMENTS;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

	@Override
	public void setMaximumGold(int maximumGold) {
		super.setMaximumGold(maximumGold);
		this.maximumGold = Math.min(this.maximumGold, getMaximumGold());
	}

	@Override
	protected void resetPanels() {
		super.resetPanels();
		availableGold = maximumGold;
	}

	@Override
	public int getAvailableGold() {
		return availableGold;
	}

	@Override
	public void setAvailableGold(int availableGold) {
		if (this.availableGold == availableGold) {
			return;
		}

		this.availableGold = availableGold;
		updateGoldValue();
	}

	@Override
	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}
}
