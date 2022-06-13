package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.Dialog;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class DialogUseInducement extends Dialog implements ActionListener {

	private InducementType fInducement;
	private Card fCard;

	private JButton fButtonWizard, weatherMageButton;
	private final JButton fButtonContinue;
	private final Map<Card, JButton> fButtonPerCard;

	public DialogUseInducement(FantasyFootballClient pClient, DialogUseInducementParameter pDialogParameter) {

		super(pClient, "Use Inducement", false);

		Set<InducementType> inducementSet = new HashSet<>();
		if (ArrayTool.isProvided(pDialogParameter.getInducementTypes())) {
			inducementSet.addAll(Arrays.asList(pDialogParameter.getInducementTypes()));
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
				String buttonText = "<html>" +
					"<b>" + card.getName() + "</b>" +
					"<br>" + card.getHtmlDescription() +
					"</html>";
				JButton buttonCard = new JButton(buttonText);
				buttonCard.setHorizontalAlignment(SwingConstants.LEFT);
				buttonCard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
				buttonCard.addActionListener(this);
				panelCard.add(buttonCard);

				fButtonPerCard.put(card, buttonCard);

				panelMain.add(panelCard);
				panelMain.add(Box.createVerticalStrut(5));

			}
		}

		if (inducementSet.stream().anyMatch(type -> type.hasUsage(Usage.SPELL))) {

			JPanel panelWizard = new JPanel();
			panelWizard.setLayout(new BoxLayout(panelWizard, BoxLayout.X_AXIS));
			String buttonText = "<html>" +
				"<b>Wizard</b>" +
				"<br>Cast a spell" +
				"</html>";
			fButtonWizard = new JButton(buttonText);
			fButtonWizard.setHorizontalAlignment(SwingConstants.LEFT);
			fButtonWizard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			fButtonWizard.addActionListener(this);
			panelWizard.add(fButtonWizard);

			panelMain.add(panelWizard);
			panelMain.add(Box.createVerticalStrut(5));

		}

		if (inducementSet.stream().anyMatch(type -> type.hasUsage(Usage.CHANGE_WEATHER))) {

			JPanel panelWizard = new JPanel();
			panelWizard.setLayout(new BoxLayout(panelWizard, BoxLayout.X_AXIS));
			String buttonText = "<html>" +
				"<b>Weather Mage</b>" +
				"<br>Influence Weather" +
				"</html>";
			weatherMageButton = new JButton(buttonText);
			weatherMageButton.setHorizontalAlignment(SwingConstants.LEFT);
			weatherMageButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			weatherMageButton.addActionListener(this);
			panelWizard.add(weatherMageButton);

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
			fInducement = ((InducementTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE))
				.allTypes().stream().filter(type -> type.hasUsage(Usage.SPELL)).findFirst().orElse(null);
		}
		if (pActionEvent.getSource() == weatherMageButton) {
			fInducement = ((InducementTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE))
				.allTypes().stream().filter(type -> type.hasUsage(Usage.CHANGE_WEATHER)).findFirst().orElse(null);
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
