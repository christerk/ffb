package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.inducement.CardType;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class DialogBuyCardsAndInducements extends AbstractBuyInducementsDialog {

	private final JLabel labelAvailableGold, labelPettyCash;
	private final Map<CardType, Integer> nrOfCardsPerType;
	private final JButton buyCardButton;
	private final int treasury;

	public DialogBuyCardsAndInducements(FantasyFootballClient pClient, DialogBuyCardsAndInducementsParameter pParameter) {

		super(pClient, "Buy Cards And Inducements", null, pParameter.getAvailableGold(), false);

		GameOptions gameOptions = pClient.getGame().getOptions();
		nrOfCardsPerType = pParameter.getNrOfCardsPerType();

		treasury = pParameter.getTreasury();

		JPanel panelCards = new JPanel();
		panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.Y_AXIS));
		panelCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		labelAvailableGold = new JLabel();
		labelPettyCash = new JLabel();

		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));
		panelGold.add(labelAvailableGold);
		panelGold.add(labelPettyCash);
		panelGold.add(Box.createHorizontalGlue());
		panelCards.add(Box.createVerticalStrut(5));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

		buyCardButton = new JButton("Add Card");
		buyCardButton.addActionListener(this);

		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(buyCardButton);
		panelButtons.add(Box.createHorizontalGlue());

		panelCards.add(panelButtons);

		JPanel horizontalMainPanel = new JPanel();
		horizontalMainPanel.setLayout(new BoxLayout(horizontalMainPanel, BoxLayout.X_AXIS));
		horizontalMainPanel.add(panelCards);
		horizontalMainPanel.add(buildInducementPanel(gameOptions));

		JPanel verticalMainPanel = new JPanel();
		verticalMainPanel.setLayout(new BoxLayout(verticalMainPanel, BoxLayout.Y_AXIS));
		verticalMainPanel.add(panelGold);
		verticalMainPanel.add(horizontalMainPanel);
		verticalMainPanel.add(buttonPanel());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(verticalMainPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	protected void setGoldValue(int value) {

		labelAvailableGold.setText("Available Gold: " + StringTool.formatThousands(value));
		labelAvailableGold.setFont(new Font("Sans Serif", Font.BOLD, 12));

	}

	public DialogId getId() {
		return DialogId.BUY_CARDS_AND_INDUCEMENTS;
	}

	public void actionPerformed(ActionEvent pActionEvent) {

		if (pActionEvent.getSource() == buyCardButton) {
			buyCard();
		}
	}

	public void buyCard() {

	}


	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
	}

	public void keyTyped(KeyEvent pKeyEvent) {
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
