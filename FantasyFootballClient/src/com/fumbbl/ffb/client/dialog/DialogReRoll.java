package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class DialogReRoll extends Dialog implements ActionListener, KeyListener {

	private final JButton fButtonTeamReRoll;
	private final JButton fButtonProReRoll;
	private final JButton fButtonNoReRoll;
	private final ReRollSource singleUseReRollSource;
	private final DialogReRollParameter fDialogParameter;
	private JButton buttonSkillReRoll;
	private ReRollSource fReRollSource;
	private JButton buttonSingleUseReRoll;
	private boolean useSkill;
	private JButton buttonModifyingSkill;
	private Skill usedSkill;

	public DialogReRoll(FantasyFootballClient pClient, DialogReRollParameter pDialogParameter) {

		super(pClient, "Use a Re-roll", false);

		fDialogParameter = pDialogParameter;

		singleUseReRollSource = pDialogParameter.getSingleUseReRollSource();

		fButtonTeamReRoll = new JButton("Team Re-Roll");
		fButtonTeamReRoll.addActionListener(this);
		fButtonTeamReRoll.addKeyListener(this);
		fButtonTeamReRoll.setMnemonic((int) 'T');

		if (singleUseReRollSource != null) {
			buttonSingleUseReRoll = new JButton(singleUseReRollSource.getName(pClient.getGame()));
			buttonSingleUseReRoll.addActionListener(this);
			buttonSingleUseReRoll.addKeyListener(this);
			buttonSingleUseReRoll.setMnemonic('L');
		}

		fButtonProReRoll = new JButton("Pro Re-Roll");
		fButtonProReRoll.addActionListener(this);
		fButtonProReRoll.addKeyListener(this);
		fButtonProReRoll.setMnemonic((int) 'P');

		if (pDialogParameter.getReRollSkill() != null) {
			buttonSkillReRoll = new JButton(pDialogParameter.getReRollSkill().getName());
			buttonSkillReRoll.addActionListener(this);
			buttonSkillReRoll.addKeyListener(this);
			buttonSkillReRoll.setMnemonic((int) 'S');
		}

		if (pDialogParameter.getModifyingSkill() != null) {
			buttonModifyingSkill = new JButton(pDialogParameter.getModifyingSkill().getName());
			buttonModifyingSkill.addActionListener(this);
			buttonModifyingSkill.addKeyListener(this);
			buttonModifyingSkill.setMnemonic((int) 'M');
		}

		fButtonNoReRoll = new JButton("No Re-Roll");
		fButtonNoReRoll.addActionListener(this);
		fButtonNoReRoll.addKeyListener(this);
		fButtonNoReRoll.setMnemonic((int) 'N');

		StringBuilder message = new StringBuilder();

		String action = fDialogParameter.getReRolledAction().getName(pClient.getGame().getRules().getFactory(Factory.SKILL));

		if (fDialogParameter.getMinimumRoll() > 0) {
			message.append("Do you want to re-roll the failed ").append(action)
				.append("?");
		} else {
			message.append("Do you want to re-roll the ").append(action);
			if (pDialogParameter.getModifyingSkill() != null) {
				message.append(" or use ").append(pDialogParameter.getModifyingSkill().getName());
			}
			message.append("?");
		}

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(new JLabel(message.toString()));

		if (pDialogParameter.isFumble()) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel("Current roll is a FUMBLE."));
		}

		Game game = getClient().getGame();
		Player<?> reRollingPlayer = game.getPlayerById(pDialogParameter.getPlayerId());
		if ((reRollingPlayer != null)
			&& reRollingPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel("Player is a LONER - the Re-Roll is not guaranteed to help."));
		}

		if (fDialogParameter.getMinimumRoll() > 0) {
			messagePanel.add(Box.createVerticalStrut(5));
			messagePanel.add(new JLabel("You will need a roll of " + fDialogParameter.getMinimumRoll() + "+ to succeed."));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE_SMALL);
		infoPanel.add(new JLabel(new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(messagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		if (fDialogParameter.isTeamReRollOption()) {
			buttonPanel.add(fButtonTeamReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonSingleUseReRoll != null) {
			buttonPanel.add(buttonSingleUseReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (fDialogParameter.isProReRollOption()) {
			buttonPanel.add(fButtonProReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		if (buttonSkillReRoll != null) {
			buttonPanel.add(buttonSkillReRoll);
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		buttonPanel.add(fButtonNoReRoll);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.RE_ROLL;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonTeamReRoll) {
			fReRollSource = ReRollSources.TEAM_RE_ROLL;
		}
		if (pActionEvent.getSource() == fButtonProReRoll) {
			fReRollSource = ReRollSources.PRO;
		}
		if (pActionEvent.getSource() == fButtonNoReRoll) {
			fReRollSource = null;
		}
		if (pActionEvent.getSource() == buttonSingleUseReRoll) {
			fReRollSource = singleUseReRollSource;
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
		return fDialogParameter.getReRolledAction();
	}

	public DialogReRollParameter getDialogParameter() {
		return fDialogParameter;
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
				if (getDialogParameter().isTeamReRollOption()) {
					fReRollSource = ReRollSources.TEAM_RE_ROLL;
				}
				break;
			case KeyEvent.VK_P:
				if (getDialogParameter().isProReRollOption()) {
					fReRollSource = ReRollSources.PRO;
				}
				break;
			case KeyEvent.VK_L:
				if (singleUseReRollSource != null) {
					fReRollSource = singleUseReRollSource;
				}
				break;
			case KeyEvent.VK_S:
				useSkill = true;
				usedSkill = getDialogParameter().getReRollSkill();
				break;
			case KeyEvent.VK_M:
				useSkill = true;
				usedSkill = getDialogParameter().getModifyingSkill();
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

	public void keyTyped(KeyEvent pKeyEvent) {
	}

}
