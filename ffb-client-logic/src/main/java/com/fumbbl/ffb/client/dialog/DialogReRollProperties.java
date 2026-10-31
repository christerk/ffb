package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class DialogReRollProperties extends Dialog implements ActionListener, KeyListener {

	private final Map<JButton, ReRollSource> teamReRollButtons = new LinkedHashMap<>();
	private final Map<Integer, ReRollSource> teamReRollKeys = new HashMap<>();
	private final JButton buttonProReRoll;
	private final JButton buttonNoReRoll;
	private JButton buttonSkillReRoll, buttonModifyingSkill;
	private final DialogReRollPropertiesParameter dialogParameter;
	private ReRollSource fReRollSource;
	private boolean useSkill;
	private Skill usedSkill;

	public DialogReRollProperties(FantasyFootballClient pClient, DialogReRollPropertiesParameter pDialogParameter) {

		super(pClient, "Use a Re-roll", false);

		dialogParameter = pDialogParameter;

		DialogExtensionMascot mascotExtension = new DialogExtensionMascot();
		for (ReRollSource teamReRollSource : mascotExtension.teamReRollSources(pDialogParameter, true)) {
			JButton button = mascotExtension.button(teamReRollSource, getClient().getGame(), dimensionProvider());
			button.addActionListener(this);
			button.addKeyListener(this);
			teamReRollButtons.put(button, teamReRollSource);
			teamReRollKeys.put(mascotExtension.mnemonic(teamReRollSource), teamReRollSource);
		}

		buttonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll");
		buttonProReRoll.addActionListener(this);
		buttonProReRoll.addKeyListener(this);
		buttonProReRoll.setMnemonic((int) 'P');
		buttonProReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		if (pDialogParameter.getReRollSkill() != null) {
			buttonSkillReRoll = new JButton(dimensionProvider(), pDialogParameter.getReRollSkill().getName());
			buttonSkillReRoll.addActionListener(this);
			buttonSkillReRoll.addKeyListener(this);
			buttonSkillReRoll.setMnemonic((int) 'S');
			buttonSkillReRoll.setAlignmentY(Box.TOP_ALIGNMENT);
		}

		if (pDialogParameter.getModifyingSkill() != null) {
			buttonModifyingSkill = new JButton(dimensionProvider(), pDialogParameter.getModifyingSkill().getName());
			buttonModifyingSkill.addActionListener(this);
			buttonModifyingSkill.addKeyListener(this);
			buttonModifyingSkill.setMnemonic((int) 'M');
			buttonModifyingSkill.setAlignmentY(Box.TOP_ALIGNMENT);
		}

		buttonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		buttonNoReRoll.addActionListener(this);
		buttonNoReRoll.addKeyListener(this);
		buttonNoReRoll.setMnemonic((int) 'N');
		buttonNoReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		StringBuilder message = new StringBuilder();

		String action = dialogParameter.getReRolledAction().getName(pClient.getGame().getRules().getSkillFactory());

		if (dialogParameter.getMinimumRoll() > 0) {
			message.append("Do you want to re-roll the failed ").append(action);
		} else {
			message.append("Do you want to re-roll the ").append(action);
		}

		if (pDialogParameter.getModifyingSkill() != null) {
			message.append(" or use ").append(pDialogParameter.getModifyingSkill().getName());
		}
		message.append("?");

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(new JLabel(dimensionProvider(), message.toString()));

		if (pDialogParameter.isFumble()) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Current roll is a FUMBLE."));
		}

		if (pDialogParameter.getMessages() != null) {
			for (String additionalMessage : pDialogParameter.getMessages()) {
				messagePanel.add(Box.createVerticalStrut(5));
				messagePanel.add(new JLabel(dimensionProvider(), additionalMessage));
			}
		}

		if (dialogParameter.hasProperty(ReRollProperty.LONER)) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Player is a LONER - the Re-Roll is not guaranteed to help."));
		}

		if (dialogParameter.getMinimumRoll() > 0) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(),
				"You will need a roll of " + dialogParameter.getMinimumRoll() + "+ to succeed."));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache()
			.getIconByProperty(IIconProperty.GAME_DICE_SMALL, dimensionProvider());
		infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(messagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		for (JButton teamReRollButton : teamReRollButtons.keySet()) {
			buttonPanel.add(mascotExtension.wrapperPanel(teamReRollButton));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonProReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonSkillReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonModifyingSkill != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonModifyingSkill));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(mascotExtension.wrapperPanel(buttonNoReRoll));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);
		addMenuPanel(getContentPane(), pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.RE_ROLL_PROPERTIES;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (teamReRollButtons.containsKey(pActionEvent.getSource())) {
			fReRollSource = teamReRollButtons.get(pActionEvent.getSource());
		}
		if (pActionEvent.getSource() == buttonProReRoll) {
			if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
				fReRollSource = ReRollSources.PRO;
			}
		}
		if (pActionEvent.getSource() == buttonNoReRoll) {
			fReRollSource = null;
		}
		if (pActionEvent.getSource() == buttonSkillReRoll) {
			useSkill = true;
			usedSkill = getDialogParameter().getReRollSkill();
		}
		if (pActionEvent.getSource() == buttonModifyingSkill) {
			useSkill = true;
			usedSkill = getDialogParameter().getModifyingSkill();
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	public ReRolledAction getReRolledAction() {
		return dialogParameter.getReRolledAction();
	}

	public DialogReRollPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}

	public Skill getUsedSkill() {
		return usedSkill;
	}

	public boolean isUseSkill() {
		return useSkill;
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		if (teamReRollKeys.containsKey(pKeyEvent.getKeyCode())) {
			fReRollSource = teamReRollKeys.get(pKeyEvent.getKeyCode());
		} else {
			switch (pKeyEvent.getKeyCode()) {
				case KeyEvent.VK_P:
					if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
						fReRollSource = ReRollSources.PRO;
					} else {
						keyHandled = false;
					}
					break;
				case KeyEvent.VK_S:
					if (getDialogParameter().getReRollSkill() != null) {
						useSkill = true;
						usedSkill = getDialogParameter().getReRollSkill();
					} else {
						keyHandled = false;
					}
					break;
				case KeyEvent.VK_M:
					if (getDialogParameter().getModifyingSkill() != null) {
						useSkill = true;
						usedSkill = getDialogParameter().getModifyingSkill();
					} else {
						keyHandled = false;
					}
					break;
				case KeyEvent.VK_N:
					break;
				default:
					keyHandled = false;
					break;
			}
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
