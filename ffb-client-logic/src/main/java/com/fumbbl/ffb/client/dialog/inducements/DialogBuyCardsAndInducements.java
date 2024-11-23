package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardChoice;
import com.fumbbl.ffb.inducement.CardChoices;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.net.commands.ClientCommandSelectCardToBuy;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * @author Kalimar
 */
public class DialogBuyCardsAndInducements extends AbstractBuyInducementsDialog {

	private final Font boldFont;
	private final Font regularFont;
	private final JLabel labelAvailableGold, typeLabel;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final JPanel dynamicPanel = new JPanel(), addCardPanel, deckChoicePanel, cardChoicePanel,
		cardsListPanel = new JPanel(), cardsSummaryPanel = new JPanel();
	private final JButton addCardButton, rerollChoiceButton, selectChoiceButton, choiceOneButton, choiceTwoButton;
	private final Map<CardType, Integer> nrOfCardsPerType;
	private final int cardPrice;
	private int availableGold, cardSlots, maximumGold;
	private CardChoices cardChoices;
	private CardChoice currentChoice;
	private final boolean superInitialized;
	private final DialogBuyCardsAndInducementsParameter parameter;

	public DialogBuyCardsAndInducements(FantasyFootballClient pClient, DialogBuyCardsAndInducementsParameter pParameter) {

		super(pClient, "Buy Cards And Inducements", pParameter.getTeamId(), pParameter.getAvailableGold(), false);
		this.parameter = pParameter;
		superInitialized = true;
		this.cardChoices = pParameter.getCardChoices();

		addCardButton = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
		rerollChoiceButton = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
		selectChoiceButton = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
		choiceOneButton = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
		choiceTwoButton = new JButton(dimensionProvider(), RenderContext.ON_PITCH);
		labelAvailableGold = new JLabel(dimensionProvider(), RenderContext.ON_PITCH);
		typeLabel = new JLabel(dimensionProvider(), RenderContext.ON_PITCH);

		FontCache fontCache = pClient.getUserInterface().getFontCache();

		boldFont = fontCache.font(Font.BOLD, 12, RenderContext.ON_PITCH);
		regularFont = fontCache.font(Font.PLAIN, 11, RenderContext.ON_PITCH);


		GameOptions gameOptions = pClient.getGame().getOptions();
		nrOfCardsPerType = pParameter.getNrOfCardsPerType();

		cardSlots = pParameter.getCardSlots();
		cardPrice = pParameter.getCardPrice();
		maximumGold = availableGold = pParameter.getAvailableGold();
		updateGoldValue();

		addCardPanel = buildAddCardPanel(pParameter);
		deckChoicePanel = buildDeckChoicePanel();
		cardChoicePanel = buildCardChoicePanel();

		JPanel verticalMainPanel = verticalMainPanel(horizontalMainPanel(gameOptions, buildCardPanel()));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(verticalMainPanel, BorderLayout.CENTER);
		showDialog();


		setLocationToCenter();
		Point p = getLocation();
		setLocation(p.x, 10);
	}

	public void setCardChoices(CardChoices cardChoices) {
		this.cardChoices = cardChoices;
	}

	public CardChoices getCardChoices() {
		return cardChoices;
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
		updateSummaryPanel();
		cardsListPanel.setLayout(new BoxLayout(cardsListPanel, BoxLayout.Y_AXIS));
		cardsListPanel.add(Box.createVerticalStrut(5));
		cardsListPanel.setAlignmentX(CENTER_ALIGNMENT);
		cardsListPanel.add(label("Selected Cards:", boldFont));

		JPanel wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		wrapperPanel.add(cardsSummaryPanel);
		wrapperPanel.add(cardsListPanel);
		return wrapperPanel;
	}

	private void updateSummaryPanel() {
		cardsSummaryPanel.removeAll();
		cardsSummaryPanel.add(Box.createVerticalStrut(5));
		cardsSummaryPanel.add(label("Available Card Slots: " + cardSlots, boldFont));
		cardsSummaryPanel.add(Box.createVerticalStrut(5));
		cardsSummaryPanel.add(label("Available Cards:", boldFont));
		nrOfCardsPerType.forEach((key, value) -> {
			cardsSummaryPanel.add(Box.createVerticalStrut(3));
			cardsSummaryPanel.add(label(key.getDeckName() + ": " + value, regularFont));
		});
	}

	private JLabel label(String text, Font font) {
		JLabel label = new JLabel(dimensionProvider(), RenderContext.ON_PITCH);
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

	private JPanel horizontalMainPanel(GameOptions gameOptions, @SuppressWarnings("unused") JPanel panelCards) {
		JPanel horizontalMainPanel = new JPanel();
		horizontalMainPanel.setLayout(new BoxLayout(horizontalMainPanel, BoxLayout.X_AXIS));
		horizontalMainPanel.add(buildInducementPanel(gameOptions));
		// disable cards for now
		//	horizontalMainPanel.add(panelCards);
		return horizontalMainPanel;
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
		// disable cards for now
		//dynamicPanel.removeAll();
		//dynamicPanel.add(addCardPanel);
		getContentPane().validate();
		pack();
	}

	private void showDeckChoice() {
		super.okButton.setEnabled(false);
		dynamicPanel.removeAll();
		selectChoiceButton.setText("Use " + cardChoices.getInitial().getType().getDeckName());
		dynamicPanel.add(deckChoicePanel);
		getContentPane().validate();
		pack();
	}

	private void showCardChoice(CardChoice choice) {
		currentChoice = choice;
		dynamicPanel.removeAll();
		typeLabel.setText("<html>Choose from<br/>" + choice.getType().getDeckName() + "</html>");
		if (choice.getChoiceOne() != null) {
			choiceOneButton.setText(choice.getChoiceOne().getName());
		}
		if (choice.getChoiceTwo() != null) {
			choiceTwoButton.setText(choice.getChoiceTwo().getName());
		}
		dynamicPanel.add(cardChoicePanel);
		getContentPane().validate();
		pack();
	}

	private JPanel buildAddCardPanel(DialogBuyCardsAndInducementsParameter pParameter) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel(dimensionProvider(), RenderContext.ON_PITCH);
		label.setText("<html>Buy card from random<br/>deck for "
				+ StringTool.formatThousands(pParameter.getCardPrice()) + " gp</html>");
		label.setAlignmentX(CENTER_ALIGNMENT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);
		panel.add(Box.createVerticalStrut(3));
		addCardButton.setText("Buy Special Play Card");
		addCardButton.addActionListener(e -> showDeckChoice());
		addCardButton.setAlignmentX(CENTER_ALIGNMENT);
		addCardButton.setEnabled(pParameter.isCanBuyCards());
		addCardButton.setMnemonic((int)'C');

		panel.add(addCardButton);
		return panel;
	}

	private JPanel buildDeckChoicePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		selectChoiceButton.addActionListener(e -> showCardChoice(cardChoices.getInitial()));
		selectChoiceButton.setAlignmentX(CENTER_ALIGNMENT);
		selectChoiceButton.setMnemonic((int)'U');
		panel.add(selectChoiceButton);

		panel.add(Box.createVerticalStrut(2));

		rerollChoiceButton.setText("Reroll to get a different deck");
		rerollChoiceButton.addActionListener(e -> showCardChoice(cardChoices.getRerolled()));
		rerollChoiceButton.setAlignmentX(CENTER_ALIGNMENT);
		rerollChoiceButton.setMnemonic((int)'R');
		panel.add(rerollChoiceButton);

		return panel;
	}

	private JPanel buildCardChoicePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		typeLabel.setAlignmentX(CENTER_ALIGNMENT);
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(typeLabel);
		panel.add(Box.createVerticalStrut(3));

		panel.add(choiceOneButton);
		panel.add(Box.createVerticalStrut(2));

		choiceOneButton.addActionListener(e -> sendCommand(true));
		choiceOneButton.setAlignmentX(CENTER_ALIGNMENT);
		choiceOneButton.setMnemonic((int) '1');

		panel.add(choiceTwoButton);
		choiceTwoButton.addActionListener(e -> sendCommand(false));
		choiceTwoButton.setAlignmentX(CENTER_ALIGNMENT);
		choiceTwoButton.setMnemonic((int) '2');
		return panel;
	}

	private void sendCommand(boolean firstCardChoice) {
		ClientCommandSelectCardToBuy.Selection selection =
			ClientCommandSelectCardToBuy.Selection.valueOf(currentChoice == cardChoices.getInitial(), firstCardChoice);
		getClient().getCommunication().sendCardSelection(selection);
	}

	protected void updateGoldValue() {
		if (superInitialized) {
			labelAvailableGold.setText((parameter.isUsesTreasury() ? "Treasury: " : "Petty Cash: ") + StringTool.formatThousands(availableGold) + " gp");
			labelAvailableGold.setForeground(parameter.isUsesTreasury() ? Color.RED : Color.BLACK);
			getContentPane().validate();
		}
	}

	public void addCard(Card card) {
		cardsListPanel.add(Box.createVerticalStrut(3));
		cardsListPanel.add(new JLabel(dimensionProvider(), card.getName(), RenderContext.ON_PITCH));
		cardSlots--;
		setMaximumGold(getMaximumGold() - cardPrice);
		setAvailableGold(availableGold - cardPrice);
		nrOfCardsPerType.put(card.getType(), nrOfCardsPerType.get(card.getType()) - 2);
		updateSummaryPanel();
		showDialog();
	}

	public DialogId getId() {
		return DialogId.BUY_CARDS_AND_INDUCEMENTS;
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
		if (superInitialized) {
			addCardButton.setEnabled(availableGold >= cardPrice && cardSlots > 0 && parameter.isCanBuyCards());
		}
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
