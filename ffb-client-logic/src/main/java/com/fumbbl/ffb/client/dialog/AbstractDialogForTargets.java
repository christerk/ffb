package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.AbstractDialogMultiBlock.PressedKeyListener;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class AbstractDialogForTargets extends Dialog {

	public static final Color HIGHLIGHT = Color.lightGray;
	protected String selectedTarget;

	protected AbstractDialogForTargets(FantasyFootballClient pClient, String title) {
		super(pClient, title, false);
	}

	protected void init(List<String> mainMessages, JPanel detailPanel) {
		JButton noReRollButton = new JButton(dimensionProvider(), "No Re-Roll");
		noReRollButton.addActionListener(e -> close());
		this.addKeyListener(new PressedKeyListener('N') {
			@Override
			protected void handleKey() {
				close();
			}
		});
		noReRollButton.setMnemonic((int) 'N');

		JPanel mainMessagePanel = new JPanel();
		mainMessagePanel.setLayout(new BoxLayout(mainMessagePanel, BoxLayout.Y_AXIS));
		mainMessagePanel.setAlignmentX(CENTER_ALIGNMENT);
		mainMessagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		mainMessages.stream().map(message -> new JLabel(dimensionProvider(), message)).forEach(label -> {
			label.setHorizontalAlignment(SwingConstants.CENTER);
			mainMessagePanel.add(label);
			mainMessagePanel.add(Box.createVerticalStrut(5));
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.setAlignmentX(CENTER_ALIGNMENT);
		bottomPanel.add(noReRollButton);
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

	protected JPanel createDetailPanel() {
		JPanel detailPanel = new JPanel();
		detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
		detailPanel.setAlignmentX(CENTER_ALIGNMENT);
		return detailPanel;
	}

	protected JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBackground(HIGHLIGHT);
		return buttonPanel;
	}

	protected void addTargetPanel(JPanel detailPanel, JPanel targetPanel) {
		targetPanel.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		targetPanel.setBackground(HIGHLIGHT);
		detailPanel.add(targetPanel);
		detailPanel.add(Box.createVerticalStrut(5));
	}

	protected JPanel createTargetPanel(JPanel buttonPanel) {
		JPanel targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
		targetPanel.setAlignmentX(CENTER_ALIGNMENT);
		targetPanel.add(Box.createVerticalStrut(3));
		targetPanel.add(buttonPanel);
		targetPanel.add(Box.createVerticalStrut(3));
		return targetPanel;
	}

	protected void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}
}
