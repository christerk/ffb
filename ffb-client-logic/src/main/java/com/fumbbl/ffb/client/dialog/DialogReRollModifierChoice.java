package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Lets the coach pick a combination of optional roll modifiers or a re-roll.
 */
public class DialogReRollModifierChoice extends Dialog implements ActionListener, KeyListener {

	private static final int MAX_MODIFIER_BUTTONS = 9;

	private final DialogReRollModifierChoiceParameter dialogParameter;
	private final List<JButton> modifierButtons = new ArrayList<>();
	private final List<ModifierChoiceOption> modifierOptions = new ArrayList<>();
	private JButton buttonFallbackReRoll;
	private final JButton buttonTeamReRoll;
	private final JButton buttonProReRoll;
	private final JButton buttonNoReRoll;
	private JButton buttonSkillReRoll;
	private JCheckBox proFallbackMascot, proFallbackTrr;
	private ReRollSource reRollSource;
	private boolean useSkill;
	private Skill usedSkill;
	private ModifierChoiceOption selectedOption;
	private final boolean willUseMascot;

	public DialogReRollModifierChoice(FantasyFootballClient pClient,
																	 DialogReRollModifierChoiceParameter pDialogParameter) {

		super(pClient, "Use a Skill or a Re-roll", false);

		dialogParameter = pDialogParameter;

		DialogExtensionMascot mascotExtension = new DialogExtensionMascot();
		ReRollSource trrSource = mascotExtension.teamReRollSource(pDialogParameter);

		willUseMascot = trrSource == ReRollSources.MASCOT;

		String trrSourceText = trrSource.getName(getClient().getGame());
		if (willUseMascot) {
			trrSourceText += " (No Team Re-Roll)";
		}

		buttonTeamReRoll = new JButton(dimensionProvider(), trrSourceText);
		buttonTeamReRoll.addActionListener(this);
		buttonTeamReRoll.addKeyListener(this);
		buttonTeamReRoll.setMnemonic((int) 'T');
		buttonTeamReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

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

		buttonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		buttonNoReRoll.addActionListener(this);
		buttonNoReRoll.addKeyListener(this);
		buttonNoReRoll.setMnemonic((int) 'N');
		buttonNoReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		for (ModifierChoiceOption option : pDialogParameter.getModifierOptions()) {
			if (modifierOptions.size() >= MAX_MODIFIER_BUTTONS) {
				break;
			}
			JButton button = new JButton(dimensionProvider(), option.getLabel() + " (" + option.getMinimumRoll() + "+)");
			button.addActionListener(this);
			button.addKeyListener(this);
			button.setMnemonic(KeyEvent.VK_1 + modifierOptions.size());
			button.setAlignmentY(Box.TOP_ALIGNMENT);
			modifierButtons.add(button);
			modifierOptions.add(option);
		}

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(new JLabel(dimensionProvider(), "You rolled a " + pDialogParameter.getRoll()
			+ " and needed " + pDialogParameter.getMinimumRoll() + "+ to succeed."));

		if (pDialogParameter.getMessages() != null) {
			for (String additionalMessage : pDialogParameter.getMessages()) {
				messagePanel.add(Box.createVerticalStrut(5));
				messagePanel.add(new JLabel(dimensionProvider(), additionalMessage));
			}
		}

		if (pDialogParameter.isFumble()) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Current roll is a FUMBLE."));
		}

		if (dialogParameter.hasProperty(ReRollProperty.LONER)) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel(dimensionProvider(), "Player is a LONER - the Re-Roll is not guaranteed to help."));
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

		JPanel modifierPanel = new JPanel();
		modifierPanel.setLayout(new BoxLayout(modifierPanel, BoxLayout.X_AXIS));
		modifierPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		modifierPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		for (JButton modifierButton : modifierButtons) {
			modifierPanel.add(modifierButton);
			modifierPanel.add(Box.createHorizontalStrut(5));
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		if (willUseMascot) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonTeamReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
			if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
				buttonFallbackReRoll = new JButton(dimensionProvider(),
					ReRollSources.MASCOT.getName(getClient().getGame()) + " (or Team-ReRoll)");
				buttonFallbackReRoll.addActionListener(this);
				buttonFallbackReRoll.addKeyListener(this);
				buttonFallbackReRoll.setMnemonic((int) 'F');
				buttonFallbackReRoll.setAlignmentY(Box.TOP_ALIGNMENT);
				buttonPanel.add(mascotExtension.wrapperPanel(buttonFallbackReRoll));
				buttonPanel.add(Box.createHorizontalStrut(5));
			}
		} else if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonTeamReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
			if (dialogParameter.hasProperty(ReRollProperty.LONER) &&
				(willUseMascot || dialogParameter.hasProperty(ReRollProperty.TRR))) {
				JPanel proPanel = new JPanel();
				proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
				proPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
				proPanel.setAlignmentY(Box.TOP_ALIGNMENT);
				buttonPanel.add(proPanel);
				buttonProReRoll.setAlignmentX(Box.CENTER_ALIGNMENT);
				proPanel.add(buttonProReRoll);
				if (willUseMascot) {
					proFallbackMascot = mascotExtension.checkBox("Mascot", KeyEvent.VK_A, Color.BLACK, dimensionProvider(),
						this, this);
					proPanel.add(proFallbackMascot);
				}
				if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
					proFallbackTrr = mascotExtension.checkBox(willUseMascot ? "TRR fallback" : "ReRoll", KeyEvent.VK_R,
						Color.BLACK, dimensionProvider(), this, this);
					proFallbackTrr.setEnabled(!willUseMascot);
					proPanel.add(proFallbackTrr);
				}
				buttonPanel.add(Box.createHorizontalStrut(5));
			} else {
				buttonPanel.add(mascotExtension.wrapperPanel(buttonProReRoll));
				buttonPanel.add(Box.createHorizontalStrut(5));
			}
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonSkillReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(mascotExtension.wrapperPanel(buttonNoReRoll));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		if (!modifierButtons.isEmpty()) {
			getContentPane().add(modifierPanel);
		}
		getContentPane().add(buttonPanel);
		addMenuPanel(getContentPane(), pDialogParameter.getMenuProperty(), pDialogParameter.getDefaultValueKey());

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.RE_ROLL_MODIFIER_CHOICE;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == proFallbackMascot) {
			if (proFallbackTrr != null) {
				if (!proFallbackMascot.isSelected()) {
					proFallbackTrr.setSelected(false);
				}
				proFallbackTrr.setEnabled(proFallbackMascot.isSelected());
			}
			return;
		}
		if (pActionEvent.getSource() == proFallbackTrr) {
			return;
		}
		int modifierIndex = modifierButtons.indexOf(pActionEvent.getSource());
		if (modifierIndex >= 0) {
			selectedOption = modifierOptions.get(modifierIndex);
		}
		if (pActionEvent.getSource() == buttonTeamReRoll) {
			reRollSource = willUseMascot ? ReRollSources.MASCOT : ReRollSources.TEAM_RE_ROLL;
		}
		if (pActionEvent.getSource() == buttonFallbackReRoll) {
			reRollSource = ReRollSources.MASCOT_TRR;
		}
		if (pActionEvent.getSource() == buttonProReRoll) {
			if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
				determineProReRollSource();
			}
		}
		if (pActionEvent.getSource() == buttonNoReRoll) {
			reRollSource = null;
		}
		if (pActionEvent.getSource() == buttonSkillReRoll) {
			useSkill = true;
			usedSkill = dialogParameter.getReRollSkill();
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public ReRolledAction getReRolledAction() {
		return dialogParameter.getReRolledAction();
	}

	public DialogReRollModifierChoiceParameter getDialogParameter() {
		return dialogParameter;
	}

	public ModifierChoiceOption getSelectedOption() {
		return selectedOption;
	}

	public boolean isUseModifiers() {
		return selectedOption != null;
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
		int modifierIndex = pKeyEvent.getKeyCode() - KeyEvent.VK_1;
		if (modifierIndex >= 0 && modifierIndex < modifierOptions.size()) {
			selectedOption = modifierOptions.get(modifierIndex);
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
			return;
		}
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_T:
				reRollSource = willUseMascot ? ReRollSources.MASCOT : ReRollSources.TEAM_RE_ROLL;
				break;
			case KeyEvent.VK_F:
				if (buttonFallbackReRoll != null) {
					reRollSource = ReRollSources.MASCOT_TRR;
				}
				break;
			case KeyEvent.VK_P:
				if (dialogParameter.hasProperty(ReRollProperty.PRO)) {
					determineProReRollSource();
				}
				break;
			case KeyEvent.VK_S:
				if (dialogParameter.getReRollSkill() != null) {
					useSkill = true;
					usedSkill = dialogParameter.getReRollSkill();
				} else {
					keyHandled = false;
				}
				break;
			case KeyEvent.VK_A:
				if (proFallbackMascot != null) {
					proFallbackMascot.setSelected(!proFallbackMascot.isSelected());
					if (proFallbackTrr != null) {
						if (!proFallbackMascot.isSelected()) {
							proFallbackTrr.setSelected(false);
						}
						proFallbackTrr.setEnabled(proFallbackMascot.isSelected());
					}
				}
				keyHandled = false;
				break;
			case KeyEvent.VK_R:
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

	private void determineProReRollSource() {
		boolean mascot = proFallbackMascot != null && proFallbackMascot.isSelected();
		boolean reRoll = proFallbackTrr != null && proFallbackTrr.isSelected();

		if (mascot && reRoll) {
			reRollSource = ReRollSources.PRO_MASCOT_TRR;
		} else if (mascot) {
			reRollSource = ReRollSources.PRO_MASCOT;
		} else if (reRoll) {
			reRollSource = ReRollSources.PRO_TRR;
		} else {
			reRollSource = ReRollSources.PRO;
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}
}
