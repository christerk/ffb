package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.dialog.Dialog;
import com.fumbbl.ffb.client.ui.ChatLogScrollPane;
import com.fumbbl.ffb.client.ui.ChatLogTextPane;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogBuyCardsParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.CardTypeFactory;
import com.fumbbl.ffb.factory.InducementPhaseFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Kalimar
 */
public class DialogBuyCards extends Dialog implements ActionListener, KeyListener, ListSelectionListener {

	private final Map<CardType, Integer> fNrOfCardsPerType;
	private final Map<CardType, JButton> fButtonPerType;
	private final Map<CardType, Integer> cardPrices;
	private final Map<CardType, Integer> cardLimits;

	private int fAvailableGold;
	private final JLabel fLabelAvailableGold;

	private int fAvailableCards;
	private final JLabel fLabelAvailableCards;

	private final ChatLogTextPane fCardLogTextPane;

	private final JButton fButtonContinue;

	public DialogBuyCards(FantasyFootballClient pClient, DialogBuyCardsParameter pParameter) {

		super(pClient, "Buy Cards", false);

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fAvailableGold = pParameter.getAvailableGold();
		fLabelAvailableGold = new JLabel(dimensionProvider());
		updateAvailableGoldLabel();

		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));
		panelGold.add(fLabelAvailableGold);
		panelGold.add(Box.createHorizontalGlue());
		panelMain.add(panelGold);
		panelMain.add(Box.createVerticalStrut(5));

		fAvailableCards = pParameter.getAvailableCards();
		fLabelAvailableCards = new JLabel(dimensionProvider());
		updateAvailableCardsLabel();

		JPanel panelCards = new JPanel();
		panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.X_AXIS));
		panelCards.add(fLabelAvailableCards);
		panelCards.add(Box.createHorizontalGlue());
		panelMain.add(panelCards);
		panelMain.add(Box.createVerticalStrut(10));

		fNrOfCardsPerType = new HashMap<>();
		fButtonPerType = new HashMap<>();
		cardPrices = new HashMap<>();
		cardLimits = new HashMap<>();

		List<CardType> cardTypes = ((CardTypeFactory)getClient().getGame().getFactory(FactoryType.Factory.CARD_TYPE))
			.getCardTypes().stream().sorted().collect(Collectors.toList());

		for (CardType cardType : cardTypes) {
			int price = ((GameOptionInt) pClient.getGame().getOptions().getOptionWithDefault(cardType.getCostId()))
					.getValue();
			cardPrices.put(cardType, price);

			GameOptionInt gameOption = (GameOptionInt) pClient.getGame().getOptions()
					.getOptionWithDefault(cardType.getMaxId());
			if (gameOption.isChanged()) {
				int limit = gameOption.getValue();
				cardLimits.put(cardType, limit);
			}
		}


		for (CardType cardType: cardTypes) {
			JButton button = new JButton(dimensionProvider());
			button.addActionListener(this);
			fButtonPerType.put(cardType, button);
			fNrOfCardsPerType.put(cardType, pParameter.getNrOfCards(cardType));
			panelMain.add(createDeckPanel(cardType));
			panelMain.add(Box.createVerticalStrut(5));
		}

		fCardLogTextPane = new ChatLogTextPane(pClient.getUserInterface().getStyleProvider(), pClient.getUserInterface().getPitchDimensionProvider());
		ChatLogScrollPane fCardLogScrollPane = new ChatLogScrollPane(fCardLogTextPane);

		JPanel panelCardLog = new JPanel();
		panelCardLog.setLayout(new BorderLayout());
		panelCardLog.add(fCardLogScrollPane, BorderLayout.CENTER);
		panelCardLog.setMinimumSize(new Dimension(450, 135));
		panelCardLog.setPreferredSize(panelCardLog.getMinimumSize());

		panelMain.add(panelCardLog);
		panelMain.add(Box.createVerticalStrut(10));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

		fButtonContinue = new JButton(dimensionProvider(), "Continue");
		fButtonContinue.addActionListener(this);

		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(fButtonContinue);
		panelButtons.add(Box.createHorizontalGlue());

		panelMain.add(panelButtons);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelMain, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public void addCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		fCardLogTextPane.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, pCard.getName());
		fCardLogTextPane.append(ParagraphStyle.INDENT_0, TextStyle.NONE, null);
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, pCard.getDescription());
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, pCard.getDuration().getDescription());
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE,
				new InducementPhaseFactory().getDescription(pCard.getPhases()));
		fCardLogTextPane.append(ParagraphStyle.INDENT_1, TextStyle.NONE, null);
	}

	public void updateDialog() {
		fCardLogTextPane.detachDocument();
		fCardLogTextPane.attachDocument();
		InducementSet inducementSet = getClient().getGame().getTurnDataHome().getInducementSet();
		for (Card card : inducementSet.getAvailableCards()) {
			addCard(card);
		}
	}

	private JPanel createDeckPanel(CardType pType) {
		JPanel deckPanel = new JPanel();
		deckPanel.setLayout(new BoxLayout(deckPanel, BoxLayout.X_AXIS));
		deckPanel.add(updateDeckButton(pType));
		return deckPanel;
	}

	private void updateAvailableGoldLabel() {

		fLabelAvailableGold.setText("Available Gold: " + StringTool.formatThousands(fAvailableGold));
		fLabelAvailableGold.setFont(fontCache().font(Font.BOLD, 12, dimensionProvider()));

	}

	private void updateAvailableCardsLabel() {

		fLabelAvailableCards.setText("Available Cards: " + fAvailableCards);
		fLabelAvailableCards.setFont(fontCache().font(Font.BOLD, 12, dimensionProvider()));

	}

	private JButton updateDeckButton(CardType pType) {

		JButton button = fButtonPerType.get(pType);
		if (button == null) {
			return null;
		}

		Integer limit = cardLimits.get(pType);

		StringBuilder label = new StringBuilder();
		label.append("<html><center>");

		label.append("<b>").append(pType.getDeckName()).append("</b>");
		label.append("<br>");

		int nrOfCards = (fNrOfCardsPerType.get(pType) != null) ? fNrOfCardsPerType.get(pType) : 0;
		int cardPrice = cardPrices.get(pType) != null ? cardPrices.get(pType) : 0;
		label.append(nrOfCards).append(" cards for ").append(StringTool.formatThousands(cardPrice)).append(" gold each")
				.append(" ( max. ");

		int nrAvailableCards = Math.min(nrOfCards, fAvailableCards);

		if (limit != null) {
			label.append(Math.min(limit, nrAvailableCards));
		} else {
			label.append(nrAvailableCards);
		}

		label.append(" more can be purchased )").append("</center></html>");

		button.setText(label.toString());
		button.setEnabled(
				(nrOfCards > 0) && (fAvailableGold >= cardPrice) && (fAvailableCards > 0) && (limit == null || limit > 0));

		return button;

	}

	public DialogId getId() {
		return DialogId.BUY_CARDS;
	}

	public void actionPerformed(ActionEvent pActionEvent) {

		fButtonPerType.entrySet().stream()
			.filter(entry -> entry.getValue() == pActionEvent.getSource())
			.map(Map.Entry::getKey)
			.findFirst()
			.ifPresent(this::buyCard);

		if (pActionEvent.getSource() == fButtonContinue) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}

	}

	private void buyCard(CardType pType) {

		getClient().getCommunication().sendBuyCard(pType);

		int nrOfCards = fNrOfCardsPerType.get(pType);
		fNrOfCardsPerType.put(pType, nrOfCards - 1);

		fAvailableCards--;
		updateAvailableCardsLabel();

		int price = cardPrices.get(pType) != null ? cardPrices.get(pType) : 0;
		fAvailableGold -= price;
		updateAvailableGoldLabel();

		if (cardLimits.containsKey(pType)) {
			cardLimits.put(pType, cardLimits.get(pType) - 1);
		}

		((CardTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.CARD_TYPE))
			.getCardTypes().forEach(this::updateDeckButton);
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

	public void valueChanged(ListSelectionEvent e) {
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
