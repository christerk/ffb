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
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
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

/**
 * @author Kalimar
 */
public class DialogReRollProperties extends Dialog implements ActionListener, KeyListener {

	private final JButton fButtonTeamReRoll;
	private final JButton fButtonProReRoll;
	private final JButton fButtonNoReRoll;
	private JButton buttonSkillReRoll, buttonModifyingSkill;
	private JCheckBox fallbackToTrr, proFallbackMascot, proFallbackTrr;
	private final DialogReRollPropertiesParameter dialogParameter;
	private ReRollSource fReRollSource;
	private boolean useSkill;
	private Skill usedSkill;
	private final boolean willUseMascot;

	public DialogReRollProperties(FantasyFootballClient pClient, DialogReRollPropertiesParameter pDialogParameter) {

		super(pClient, "Use a Re-roll", false);

		dialogParameter = pDialogParameter;

		DialogExtensionMascot mascotExtension = new DialogExtensionMascot();
		ReRollSource trrSource = mascotExtension.teamReRollText(pDialogParameter);

		willUseMascot = trrSource == ReRollSources.MASCOT;

		fButtonTeamReRoll = new JButton(dimensionProvider(), trrSource.getName(getClient().getGame()));
		fButtonTeamReRoll.addActionListener(this);
		fButtonTeamReRoll.addKeyListener(this);
		fButtonTeamReRoll.setMnemonic((int) 'T');
		fButtonTeamReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

		fButtonProReRoll = new JButton(dimensionProvider(), "Pro Re-Roll");
		fButtonProReRoll.addActionListener(this);
		fButtonProReRoll.addKeyListener(this);
		fButtonProReRoll.setMnemonic((int) 'P');
		fButtonProReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

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

		fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		fButtonNoReRoll.addActionListener(this);
		fButtonNoReRoll.addKeyListener(this);
		fButtonNoReRoll.setMnemonic((int) 'N');
		fButtonNoReRoll.setAlignmentY(Box.TOP_ALIGNMENT);

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
		if (willUseMascot) {
			JPanel mascotPanel = new JPanel();
			mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
			mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
			mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
			fButtonTeamReRoll.setAlignmentX(Box.CENTER_ALIGNMENT);
			mascotPanel.add(fButtonTeamReRoll);
			if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
				fallbackToTrr = mascotExtension.checkBox( "TRR fallback", KeyEvent.VK_F, Color.BLACK, dimensionProvider(),
					this, this);
				mascotPanel.add(fallbackToTrr);
			}
			buttonPanel.add(mascotPanel);
			buttonPanel.add(Box.createHorizontalStrut(5));
		} else if (dialogParameter.hasProperty(ReRollProperty.TRR)) {
			buttonPanel.add(mascotExtension.wrapperPanel(fButtonTeamReRoll));
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
				fButtonProReRoll.setAlignmentX(Box.CENTER_ALIGNMENT);
				proPanel.add(fButtonProReRoll);
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
				buttonPanel.add(mascotExtension.wrapperPanel(fButtonProReRoll));
				buttonPanel.add(Box.createHorizontalStrut(5));
			}
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonSkillReRoll));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonModifyingSkill != null) {
			buttonPanel.add(mascotExtension.wrapperPanel(buttonModifyingSkill));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(mascotExtension.wrapperPanel(fButtonNoReRoll));
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
			determineTeamReRollSource();
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
				determineProReRollSource();
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
		if (pActionEvent.getSource() == proFallbackMascot) {
			if (proFallbackTrr != null) {
				if (!proFallbackMascot.isSelected()) {
					proFallbackTrr.setSelected(false);
				}
				proFallbackTrr.setEnabled(proFallbackMascot.isSelected());
			}
			return;
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
				determineTeamReRollSource();
				break;
			case KeyEvent.VK_P:
				if (getDialogParameter().hasProperty(ReRollProperty.PRO)) {
					determineProReRollSource();
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

	private void determineTeamReRollSource() {
		if (willUseMascot) {
			if (fallbackToTrr.isSelected()) {
				fReRollSource = ReRollSources.MASCOT_TRR;
			} else {
				fReRollSource = ReRollSources.MASCOT;
			}
		} else {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
	}

	private void determineProReRollSource() {
		boolean mascot = proFallbackMascot != null && proFallbackMascot.isSelected();
		boolean reRoll = proFallbackTrr != null && proFallbackTrr.isSelected();

		if (mascot && reRoll) {
			fReRollSource = ReRollSources.PRO_MASCOT_TRR;
		} else if (mascot) {
			fReRollSource = ReRollSources.PRO_MASCOT;
		} else if (reRoll) {
			fReRollSource = ReRollSources.PRO_TRR;
		} else {
			fReRollSource = ReRollSources.PRO;
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}
}
