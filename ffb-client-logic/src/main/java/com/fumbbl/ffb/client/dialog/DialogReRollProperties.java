package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class DialogReRollProperties extends Dialog implements ActionListener, KeyListener {

	private final JButton fButtonTeamReRoll;
	private final JButton fButtonProReRoll;
	private final JButton fButtonNoReRoll;
	private JButton buttonSkillReRoll, buttonModifyingSkill;
	private JCheckBox fallbackToTrr;
	private final DialogReRollPropertiesParameter dialogParameter;
	private ReRollSource fReRollSource;
	private boolean useSkill;
	private Skill usedSkill;
	private boolean willUseMascot;

	public DialogReRollProperties(FantasyFootballClient pClient, DialogReRollPropertiesParameter pDialogParameter) {

		super(pClient, "Use a Re-roll", false);

		dialogParameter = pDialogParameter;

		fButtonTeamReRoll = new JButton(dimensionProvider(), teamReRollText());
		fButtonTeamReRoll.addActionListener(this);
		fButtonTeamReRoll.addKeyListener(this);
		fButtonTeamReRoll.setMnemonic((int) 'T');

		fButtonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll");
		fButtonProReRoll.addActionListener(this);
		fButtonProReRoll.addKeyListener(this);
		fButtonProReRoll.setMnemonic((int) 'P');

		if (pDialogParameter.getReRollSkill() != null) {
			buttonSkillReRoll = new JButton(dimensionProvider(), pDialogParameter.getReRollSkill().getName());
			buttonSkillReRoll.addActionListener(this);
			buttonSkillReRoll.addKeyListener(this);
			buttonSkillReRoll.setMnemonic((int) 'S');
		}

		if (pDialogParameter.getModifyingSkill() != null) {
			buttonModifyingSkill = new JButton(dimensionProvider(), pDialogParameter.getModifyingSkill().getName());
			buttonModifyingSkill.addActionListener(this);
			buttonModifyingSkill.addKeyListener(this);
			buttonModifyingSkill.setMnemonic((int) 'M');
		}

		fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		fButtonNoReRoll.addActionListener(this);
		fButtonNoReRoll.addKeyListener(this);
		fButtonNoReRoll.setMnemonic((int) 'N');

		StringBuilder message = new StringBuilder();

		String action = dialogParameter.getReRolledAction().getName(pClient.getGame().getRules().getFactory(Factory.SKILL));

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
			messagePanel.add(new JLabel(dimensionProvider(), "You will need a roll of " + dialogParameter.getMinimumRoll() + "+ to succeed."));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE_SMALL, dimensionProvider());
		infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(messagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		if (willUseMascot) {
			JPanel mascotPanel = new JPanel();
			mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
			mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
			buttonPanel.add(mascotPanel);
			buttonPanel.add(Box.createHorizontalStrut(5));
			mascotPanel.add(fButtonTeamReRoll);
			if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
				mascotPanel.add(Box.createVerticalStrut(5));
				fallbackToTrr = new JCheckBox(dimensionProvider(), "Fallback to team re-roll");
				fallbackToTrr.setMnemonic('F');
				fallbackToTrr.addKeyListener(this);
				mascotPanel.add(fallbackToTrr);
			}
		} else if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
			buttonPanel.add(fButtonTeamReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
			buttonPanel.add(fButtonProReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(buttonSkillReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonModifyingSkill != null) {
			buttonPanel.add(buttonModifyingSkill);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(fButtonNoReRoll);
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
		if (pActionEvent.getSource() == fButtonTeamReRoll) {
			determinTeamReRollSource();
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
				fReRollSource = ReRollSources.PRO;
			}
		}
		if (pActionEvent.getSource() == fButtonNoReRoll) {
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
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_T:
				determinTeamReRollSource();
				break;
			case KeyEvent.VK_P:
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					fReRollSource = ReRollSources.PRO;
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
			case KeyEvent.VK_F:
				if (fallbackToTrr != null) {
					fallbackToTrr.setSelected(!fallbackToTrr.isSelected());
				}
				keyHandled = false;
				break;
			case KeyEvent.VK_N:
				break;
			default:
				keyHandled = false;
				break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	private void determinTeamReRollSource() {
		if (willUseMascot) {
			if (fallbackToTrr.isSelected())	{
				fReRollSource = ReRollSources.MASCOT_TRR;
			} else {
				fReRollSource = ReRollSources.MASCOT;
			}
		} else {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

	private String teamReRollText() {
		if (dialogParameter.hasProperty(ReRollProperty.BRILLIANT_COACHING)) {
			return ReRollSources.BRILLIANT_COACHING.getName();
		}

		if (dialogParameter.hasProperty(ReRollProperty.PUMP_UP_THE_CROWD)) {
			return ReRollSources.PUMP_UP_THE_CROWD.getName();
		}

		if (dialogParameter.hasProperty(ReRollProperty.SHOW_STAR)) {
			return ReRollSources.SHOW_STAR.getName();
		}

		if (dialogParameter.hasProperty(ReRollProperty.MASCOT)) {
			Optional<InducementType> mascot =
				getClient().getGame().getActingTurnData().getInducementSet().getInducementTypes().stream()
					.filter(ind -> ind.hasUsage(
						Usage.CONDITIONAL_REROLL)).findFirst();
			if (mascot.isPresent()) {
				willUseMascot = true;
				return mascot.get().getDescription();
			}
		}

		return ReRollSources.TEAM_RE_ROLL.getName();

	}
}
