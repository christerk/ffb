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
	private final Font regularFont;
	private final JLabel labelAvailableGold;
	private int availableGold, maximumGold;
	private final boolean superInitialized;
	private final DialogBuyPrayersAndInducementsParameter parameter;

	public DialogBuyPrayersAndInducements(FantasyFootballClient pClient, DialogBuyPrayersAndInducementsParameter pParameter) {

		super(pClient, "Buy Prayers And Inducements", pParameter.getTeamId(), pParameter.getAvailableGold(), false);
		this.parameter = pParameter;
		superInitialized = true;

		labelAvailableGold = new JLabel(dimensionProvider());

		FontCache fontCache = pClient.getUserInterface().getFontCache();

		boldFont = fontCache.font(Font.BOLD, 12, dimensionProvider());
		regularFont = fontCache.font(Font.PLAIN, 11, dimensionProvider());

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


	private JLabel label(String text, Font font) {
		JLabel label = new JLabel(dimensionProvider());
		label.setText(text);
		label.setFont(font);
		return label;
	}


	private JPanel verticalMainPanel(JTabbedPane horizontalMainPanel) {
		JPanel verticalMainPanel = new JPanel();
		verticalMainPanel.setLayout(new BoxLayout(verticalMainPanel, BoxLayout.Y_AXIS));
		verticalMainPanel.add(goldPanel());
		verticalMainPanel.add(horizontalMainPanel);
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
		tabbedPane.addTab("Stars/Mercs", buildRightPanel(gameOptions));

		return tabbedPane;
	}

	private JPanel goldPanel() {
		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));

		labelAvailableGold.setFont(boldFont);

		panelGold.add(Box.createHorizontalGlue());
		panelGold.add(labelAvailableGold);
		panelGold.add(Box.createHorizontalGlue());
		return panelGold;
	}

	private void showDialog() {
		super.okButton.setEnabled(true);
		validate();
		pack();
	}



	protected void updateGoldValue() {
		if (superInitialized) {
			labelAvailableGold.setText((parameter.isUsesTreasury() ? "Treasury: " : "Petty Cash: ") + StringTool.formatThousands(availableGold) + " gp");
			labelAvailableGold.setForeground(parameter.isUsesTreasury() ? Color.RED : Color.BLACK);
			validate();
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
