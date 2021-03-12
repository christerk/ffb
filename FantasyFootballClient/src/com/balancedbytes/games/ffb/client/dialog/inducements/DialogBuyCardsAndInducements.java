package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.inducement.CardChoice;
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

	private static final Font BOLD_FONT = new Font("Sans Serif", Font.BOLD, 12);
	private static final Font REGULAR_FONT = new Font("Sans Serif", Font.PLAIN, 11);
	private final JLabel labelAvailableGold = new JLabel(), labelPettyCash = new JLabel(), choiceOneLabel = new JLabel(), choiceTwoLabel = new JLabel();
	private final JPanel dynamicPanel, addCardPanel, initialChoicePanel;
	private final JButton addCardButton, rerollChoiceButton, selectChoiceButton;
	private final Map<CardType, Integer> nrOfCardsPerType;
	private final int treasury;
	private final CardChoice initialChoice, rerolledChoice;

	public DialogBuyCardsAndInducements(FantasyFootballClient pClient, DialogBuyCardsAndInducementsParameter pParameter) {

		super(pClient, "Buy Cards And Inducements", null, pParameter.getAvailableGold(), false);
		this.initialChoice = pParameter.getInitialChoice();
		this.rerolledChoice = pParameter.getRerolledChoice();

		GameOptions gameOptions = pClient.getGame().getOptions();
		nrOfCardsPerType = pParameter.getNrOfCardsPerType();

		dynamicPanel = new JPanel();
		dynamicPanel.setLayout(new BorderLayout());

		treasury = pParameter.getTreasury();
		addCardButton = new JButton("Add Card");

		addCardButton.addActionListener(e -> {
			showDeckChoice();
		});
		rerollChoiceButton = new JButton("Reroll to get a different deck");
		selectChoiceButton = new JButton();
		selectChoiceButton.addActionListener(e -> {
			showAddCardButton();
		});
		addCardPanel = buildAddCardPanel();
		initialChoicePanel = buildDeckChoicePanel();

		JPanel panelCards = new JPanel();
		panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.Y_AXIS));
		panelCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelCards.add(Box.createVerticalStrut(5));
		panelCards.add(dynamicPanel);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(label("Available Cards:", BOLD_FONT));
		nrOfCardsPerType.forEach((key, value) -> textPanel.add(label(key.getDeckName() + ": " + value, REGULAR_FONT)));
		textPanel.add(label("Drawn Cards:", BOLD_FONT));

		JPanel wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
		wrapperPanel.add(Box.createHorizontalGlue());
		wrapperPanel.add(textPanel);
		wrapperPanel.add(Box.createHorizontalGlue());
		panelCards.add(wrapperPanel);

		JPanel verticalMainPanel = verticalMainPanel(horizontalMainPanel(gameOptions, panelCards));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(verticalMainPanel, BorderLayout.CENTER);
		showDeckChoice();
		pack();

		setLocationToCenter();

	}

	private JLabel label(String text, Font font) {
		JLabel label = new JLabel();
		label.setText(text);
		label.setFont(font);
		return label;
	}


	private JPanel verticalMainPanel(JPanel horizontalMainPanel) {
		JPanel verticalMainPanel = new JPanel();
		verticalMainPanel.setLayout(new BoxLayout(verticalMainPanel, BoxLayout.Y_AXIS));
		verticalMainPanel.add(goldPanel());
		verticalMainPanel.add(horizontalMainPanel);
		verticalMainPanel.add(buttonPanel());
		return verticalMainPanel;
	}

	private JPanel horizontalMainPanel(GameOptions gameOptions, JPanel panelCards) {
		JPanel horizontalMainPanel = new JPanel();
		horizontalMainPanel.setLayout(new BoxLayout(horizontalMainPanel, BoxLayout.X_AXIS));
		horizontalMainPanel.add(panelCards);
		horizontalMainPanel.add(buildInducementPanel(gameOptions));
		return horizontalMainPanel;
	}

	private JPanel goldPanel() {
		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));
		panelGold.add(labelAvailableGold);
		panelGold.add(labelPettyCash);
		panelGold.add(Box.createHorizontalGlue());
		return panelGold;
	}

	private void showAddCardButton() {
		dynamicPanel.removeAll();
		dynamicPanel.add(addCardPanel, BorderLayout.CENTER);
		getContentPane().validate();
	}

	private void showDeckChoice() {
		dynamicPanel.removeAll();
		selectChoiceButton.setText("Use " + initialChoice.getType() + " deck");
		dynamicPanel.add(initialChoicePanel, BorderLayout.CENTER);
		getContentPane().validate();
	}

	private JPanel buildAddCardPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(Box.createHorizontalGlue());
		panel.add(addCardButton);
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private JPanel buildDeckChoicePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(selectChoiceButton);
		panel.add(rerollChoiceButton);
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	protected void setGoldValue(int value) {

		labelAvailableGold.setText("Available Gold: " + StringTool.formatThousands(value));
		labelAvailableGold.setFont(BOLD_FONT);

	}

	public DialogId getId() {
		return DialogId.BUY_CARDS_AND_INDUCEMENTS;
	}

	public void actionPerformed(ActionEvent pActionEvent) {

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
