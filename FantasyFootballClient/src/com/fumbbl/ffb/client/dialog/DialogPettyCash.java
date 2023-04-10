package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.IntegerField;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Kalimar
 */
public class DialogPettyCash extends Dialog implements ActionListener, KeyListener {

	private final IntegerField fIntegerFieldPettyCash;
	private final JLabel fLabelTreasury;
	private final JLabel fLabelTeamValue;
	private final JLabel fLabelInducements;

	private final int fOriginalTeamValue;
	private int fTeamValue;
	private final int fOriginalTreasury;
	private int fTreasury;
	private final int fOpponentTeamValue;
	private int fPettyCash;

	public DialogPettyCash(FantasyFootballClient pClient, int pTeamValue, int pTreasury, int pOpponentTeamValue) {

		super(pClient, "Transfer Gold to Petty Cash", false);

		fOriginalTeamValue = pTeamValue;
		fTeamValue = pTeamValue;
		fOriginalTreasury = pTreasury;
		fTreasury = pTreasury;
		fOpponentTeamValue = pOpponentTeamValue;

		JPanel panelTreasury = new JPanel();
		panelTreasury.setLayout(new BoxLayout(panelTreasury, BoxLayout.X_AXIS));
		fLabelTreasury = new JLabel(createTreasuryText());
		panelTreasury.add(fLabelTreasury);
		panelTreasury.add(Box.createHorizontalGlue());

		JPanel panelTeamValue = new JPanel();
		panelTeamValue.setLayout(new BoxLayout(panelTeamValue, BoxLayout.X_AXIS));
		fLabelTeamValue = new JLabel(createTeamValueText());
		panelTeamValue.add(fLabelTeamValue);
		panelTeamValue.add(Box.createHorizontalGlue());

		JPanel panelOpponentTeamValue = new JPanel();
		panelOpponentTeamValue.setLayout(new BoxLayout(panelOpponentTeamValue, BoxLayout.X_AXIS));
		JLabel fLabelOpponentTeamValue = new JLabel(createOpponentTeamValueText());
		panelOpponentTeamValue.add(fLabelOpponentTeamValue);
		panelOpponentTeamValue.add(Box.createHorizontalGlue());

		fIntegerFieldPettyCash = new IntegerField(5);
		fIntegerFieldPettyCash.setText("0");
		fIntegerFieldPettyCash.setHorizontalAlignment(JTextField.RIGHT);
		fIntegerFieldPettyCash.selectAll();
		fIntegerFieldPettyCash.setMaximumSize(fIntegerFieldPettyCash.getPreferredSize());
		fIntegerFieldPettyCash.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent pE) {
				updatePettyCash();
			}

			public void insertUpdate(DocumentEvent pE) {
				updatePettyCash();
			}

			public void changedUpdate(DocumentEvent pE) {
				updatePettyCash();
			}
		});
		fIntegerFieldPettyCash.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent pE) {
				if (getEnteredValue() != fPettyCash / 1000) {
					fIntegerFieldPettyCash.setInt(fPettyCash / 1000);
				}
			}
		});

		JPanel panelPettyCash = new JPanel();
		panelPettyCash.setLayout(new BoxLayout(panelPettyCash, BoxLayout.X_AXIS));
		JLabel labelLeading = new JLabel("Petty Cash");
		labelLeading.setFont(labelLeading.getFont().deriveFont(Font.BOLD));
		panelPettyCash.add(labelLeading);
		panelPettyCash.add(Box.createHorizontalStrut(5));
		panelPettyCash.add(fIntegerFieldPettyCash);
		panelPettyCash.add(Box.createHorizontalStrut(5));
		JLabel labelTrailing = new JLabel("k gold");
		labelTrailing.setFont(labelTrailing.getFont().deriveFont(Font.BOLD));
		panelPettyCash.add(labelTrailing);
		panelPettyCash.add(Box.createHorizontalGlue());

		JPanel panelInducements = new JPanel();
		panelInducements.setLayout(new BoxLayout(panelInducements, BoxLayout.X_AXIS));
		fLabelInducements = new JLabel(createInducementsText());
		panelInducements.add(fLabelInducements);
		panelInducements.add(Box.createHorizontalGlue());

		JButton fButtonTransfer = new JButton(dimensionProvider(), "Transfer");
		fButtonTransfer.addActionListener(this);
		fButtonTransfer.addKeyListener(this);
		fButtonTransfer.setMnemonic(KeyEvent.VK_T);

		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
		panelButtons.add(fButtonTransfer);

		JPanel panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		panelContent.add(panelTreasury);
		panelContent.add(panelTeamValue);
		panelContent.add(panelOpponentTeamValue);
		panelContent.add(Box.createVerticalStrut(5));
		panelContent.add(panelPettyCash);
		panelContent.add(Box.createVerticalStrut(5));
		panelContent.add(panelInducements);
		panelContent.add(Box.createVerticalStrut(5));
		panelContent.add(panelButtons);
		panelContent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelContent, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.PETTY_CASH;
	}

	private String createTreasuryText() {
		return "Your treasury is " + StringTool.formatThousands(fTreasury / 1000) + "k gold.";
	}

	private String createTeamValueText() {
		return "Your team value is " + StringTool.formatThousands(fTeamValue / 1000) + "k.";
	}

	private String createOpponentTeamValueText() {
		return "Your opponent's team value is " + StringTool.formatThousands(fOpponentTeamValue / 1000) +
			"k.";
	}

	private String createInducementsText() {
		StringBuilder line = new StringBuilder();
		int inducements = (fOpponentTeamValue - (fTeamValue));
		if (inducements == 0) {
			line.append("You don't gain or give away any free inducements.");
		} else if (inducements > 0) {
			line.append("You gain free inducements for ").append(StringTool.formatThousands(inducements / 1000))
				.append("k gold.");
		} else {
			line.append("You give away free inducements for ").append(StringTool.formatThousands(-inducements / 1000))
				.append("k gold.");
		}
		return line.toString();
	}

	private void updatePettyCash() {
		fPettyCash = Math.min(getEnteredValue() * 1000, fOriginalTreasury);
		fTreasury = fOriginalTreasury - fPettyCash;
		fLabelTreasury.setText(createTreasuryText());
		fTeamValue = fOriginalTeamValue + fPettyCash;
		fLabelTeamValue.setText(createTeamValueText());
		fLabelInducements.setText(createInducementsText());
	}

	private int getEnteredValue() {
		int enteredValue = 0;

		try {
			enteredValue = fIntegerFieldPettyCash.getInt();
		} catch (NumberFormatException ex) {
			Toolkit.getDefaultToolkit().beep();
		}

		if (enteredValue > fTreasury) {
			enteredValue = fTreasury;
		}
		return enteredValue;
	}

	public int getPettyCash() {
		return fPettyCash;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = true;
		switch (pKeyEvent.getKeyCode()) {
			case KeyEvent.VK_C:
				fPettyCash = 0;
				break;
			case KeyEvent.VK_T:
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