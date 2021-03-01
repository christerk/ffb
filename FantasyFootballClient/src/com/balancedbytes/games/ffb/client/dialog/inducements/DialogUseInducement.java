package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.dialog.Dialog;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseInducementParameter;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 *
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogUseInducement extends Dialog implements ActionListener {

	private InducementType fInducement;
	private Card fCard;

	private JButton fButtonWizard;
	private JButton fButtonContinue;
	private Map<Card, JButton> fButtonPerCard;

	public DialogUseInducement(FantasyFootballClient pClient, DialogUseInducementParameter pDialogParameter) {

		super(pClient, "Use Inducement", false);

		Set<InducementType> inducementSet = new HashSet<>();
		if (ArrayTool.isProvided(pDialogParameter.getInducementTypes())) {
			for (InducementType inducement : pDialogParameter.getInducementTypes()) {
				inducementSet.add(inducement);
			}
		}

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel panelText = new JPanel();
		panelText.setLayout(new BoxLayout(panelText, BoxLayout.X_AXIS));
		panelText.add(new JLabel("Which inducement do you want to use?"));
		panelText.add(Box.createHorizontalGlue());

		panelMain.add(panelText);
		panelMain.add(Box.createVerticalStrut(10));

		fButtonPerCard = new HashMap<>();
		if (ArrayTool.isProvided(pDialogParameter.getCards())) {
			for (Card card : pDialogParameter.getCards()) {

				JPanel panelCard = new JPanel();
				panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.X_AXIS));
				StringBuilder buttonText = new StringBuilder();
				buttonText.append("<html>");
				buttonText.append("<b>").append(card.getName()).append("</b>");
				buttonText.append("<br>").append(card.getHtmlDescription());
				buttonText.append("</html>");
				JButton buttonCard = new JButton(buttonText.toString());
				buttonCard.setHorizontalAlignment(SwingConstants.LEFT);
				buttonCard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
				buttonCard.addActionListener(this);
				panelCard.add(buttonCard);

				fButtonPerCard.put(card, buttonCard);

				panelMain.add(panelCard);
				panelMain.add(Box.createVerticalStrut(5));

			}
		}

		if (inducementSet.contains(InducementType.WIZARD)) {

			JPanel panelWizard = new JPanel();
			panelWizard.setLayout(new BoxLayout(panelWizard, BoxLayout.X_AXIS));
			StringBuilder buttonText = new StringBuilder();
			buttonText.append("<html>");
			buttonText.append("<b>Wizard</b>");
			buttonText.append("<br>Cast a spell");
			buttonText.append("</html>");
			fButtonWizard = new JButton(buttonText.toString());
			fButtonWizard.setHorizontalAlignment(SwingConstants.LEFT);
			fButtonWizard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			fButtonWizard.addActionListener(this);
			panelWizard.add(fButtonWizard);

			panelMain.add(panelWizard);
			panelMain.add(Box.createVerticalStrut(5));

		}

		JPanel panelContinue = new JPanel();
		panelContinue.setLayout(new BoxLayout(panelContinue, BoxLayout.X_AXIS));
		fButtonContinue = new JButton("Continue");
		fButtonContinue.addActionListener(this);
		panelContinue.add(Box.createHorizontalGlue());
		panelContinue.add(fButtonContinue);
		panelContinue.add(Box.createHorizontalGlue());

		panelMain.add(Box.createVerticalStrut(10));
		panelMain.add(panelContinue);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelMain, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.USE_INDUCEMENT;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		fInducement = null;
		if (pActionEvent.getSource() == fButtonWizard) {
			fInducement = InducementType.WIZARD;
		}
		fCard = null;
		for (Card card : fButtonPerCard.keySet()) {
			if (pActionEvent.getSource() == fButtonPerCard.get(card)) {
				fCard = card;
				break;
			}
		}
		if (pActionEvent.getSource() == fButtonContinue) {
			fInducement = null;
			fCard = null;
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public InducementType getInducement() {
		return fInducement;
	}

	public Card getCard() {
		return fCard;
	}

}
