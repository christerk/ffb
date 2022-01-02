package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class DialogSelectBlitzTarget extends Dialog implements ActionListener {

	private final JCheckBox doNotShowAgainCheckbox = new JCheckBox("Do not show this panel again");

	private final String panelProperty; // IClientProperty.SETTING_BLITZ_TARGET_PANEL;
	private final String panelOffValue; // IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF;
	private final DialogId dialogId; // DialogId.SELECT_BLITZ_TARGET;

	public DialogSelectBlitzTarget(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty,
	                               DialogId dialogId, String panelProperty, String panelOffValue) {

		super(pClient, pTitle, false);

		this.dialogId = dialogId;
		this.panelProperty = panelProperty;
		this.panelOffValue = panelOffValue;

		JButton fButton = new JButton("Ok");
		fButton.addActionListener(this);

		JPanel[] messagePanels = new JPanel[pMessages.length];
		for (int i = 0; i < pMessages.length; i++) {
			messagePanels[i] = new JPanel();
			messagePanels[i].setLayout(new BoxLayout(messagePanels[i], BoxLayout.X_AXIS));
			messagePanels[i].add(new JLabel(pMessages[i]));
			messagePanels[i].add(Box.createHorizontalGlue());
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
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(pIconProperty);
			infoPanel.add(new JLabel(new ImageIcon(icon)));
			infoPanel.add(Box.createHorizontalStrut(5));
		}
		infoPanel.add(textPanel);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButton);
		buttonPanel.add(doNotShowAgainCheckbox);
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
			if(doNotShowAgainCheckbox.isSelected())
			{
				getClient().setProperty(panelProperty, panelOffValue);
				getClient().saveUserSettings(false);
			}
		}
	}

	public DialogId getId() {
		return dialogId;
	}
}
