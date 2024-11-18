package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public abstract class DialogThreeWayChoice extends Dialog implements ActionListener, KeyListener {

	private final JButton buttonChoiceOne;
	private final int choiceOneMnemonic, choiceTwoMnemonic, choiceThreeMnemonic;
	private JButton buttonChoiceTwo;
	private boolean choiceOne, choiceTwo;

	public DialogThreeWayChoice(FantasyFootballClient pClient, String pTitle, String pMessageLine) {
		this(pClient, pTitle, new String[]{pMessageLine}, null);
	}

	public DialogThreeWayChoice(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty) {
		this(pClient, pTitle, pMessages, pIconProperty, "Yes", 'Y', "No", 'N');
	}

	public DialogThreeWayChoice(FantasyFootballClient client, String title, String[] messages, String iconProperty, String choiceTwoText, int choiceTwoMnemonic) {
		this(client, title, messages, iconProperty, "Yes", 'Y', choiceTwoText, choiceTwoMnemonic, "No", 'N', null, null);
	}

	public DialogThreeWayChoice(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty, CommonProperty menuProperty, String defaultValueKey) {
		this(pClient, pTitle, pMessages, pIconProperty, "Yes", 'Y', null, 0, "No", 'N', menuProperty, defaultValueKey);
	}

	public DialogThreeWayChoice(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty,
															String pYesButtonText, int pYesButtonMnemonic, String pNoButtonText, int pNoButtonMnemonic) {
		this(pClient, pTitle, pMessages, pIconProperty, pYesButtonText, pYesButtonMnemonic, null, 0, pNoButtonText, pNoButtonMnemonic, null, null);
	}

	public DialogThreeWayChoice(FantasyFootballClient pClient, String pTitle, String[] pMessages, String pIconProperty,
															String choiceOneText, int choiceOneMnemonic, String choiceTwoText, int choiceTwoMnemonic,
															String choiceThreeText, int choiceThreeMnemonic, CommonProperty menuProperty, String defaultValueKey) {

		super(pClient, pTitle, false);

		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();

		this.choiceOneMnemonic = choiceOneMnemonic;
		this.choiceTwoMnemonic = choiceTwoMnemonic;
		this.choiceThreeMnemonic = choiceThreeMnemonic;

		buttonChoiceOne = new JButton(dimensionProvider, choiceOneText);
		buttonChoiceOne.addActionListener(this);
		buttonChoiceOne.addKeyListener(this);
		buttonChoiceOne.setMnemonic(choiceOneMnemonic);

		if (StringTool.isProvided(choiceTwoText)) {
			buttonChoiceTwo = new JButton(dimensionProvider, choiceTwoText);
			buttonChoiceTwo.addActionListener(this);
			buttonChoiceTwo.addKeyListener(this);
			buttonChoiceTwo.setMnemonic(choiceTwoMnemonic);
		}

		JButton buttonChoiceThree = new JButton(dimensionProvider, choiceThreeText);
		buttonChoiceThree.addActionListener(this);
		buttonChoiceThree.addKeyListener(this);
		buttonChoiceThree.setMnemonic(choiceThreeMnemonic);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < pMessages.length; i++) {
			if (!StringTool.isProvided(pMessages[i])) {
				textPanel.add(Box.createVerticalStrut(5));
			} else {
				if (i > 0) {
					textPanel.add(Box.createVerticalStrut(5));
				}
				JPanel messagePanel = new JPanel();
				messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
				JLabel messageLabel = new JLabel(dimensionProvider(), pMessages[i]);
				if (i == 0) {
					messageLabel.setFont(new Font(messageLabel.getFont().getName(), Font.BOLD, messageLabel.getFont().getSize()));
				}
				messagePanel.add(messageLabel);
				messagePanel.add(Box.createHorizontalGlue());
				textPanel.add(messagePanel);
			}
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		if (StringTool.isProvided(pIconProperty)) {
			BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(pIconProperty, RenderContext.ON_PITCH);
			infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
			infoPanel.add(Box.createHorizontalStrut(5));
		}
		infoPanel.add(textPanel);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(buttonChoiceOne);
		buttonPanel.add(Box.createHorizontalStrut(5));
		if (buttonChoiceTwo != null) {
			buttonPanel.add(buttonChoiceTwo);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(buttonChoiceThree);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);
		addMenuPanel(getContentPane(), menuProperty, defaultValueKey);

		pack();
		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		choiceOne = (pActionEvent.getSource() == buttonChoiceOne);
		choiceTwo = (pActionEvent.getSource() == buttonChoiceTwo);
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public boolean isChoiceYes() {
		return isChoiceOne();
	}

	public boolean isChoiceOne() {
		return choiceOne;
	}

	public boolean isChoiceTwo() {
		return choiceTwo;
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		if (pKeyEvent.getKeyCode() == choiceOneMnemonic) {
			choiceOne = true;
			choiceTwo = false;
		} else if (buttonChoiceTwo != null && pKeyEvent.getKeyCode() == choiceTwoMnemonic) {
			choiceOne = false;
			choiceTwo = true;
		} else if (pKeyEvent.getKeyCode() == choiceThreeMnemonic) {
			choiceOne = false;
			choiceTwo = false;
		} else {
			keyHandled = false;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

}
