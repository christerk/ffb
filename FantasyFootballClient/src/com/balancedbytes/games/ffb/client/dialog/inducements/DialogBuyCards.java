package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.client.dialog.Dialog;
import com.balancedbytes.games.ffb.client.ui.ChatLogScrollPane;
import com.balancedbytes.games.ffb.client.ui.ChatLogTextPane;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.factory.InducementPhaseFactory;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogBuyCards extends Dialog implements ActionListener, KeyListener, ListSelectionListener {

	private Map<CardType, Integer> fNrOfCardsPerType;
	private Map<CardType, JButton> fButtonPerType;
	private Map<CardType, Integer> cardPrices;
	private Map<CardType, Integer> cardLimits;

	private int fAvailableGold;
	private JLabel fLabelAvailableGold;

	private int fAvailableCards;
	private JLabel fLabelAvailableCards;

	// private JButton fButtonMiscellaneousMayhem;
	// private JButton fButtonSpecialTeamPlay;
	private JButton fButtonMagicItem;
	private JButton fButtonDirtyTrick;
	// private JButton fButtonGoodKarma;
	// private JButton fButtonRandomEvent;
	// private JButton fButtonDesperateMeasure;

	private ChatLogScrollPane fCardLogScrollPane;
	private ChatLogTextPane fCardLogTextPane;

	private JButton fButtonContinue;

	public DialogBuyCards(FantasyFootballClient pClient, DialogBuyCardsParameter pParameter) {

		super(pClient, "Buy Cards", false);

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fAvailableGold = pParameter.getAvailableGold();
		fLabelAvailableGold = new JLabel();
		updateAvailableGoldLabel();

		JPanel panelGold = new JPanel();
		panelGold.setLayout(new BoxLayout(panelGold, BoxLayout.X_AXIS));
		panelGold.add(fLabelAvailableGold);
		panelGold.add(Box.createHorizontalGlue());
		panelMain.add(panelGold);
		panelMain.add(Box.createVerticalStrut(5));

		fAvailableCards = pParameter.getAvailableCards();
		fLabelAvailableCards = new JLabel();
		updateAvailableCardsLabel();

		JPanel panelCards = new JPanel();
		panelCards.setLayout(new BoxLayout(panelCards, BoxLayout.X_AXIS));
		panelCards.add(fLabelAvailableCards);
		panelCards.add(Box.createHorizontalGlue());
		panelMain.add(panelCards);
		panelMain.add(Box.createVerticalStrut(10));

		fNrOfCardsPerType = new HashMap<CardType, Integer>();
		fButtonPerType = new HashMap<CardType, JButton>();
		cardPrices = new HashMap<CardType, Integer>();
		cardLimits = new HashMap<CardType, Integer>();

		for (CardType cardType : CardType.values()) {
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

		// Icon cardIcon = new
		// ImageIcon(pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD));

		// fButtonMiscellaneousMayhem = new JButton();
		// fButtonMiscellaneousMayhem.addActionListener(this);
		// fButtonPerType.put(CardType.MISCELLANEOUS_MAYHEM,
		// fButtonMiscellaneousMayhem);
		// fNrOfCardsPerType.put(CardType.MISCELLANEOUS_MAYHEM,
		// pParameter.getNrOfCards(CardType.MISCELLANEOUS_MAYHEM));
		// panelMain.add(createDeckPanel(CardType.MISCELLANEOUS_MAYHEM));
		// panelMain.add(Box.createVerticalStrut(5));

		// fButtonSpecialTeamPlay = new JButton();
		// fButtonSpecialTeamPlay.addActionListener(this);
		// fButtonPerType.put(CardType.SPECIAL_TEAM_PLAY, fButtonSpecialTeamPlay);
		// fNrOfCardsPerType.put(CardType.SPECIAL_TEAM_PLAY,
		// pParameter.getNrOfCards(CardType.SPECIAL_TEAM_PLAY));
		// panelMain.add(createDeckPanel(CardType.SPECIAL_TEAM_PLAY));
		// panelMain.add(Box.createVerticalStrut(5));

		fButtonMagicItem = new JButton();
		fButtonMagicItem.addActionListener(this);
		fButtonPerType.put(CardType.MAGIC_ITEM, fButtonMagicItem);
		fNrOfCardsPerType.put(CardType.MAGIC_ITEM, pParameter.getNrOfCards(CardType.MAGIC_ITEM));
		panelMain.add(createDeckPanel(CardType.MAGIC_ITEM));
		panelMain.add(Box.createVerticalStrut(5));

		fButtonDirtyTrick = new JButton();
		fButtonDirtyTrick.addActionListener(this);
		fButtonPerType.put(CardType.DIRTY_TRICK, fButtonDirtyTrick);
		fNrOfCardsPerType.put(CardType.DIRTY_TRICK, pParameter.getNrOfCards(CardType.DIRTY_TRICK));
		panelMain.add(createDeckPanel(CardType.DIRTY_TRICK));
		panelMain.add(Box.createVerticalStrut(5));

		// fButtonGoodKarma = new JButton();
		// fButtonGoodKarma.addActionListener(this);
		// fButtonPerType.put(CardType.GOOD_KARMA, fButtonGoodKarma);
		// fNrOfCardsPerType.put(CardType.GOOD_KARMA,
		// pParameter.getNrOfCards(CardType.GOOD_KARMA));
		// panelMain.add(createDeckPanel(CardType.GOOD_KARMA));
		// panelMain.add(Box.createVerticalStrut(5));
		//
		// fButtonRandomEvent = new JButton();
		// fButtonRandomEvent.addActionListener(this);
		// fButtonPerType.put(CardType.RANDOM_EVENT, fButtonRandomEvent);
		// fNrOfCardsPerType.put(CardType.RANDOM_EVENT,
		// pParameter.getNrOfCards(CardType.RANDOM_EVENT));
		// panelMain.add(createDeckPanel(CardType.RANDOM_EVENT));
		// panelMain.add(Box.createVerticalStrut(5));
		//
		// fButtonDesperateMeasure = new JButton();
		// fButtonDesperateMeasure.addActionListener(this);
		// fButtonPerType.put(CardType.DESPERATE_MEASURE, fButtonDesperateMeasure);
		// fNrOfCardsPerType.put(CardType.DESPERATE_MEASURE,
		// pParameter.getNrOfCards(CardType.DESPERATE_MEASURE));
		// panelMain.add(createDeckPanel(CardType.DESPERATE_MEASURE));
		// panelMain.add(Box.createVerticalStrut(10));

		fCardLogTextPane = new ChatLogTextPane();
		fCardLogScrollPane = new ChatLogScrollPane(fCardLogTextPane);

		JPanel panelCardLog = new JPanel();
		panelCardLog.setLayout(new BorderLayout());
		panelCardLog.add(fCardLogScrollPane, BorderLayout.CENTER);
		panelCardLog.setMinimumSize(new Dimension(450, 135));
		panelCardLog.setPreferredSize(panelCardLog.getMinimumSize());

		panelMain.add(panelCardLog);
		panelMain.add(Box.createVerticalStrut(10));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

		fButtonContinue = new JButton("Continue");
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

	private JLabel updateAvailableGoldLabel() {

		StringBuilder gold = new StringBuilder();
		gold.append("Available Gold: ").append(StringTool.formatThousands(fAvailableGold));

		fLabelAvailableGold.setText(gold.toString());
		fLabelAvailableGold.setFont(new Font("Sans Serif", Font.BOLD, 12));

		return fLabelAvailableGold;

	}

	private JLabel updateAvailableCardsLabel() {

		StringBuilder cards = new StringBuilder();
		cards.append("Available Cards: ").append(fAvailableCards);

		fLabelAvailableCards.setText(cards.toString());
		fLabelAvailableCards.setFont(new Font("Sans Serif", Font.BOLD, 12));

		return fLabelAvailableCards;

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

		// if (pActionEvent.getSource() == fButtonMiscellaneousMayhem) {
		// buyCard(CardType.MISCELLANEOUS_MAYHEM);
		// }
		// if (pActionEvent.getSource() == fButtonSpecialTeamPlay) {
		// buyCard(CardType.SPECIAL_TEAM_PLAY);
		// }
		if (pActionEvent.getSource() == fButtonMagicItem) {
			buyCard(CardType.MAGIC_ITEM);
		}
		if (pActionEvent.getSource() == fButtonDirtyTrick) {
			buyCard(CardType.DIRTY_TRICK);
		}
		// if (pActionEvent.getSource() == fButtonGoodKarma) {
		// buyCard(CardType.GOOD_KARMA);
		// }
		// if (pActionEvent.getSource() == fButtonRandomEvent) {
		// buyCard(CardType.RANDOM_EVENT);
		// }
		// if (pActionEvent.getSource() == fButtonDesperateMeasure) {
		// buyCard(CardType.DESPERATE_MEASURE);
		// }

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

		// updateDeckButton(CardType.MISCELLANEOUS_MAYHEM);
		// updateDeckButton(CardType.SPECIAL_TEAM_PLAY);
		updateDeckButton(CardType.MAGIC_ITEM);
		updateDeckButton(CardType.DIRTY_TRICK);
		// updateDeckButton(CardType.GOOD_KARMA);
		// updateDeckButton(CardType.RANDOM_EVENT);
		// updateDeckButton(CardType.DESPERATE_MEASURE);

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
