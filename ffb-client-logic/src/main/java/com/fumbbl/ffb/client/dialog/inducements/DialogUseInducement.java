package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.Dialog;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kalimar
 */
public class DialogUseInducement extends Dialog implements ActionListener {

	private InducementType fInducement;
	private Card fCard;

	private JButton fButtonWizard, weatherMageButton, throwARockButton, regenerationButton;
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
		panelText.add(new JLabel(dimensionProvider(), "Which inducement do you want to use?"));
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
				JButton buttonCard = new JButton(dimensionProvider(), buttonText);
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
			fButtonWizard = new JButton(dimensionProvider(), buttonText);
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
				"<br>Roll for weather" +
				"</html>";
			weatherMageButton = new JButton(dimensionProvider(), buttonText);
			weatherMageButton.setHorizontalAlignment(SwingConstants.LEFT);
			weatherMageButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			weatherMageButton.addActionListener(this);
			panelWizard.add(weatherMageButton);

			panelMain.add(panelWizard);
			panelMain.add(Box.createVerticalStrut(5));

		}

		if (inducementSet.stream().anyMatch(type -> type.hasUsage(Usage.THROW_ROCK))) {

			JPanel panelWizard = new JPanel();
			panelWizard.setLayout(new BoxLayout(panelWizard, BoxLayout.X_AXIS));
			String buttonText = "<html>" +
				"<b>Throw A Rock</b>" +
				"<br>Throw rock at random player (4+ to hit)" +
				"</html>";
			throwARockButton = new JButton(dimensionProvider(), buttonText);
			throwARockButton.setHorizontalAlignment(SwingConstants.LEFT);
			throwARockButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			throwARockButton.addActionListener(this);
			panelWizard.add(throwARockButton);

			panelMain.add(panelWizard);
			panelMain.add(Box.createVerticalStrut(5));

		}

		inducementSet.stream().filter(type -> type.hasUsage(Usage.REGENERATION))
			.forEach(type -> {

				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
				String buttonText = "<html>" +
					"<b>" + type.getDescription() + "</b>" +
					"<br>Re-Roll Regeneration" +
					"</html>";
				regenerationButton = new JButton(dimensionProvider(), buttonText);
				regenerationButton.setHorizontalAlignment(SwingConstants.LEFT);
				regenerationButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
				regenerationButton.addActionListener(this);
				panel.add(regenerationButton);

				panelMain.add(panel);
				panelMain.add(Box.createVerticalStrut(5));

			});

		JPanel panelContinue = new JPanel();
		panelContinue.setLayout(new BoxLayout(panelContinue, BoxLayout.X_AXIS));
		fButtonContinue = new JButton(dimensionProvider(), "Continue");
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
			fInducement = getInducementByType(Usage.SPELL);
		}
		if (pActionEvent.getSource() == weatherMageButton) {
			fInducement = getInducementByType(Usage.CHANGE_WEATHER);
		}
		if (pActionEvent.getSource() == throwARockButton) {
			fInducement = getInducementByType(Usage.THROW_ROCK);
		}
		if (pActionEvent.getSource() == regenerationButton) {
			fInducement = getInducementByType(Usage.REGENERATION);
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

	private InducementType getInducementByType(Usage inducement) {
		return ((InducementTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE))
			.allTypes().stream().filter(type -> type.hasUsage(inducement)).findFirst().orElse(null);
	}

	public InducementType getInducement() {
		return fInducement;
	}

	public Card getCard() {
		return fCard;
	}

}
