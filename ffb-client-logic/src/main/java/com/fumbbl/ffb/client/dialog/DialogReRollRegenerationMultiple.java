package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class DialogReRollRegenerationMultiple extends Dialog {

	public static final Color HIGHLIGHT = Color.lightGray;
	private String selectedTarget;

	public DialogReRollRegenerationMultiple(FantasyFootballClient pClient,
		DialogReRollRegenerationMultipleParameter parameter) {

		super(pClient, "Use a Re-roll", false);

		JButton fButtonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll");
		fButtonNoReRoll.addActionListener(e -> close());
		this.addKeyListener(new PressedKeyListener('N') {
			@Override
			protected void handleKey() {
				close();
			}
		});
		fButtonNoReRoll.setMnemonic((int) 'N');

		StringBuilder mainMessages = new StringBuilder();
		mainMessages.append("<html>Do you want to re-roll one of these Regeneration rolls using ");
		if (parameter.getInducementType() != null) {
			mainMessages.append(parameter.getInducementType().getName());
		} else {
			mainMessages.append("a Team Re-Roll");
		}
		mainMessages.append("?</html>");

		Game game = getClient().getGame();

		JPanel mainMessagePanel = new JPanel();
		mainMessagePanel.setLayout(new BoxLayout(mainMessagePanel, BoxLayout.Y_AXIS));
		mainMessagePanel.setAlignmentX(CENTER_ALIGNMENT);
		mainMessagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		JLabel label = new JLabel(dimensionProvider(), mainMessages.toString());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		mainMessagePanel.add(label);
		mainMessagePanel.add(Box.createVerticalStrut(5));

		JPanel detailPanel = new JPanel();
		detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
		detailPanel.setAlignmentX(CENTER_ALIGNMENT);
		for (int index = 0; index < parameter.getPlayerIds().size(); index++) {

			JPanel targetPanel = new JPanel();
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);

			String target = parameter.getPlayerIds().get(index);
			Player<?> player = game.getPlayerById(target);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.setBackground(HIGHLIGHT);
			buttonPanel.add(createButton(target, player.getName(), (char) index));

			targetPanel.add(Box.createVerticalStrut(3));
			targetPanel.add(buttonPanel);
			targetPanel.add(Box.createVerticalStrut(3));
			targetPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			targetPanel.setBackground(HIGHLIGHT);
			detailPanel.add(targetPanel);
			detailPanel.add(Box.createVerticalStrut(5));

		}

		JPanel bottomPanel = new JPanel();
		bottomPanel.setAlignmentX(CENTER_ALIGNMENT);
		bottomPanel.add(fButtonNoReRoll);
		detailPanel.add(bottomPanel);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setAlignmentX(CENTER_ALIGNMENT);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache()
			.getIconByProperty(IIconProperty.GAME_DICE_SMALL, dimensionProvider());
		JLabel iconLabel = new JLabel(dimensionProvider(), new ImageIcon(icon));
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(iconLabel);
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(detailPanel);
		infoPanel.add(Box.createHorizontalGlue());

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainMessagePanel);
		getContentPane().add(infoPanel);

		pack();
		setLocationToCenter();

	}

	private JButton createButton(String target, String buttonName, char mnemonic) {
		JButton button = new JButton(dimensionProvider(), buttonName);
		button.addActionListener(e -> handleUserInteraction(target));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				handleUserInteraction(target);
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	private void handleUserInteraction(String target) {
		selectedTarget = target;
		close();
	}

	private void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogReRollRegenerationMultiple.this);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_REGENERATION_MULTIPLE;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}
}
