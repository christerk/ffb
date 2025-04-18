package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kalimar
 */
public class DialogInformation extends Dialog implements ActionListener {

	public static final int OK_DIALOG = 1;
	public static final int CANCEL_DIALOG = 2;

	private final JCheckBox doNotShowAgainCheckbox;
	private final int fOptionType;
	private final CommonProperty panelProperty;
	private final String panelOffValue;

	public DialogInformation(FantasyFootballClient pClient, String pTitle, String pMessage, int pOptionType) {
		this(pClient, pTitle, new String[]{pMessage}, pOptionType, false, null, null, null);
	}

	public DialogInformation(FantasyFootballClient pClient, String pTitle, String[] pMessages, int pOptionType,
													 boolean pCenteredText) {
		this(pClient, pTitle, pMessages, pOptionType, pCenteredText, null, null, null);
	}

	public DialogInformation(FantasyFootballClient pClient, String pTitle, String[] pMessages, int pOptionType,
			String pIconProperty) {
		this(pClient, pTitle, pMessages, pOptionType, false, pIconProperty, null, null);
	}

	public DialogInformation(FantasyFootballClient pClient, String pTitle, String[] pMessages, int pOptionType,
													 boolean pCenteredText, String pIconProperty, CommonProperty panelProperty, String panelOffValue) {

		super(pClient, pTitle, false);
		fOptionType = pOptionType;
		doNotShowAgainCheckbox = new JCheckBox(dimensionProvider(), "Do not show this panel again");

		this.panelProperty = panelProperty;
		this.panelOffValue = panelOffValue;

		JButton fButton;
		if (getOptionType() == OK_DIALOG) {
			fButton = new JButton(dimensionProvider(), "Ok");
		} else {
			fButton = new JButton(dimensionProvider(), "Cancel");
		}
		fButton.addActionListener(this);

		JPanel[] messagePanels = new JPanel[pMessages.length];
		for (int i = 0; i < pMessages.length; i++) {
			messagePanels[i] = new JPanel();
			messagePanels[i].setLayout(new BoxLayout(messagePanels[i], BoxLayout.X_AXIS));
			messagePanels[i].add(new JLabel(dimensionProvider(), pMessages[i]));
			if (!pCenteredText) {
				messagePanels[i].add(Box.createHorizontalGlue());
			}
		}

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < messagePanels.length; i++) {
			if (i > 0) {
				textPanel.add(Box.createVerticalStrut(5));
			}
			textPanel.add(messagePanels[i]);
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		if (StringTool.isProvided(pIconProperty)) {
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(pIconProperty, dimensionProvider());
			infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
			infoPanel.add(Box.createHorizontalStrut(5));
		}
		infoPanel.add(textPanel);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButton);
		if (StringTool.isProvided(panelOffValue) && StringTool.isProvided(panelProperty)) {
			buttonPanel.add(doNotShowAgainCheckbox);
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);

			if (doNotShowAgainCheckbox.isSelected()) {
				getClient().setProperty(panelProperty, panelOffValue);
				getClient().saveUserSettings(false);
			}
		}
	}

	public DialogId getId() {
		return DialogId.INFORMATION;
	}

	public int getOptionType() {
		return fOptionType;
	}

}
