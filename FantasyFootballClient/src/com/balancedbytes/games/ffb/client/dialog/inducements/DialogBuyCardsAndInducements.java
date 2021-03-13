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
import javax.swing.SwingConstants;
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
	private final JLabel labelAvailableGold = new JLabel(), labelPettyCash = new JLabel(), labelTreasury = new JLabel(), typeLabel = new JLabel();
	private final JPanel dynamicPanel = new JPanel(), addCardPanel, deckChoicePanel, cardChoicePanel, cardsSummaryPanel = new JPanel();
	private final JButton addCardButton = new JButton(), rerollChoiceButton= new JButton(), selectChoiceButton = new JButton(), choiceOneButton = new JButton(), choiceTwoButton = new JButton();
	private final Map<CardType, Integer> nrOfCardsPerType;
	private final int availableGold, pettyCash, treasury;
	private final CardChoice initialChoice, rerolledChoice;
	private CardChoice currentChoice;

	public DialogBuyCardsAndInducements(FantasyFootballClient pClient, DialogBuyCardsAndInducementsParameter pParameter) {

		super(pClient, "Buy Cards And Inducements", null, pParameter.getAvailableGold(), false);
		this.initialChoice = pParameter.getInitialChoice();
		this.rerolledChoice = pParameter.getRerolledChoice();

		GameOptions gameOptions = pClient.getGame().getOptions();
		nrOfCardsPerType = pParameter.getNrOfCardsPerType();

		treasury = pParameter.getTreasury();
		availableGold = pParameter.getAvailableGold();
		pettyCash =  availableGold - treasury;
		updateGoldValue();

		addCardPanel = buildAddCardPanel(pParameter);
		deckChoicePanel = buildDeckChoicePanel();
		cardChoicePanel = buildCardChoicePanel();

		JPanel verticalMainPanel = verticalMainPanel(horizontalMainPanel(gameOptions, buildCardPanel()));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(verticalMainPanel, BorderLayout.CENTER);
		showAddCardButton();

		setLocationToCenter();

	}

	private JPanel buildCardPanel() {
		JPanel panelCards = new JPanel();
		panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.Y_AXIS));
		panelCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelCards.setMinimumSize(new Dimension(400, 0));

		dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.X_AXIS));
		dynamicPanel.setAlignmentX(CENTER_ALIGNMENT);
		panelCards.add(Box.createVerticalStrut(20));
		panelCards.add(buildCardInfoPanel());
		panelCards.add(Box.createVerticalGlue());
		panelCards.add(dynamicPanel);
		panelCards.add(Box.createVerticalStrut(5));
		return panelCards;
	}

	private JPanel buildCardInfoPanel(){
		cardsSummaryPanel.setLayout(new BoxLayout(cardsSummaryPanel, BoxLayout.Y_AXIS));
		cardsSummaryPanel.setAlignmentX(CENTER_ALIGNMENT);
		cardsSummaryPanel.add(Box.createVerticalStrut(5));
		cardsSummaryPanel.add(label("Available Cards:", BOLD_FONT));
		nrOfCardsPerType.forEach((key, value) -> {
			cardsSummaryPanel.add(Box.createVerticalStrut(3));
			cardsSummaryPanel.add(label(key.getDeckName() + ": " + value, REGULAR_FONT));
		});
		cardsSummaryPanel.add(Box.createVerticalStrut(5));
		cardsSummaryPanel.add(label("Selected Cards:", BOLD_FONT));

		JPanel wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
		wrapperPanel.add(cardsSummaryPanel);
		return wrapperPanel;
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
		horizontalMainPanel.add(buildInducementPanel(gameOptions));
		horizontalMainPanel.add(panelCards);
		return horizontalMainPanel;
	}

	private JPanel goldPanel() {
		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));
		labelAvailableGold.setFont(BOLD_FONT);
		labelPettyCash.setFont(BOLD_FONT);
		labelTreasury.setFont(BOLD_FONT);

		panelGold.add(Box.createHorizontalGlue());
		panelGold.add(labelAvailableGold);
		panelGold.add(Box.createHorizontalGlue());
		panelGold.add(labelPettyCash);
		panelGold.add(Box.createHorizontalGlue());
		panelGold.add(labelTreasury);
		panelGold.add(Box.createHorizontalGlue());
		return panelGold;
	}

	private void showAddCardButton() {
		dynamicPanel.removeAll();
		dynamicPanel.add(addCardPanel);
		getContentPane().validate();
		pack();
	}

	private void showDeckChoice() {
		dynamicPanel.removeAll();
		selectChoiceButton.setText("Use " + initialChoice.getType().getDeckName());
		dynamicPanel.add(deckChoicePanel);
		getContentPane().validate();
		pack();
	}

	private void showCardChoice(CardChoice choice) {
		currentChoice = choice;
		dynamicPanel.removeAll();
		typeLabel.setText("<html>Choose from<br/>" + choice.getType().getDeckName() + "</html>");
		choiceOneButton.setText(choice.getChoiceOne().getName());
		choiceTwoButton.setText(choice.getChoiceTwo().getName());
		dynamicPanel.add(cardChoicePanel);
		getContentPane().validate();
		pack();
	}

	private JPanel buildAddCardPanel(DialogBuyCardsAndInducementsParameter pParameter) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel();
		label.setText("<html>Buy card from random<br/>deck for "
				+ StringTool.formatThousands(pParameter.getMinimumCardPrice()) + " gp</html>");
		label.setAlignmentX(CENTER_ALIGNMENT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);
		panel.add(Box.createVerticalStrut(3));
		addCardButton.setText("Buy Special Play Card");
		addCardButton.addActionListener(e -> showDeckChoice());
		addCardButton.setAlignmentX(CENTER_ALIGNMENT);

		panel.add(addCardButton);
		return panel;
	}

	private JPanel buildDeckChoicePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		selectChoiceButton.addActionListener(e -> showCardChoice(initialChoice));
		selectChoiceButton.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(selectChoiceButton);

		panel.add(Box.createVerticalStrut(2));

		rerollChoiceButton.setText("Reroll to get a different deck");
		rerollChoiceButton.addActionListener(e -> {
			showCardChoice(rerolledChoice);
		});
		rerollChoiceButton.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(rerollChoiceButton);

		return panel;
	}

	private JPanel buildCardChoicePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		typeLabel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(typeLabel);
		panel.add(Box.createVerticalStrut(3));

		panel.add(choiceOneButton);
		panel.add(Box.createVerticalStrut(2));

		choiceOneButton.addActionListener(e -> {
			cardsSummaryPanel.add(Box.createVerticalStrut(3));
			cardsSummaryPanel.add(new JLabel(currentChoice.getChoiceOne().getName()));
			showAddCardButton();
		});
		choiceOneButton.setAlignmentX(CENTER_ALIGNMENT);

		panel.add(choiceTwoButton);
		choiceTwoButton.addActionListener(e -> {
			cardsSummaryPanel.add(Box.createVerticalStrut(3));
			cardsSummaryPanel.add(new JLabel(currentChoice.getChoiceTwo().getName()));
			showAddCardButton();
		});
		choiceTwoButton.setAlignmentX(CENTER_ALIGNMENT);

		return panel;
	}

	protected void updateGoldValue() {
		labelAvailableGold.setText("Total Gold: " + StringTool.formatThousands(availableGold) + " gp");

		labelPettyCash.setText("Petty Cash (free): " + StringTool.formatThousands(pettyCash) + " gp");

		labelTreasury.setText("Team Treasury: " + StringTool.formatThousands(treasury) + " gp");
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
