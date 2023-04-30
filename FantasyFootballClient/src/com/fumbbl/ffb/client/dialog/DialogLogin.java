package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JPasswordField;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.client.ui.swing.ScaledBorderFactory;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.NoSuchAlgorithmException;

/**
 * @author Kalimar
 */
public class DialogLogin extends Dialog {

	public static final int FIELD_GAME = 1;
	public static final int FIELD_COACH = 2;
	public static final int FIELD_PASSWORD = 3;

	private final JTextField fFieldGame;
	private final JTextField fFieldCoach;
	private final JPasswordField fFieldPassword;
	private final JButton fButtonCreate;
	private final JButton fButtonList;
	private byte[] fEncodedPassword;
	private int fPasswordLength;
	private boolean fListGames;
	private final boolean fShowGameName;

	private static final String _PASSWORD_DEFAULT = "1234567890123456789012345678901234567890";

	public DialogLogin(FantasyFootballClient pClient, byte[] pEncodedPassword, int pPasswordLength, String pTeamHomeName,
										 String pTeamAwayName, boolean pShowGameName) {

		super(pClient, (ClientMode.PLAYER == pClient.getMode()) ? "Start Game as Player" : "Start Game as Spectator",
			false);
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();

		fPasswordLength = pPasswordLength;
		fEncodedPassword = pEncodedPassword;
		fShowGameName = pShowGameName;
		boolean askForPassword = (fPasswordLength >= 0);

		JPanel teamHomePanel = new JPanel();
		JTextField teamHomeField = new JTextField(dimensionProvider, StringTool.print(pTeamHomeName));
		teamHomeField.setEditable(false);
		teamHomePanel.setLayout(new BoxLayout(teamHomePanel, BoxLayout.X_AXIS));
		teamHomePanel.add(teamHomeField);
		teamHomePanel.setBorder(createTitledBorder(dimensionProvider, "Team"));

		JPanel teamAwayPanel = new JPanel();
		JTextField teamAwayField = new JTextField(dimensionProvider, StringTool.print(pTeamAwayName));
		teamAwayField.setEditable(false);
		teamAwayPanel.setLayout(new BoxLayout(teamAwayPanel, BoxLayout.X_AXIS));
		teamAwayPanel.add(teamAwayField);
		teamAwayPanel.setBorder(createTitledBorder(dimensionProvider, "Opponent"));

		fFieldCoach = new JTextField(dimensionProvider, 20);
		fFieldCoach.setText(pClient.getParameters().getCoach());
		fFieldCoach.setEditable(false);

		JPanel coachPanel = new JPanel();
		coachPanel.setLayout(new BoxLayout(coachPanel, BoxLayout.X_AXIS));
		coachPanel.add(fFieldCoach);
		coachPanel.setBorder(createTitledBorder(dimensionProvider, "Coach"));

		fFieldPassword = new JPasswordField(dimensionProvider, 20);
		if (fEncodedPassword != null) {
			fFieldPassword.setText(_PASSWORD_DEFAULT.substring(0, fPasswordLength));
		}
		fFieldPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent pActionEvent) {
				if ((getClient().getParameters().getMode() == ClientMode.PLAYER) || StringTool.isProvided(getGameName())) {
					if (fFieldGame.isEditable()) {
						fFieldGame.requestFocus();
					} else {
						fButtonCreate.requestFocus();
					}
				} else {
					fButtonCreate.requestFocus();
				}
			}
		});
		fFieldPassword.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent pKeyEvent) {
				toggleButtons();
			}
		});

		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.add(fFieldPassword);
		passwordPanel.setBorder(createTitledBorder(dimensionProvider, "Password"));

		fFieldGame = new JTextField(dimensionProvider, 20);
		fFieldGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent pActionEvent) {
				fButtonCreate.requestFocus();
			}
		});
		fFieldGame.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent pKeyEvent) {
				toggleButtons();
			}
		});
		fFieldGame.setEditable(ClientMode.PLAYER == getClient().getMode());

		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.X_AXIS));
		gamePanel.add(fFieldGame);
		gamePanel.setBorder(createTitledBorder(dimensionProvider, "Game"));

		fButtonList = new JButton(dimensionProvider, "List Open Games");
		fButtonList.addActionListener(pActionEvent -> {
			fListGames = true;
			checkAndCloseDialog();
		});

		if (ClientMode.PLAYER == getClient().getMode()) {
			fButtonCreate = new JButton(dimensionProvider, fShowGameName ? "Start New Game" : "Start Game");
		} else {
			fButtonCreate = new JButton(dimensionProvider, "Spectate Game");
		}
		fButtonCreate.addActionListener(pActionEvent -> {
			fListGames = false;
			checkAndCloseDialog();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(fButtonCreate);
		if (fShowGameName) {
			buttonPanel.add(fButtonList);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		if (StringTool.isProvided(pTeamHomeName)) {
			contentPanel.add(teamHomePanel);
		}
		if (StringTool.isProvided(pTeamAwayName)) {
			contentPanel.add(teamAwayPanel);
		}
		contentPanel.add(coachPanel);
		if (askForPassword) {
			contentPanel.add(passwordPanel);
		}
		if (fShowGameName) {
			contentPanel.add(gamePanel);
		}
		contentPanel.add(buttonPanel);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

	}

	private void showDialogIntern(IDialogCloseListener pCloseListener) {
		fFieldGame.setBorder(MetalBorders.getTextFieldBorder());
		fFieldCoach.setBorder(MetalBorders.getTextFieldBorder());
		fFieldPassword.setBorder(MetalBorders.getTextFieldBorder());
		toggleButtons();
		super.showDialog(pCloseListener);
	}

	public void showDialog(IDialogCloseListener pCloseListener) {
		showDialogIntern(pCloseListener);
		if ((fFieldCoach.getDocument().getLength() > 0) && (fPasswordLength >= 0)) {
			fFieldPassword.requestFocus();
		} else {
			fFieldGame.requestFocus();
		}
	}

	public void showDialogWithError(IDialogCloseListener pCloseListener, int pErrorField) {
		showDialogIntern(pCloseListener);
		switch (pErrorField) {
			case FIELD_GAME:
				fFieldGame.setBorder(BorderFactory.createLineBorder(Color.RED));
				fFieldGame.requestFocus();
				break;
			case FIELD_COACH:
				fFieldCoach.setBorder(BorderFactory.createLineBorder(Color.RED));
				fFieldCoach.requestFocus();
				break;
			case FIELD_PASSWORD:
				fFieldPassword.setBorder(BorderFactory.createLineBorder(Color.RED));
				fFieldPassword.setText("");
				fFieldPassword.requestFocus();
				break;
			default:
				break;
		}
	}

	public String getGameName() {
		return fFieldGame.getText();
	}

	public String getCoach() {
		return fFieldCoach.getText();
	}

	public void setCoach(String pCoach) {
		fFieldCoach.setText(pCoach);
	}

	public byte[] getEncodedPassword() {
		if (fEncodedPassword != null) {
			return fEncodedPassword;
		} else {
			char[] passwordChars = fFieldPassword.getPassword();
			if ((passwordChars != null) && (passwordChars.length > 0)) {
				byte[] password = new String(passwordChars).getBytes();
				try {
					return PasswordChallenge.md5Encode(password);
				} catch (NoSuchAlgorithmException pNsaE) {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public void setEncodedPassword(byte[] pEncodedPassword, int pPasswordLength) {
		fEncodedPassword = pEncodedPassword;
		if (fEncodedPassword != null) {
			fFieldPassword.setText(_PASSWORD_DEFAULT.substring(0, pPasswordLength));
		}
	}

	private CompoundBorder createTitledBorder(DimensionProvider dimensionProvider, String title) {
		return BorderFactory.createCompoundBorder(ScaledBorderFactory.createTitledBorder(dimensionProvider, title),
			BorderFactory.createEmptyBorder(0, 2, 2, 2));
	}

	private void checkAndCloseDialog() {
		if (StringTool.isProvided(getCoach())
			&& ((fPasswordLength < 0) || ArrayTool.isProvided(fFieldPassword.getPassword()))) {
			fPasswordLength = fFieldPassword.getDocument().getLength();
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public DialogId getId() {
		return DialogId.GAME_COACH_PASSWORD;
	}

	public boolean isListGames() {
		return fListGames;
	}

	public int getPasswordLength() {
		return fPasswordLength;
	}

	private void toggleButtons() {
		boolean hasGame = (fFieldGame.getDocument().getLength() > 0);
		boolean hasCoach = (fFieldCoach.getDocument().getLength() > 0);
		boolean hasPassword = ((fPasswordLength < 0) || (fFieldPassword.getDocument().getLength() > 0));
		fButtonCreate.setEnabled((!fShowGameName || hasGame) && hasCoach && hasPassword);
		fButtonList.setEnabled(fShowGameName && hasCoach && hasPassword);
	}

}