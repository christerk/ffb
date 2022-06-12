package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Kalimar
 */
public class DialogWizardSpell extends Dialog implements ActionListener, KeyListener {

	private JButton fButtonLightning;
	private JButton buttonZap;
	private JButton fButtonFireball;
	private final JButton fButtonCancel;
	private SpecialEffect fWizardSpell;
	private final String teamId;

	public DialogWizardSpell(FantasyFootballClient pClient, String teamId) {

		super(pClient, "Wizard Spell", false);

		this.teamId = teamId;
		JPanel panelText = new JPanel();
		panelText.setLayout(new BoxLayout(panelText, BoxLayout.X_AXIS));
		panelText.add(new JLabel("Which spell should your wizard cast?"));

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

		panelButtons.add(Box.createHorizontalGlue());

		if (spellEnabled(SpecialEffect.LIGHTNING)) {
			fButtonLightning = new JButton("Lightning");
			fButtonLightning.addActionListener(this);
			fButtonLightning.addKeyListener(this);
			panelButtons.add(fButtonLightning);
		}

		if (spellEnabled(SpecialEffect.FIREBALL)) {
			fButtonFireball = new JButton("Fireball");
			fButtonFireball.addActionListener(this);
			fButtonFireball.addKeyListener(this);
			panelButtons.add(fButtonFireball);
		}

		if (spellEnabled(SpecialEffect.ZAP)) {
			buttonZap = new JButton("Zap");
			buttonZap.addActionListener(this);
			buttonZap.addKeyListener(this);
			panelButtons.add(buttonZap);
		}

		fButtonCancel = new JButton("Cancel");
		fButtonCancel.addActionListener(this);
		fButtonCancel.addKeyListener(this);
		panelButtons.add(fButtonCancel);

		panelButtons.add(Box.createHorizontalGlue());

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(Box.createVerticalStrut(5));
		getContentPane().add(panelText);
		getContentPane().add(Box.createVerticalStrut(5));
		getContentPane().add(panelButtons);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.WIZARD_SPELL;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() != null) {
			if (pActionEvent.getSource() == fButtonLightning) {
				fWizardSpell = SpecialEffect.LIGHTNING;
			}
			if (pActionEvent.getSource() == buttonZap) {
				fWizardSpell = SpecialEffect.ZAP;
			}
			if (pActionEvent.getSource() == fButtonFireball) {
				fWizardSpell = SpecialEffect.FIREBALL;
			}
			if (pActionEvent.getSource() == fButtonCancel) {
				fWizardSpell = null;
			}
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_L:
			fWizardSpell = SpecialEffect.LIGHTNING;
			break;
		case KeyEvent.VK_Z:
			fWizardSpell = SpecialEffect.ZAP;
			break;
		case KeyEvent.VK_F:
			fWizardSpell = SpecialEffect.FIREBALL;
			break;
		case KeyEvent.VK_C:
			fWizardSpell = null;
			break;
		default:
			keyHandled = false;
			break;
		}

		if (fWizardSpell != null && !spellEnabled(fWizardSpell)) {
			fWizardSpell = null;
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

	public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}

	private boolean spellEnabled(SpecialEffect effect) {
		final Game game = getClient().getGame();
		InducementSet inducementSet = game.getTeamHome().getId().equals(teamId)
			? game.getTurnDataHome().getInducementSet()
			: game.getTurnDataAway().getInducementSet();
		return inducementSet.getInducementMapping().entrySet().stream()
			.filter(entry -> entry.getKey().getUsages() == Usage.SPELL && entry.getValue().getUsesLeft() > 0)
			.flatMap(entry -> entry.getKey().effects().stream()).anyMatch(specialEffect -> specialEffect == effect);

	}
}
