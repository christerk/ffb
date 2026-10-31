package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asks which team re-roll should be used if the previously selected Pro or mascot roll fails.
 */
public class DialogReRollFallback extends Dialog {

	private final Map<Integer, ReRollSource> sourcesByKey = new HashMap<>();
	private ReRollSource reRollSource;

	/**
	 * @param fallbackFor the source whose failure triggers the fallback, Pro or Team Mascot
	 * @param options     the team re-roll sources to choose from
	 */
	public DialogReRollFallback(FantasyFootballClient client, ReRollSource fallbackFor, List<ReRollSource> options) {
		super(client, "Use a Re-roll", false);

		DialogExtensionMascot extension = new DialogExtensionMascot();

		String failingName = fallbackFor == ReRollSources.MASCOT
			? ReRollSources.MASCOT.getName(client.getGame()) : "the Pro roll";

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(new JLabel(dimensionProvider(), "Do you want to use another re-roll if " + failingName + " fails?"));

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BufferedImage icon = getClient().getUserInterface().getIconCache()
			.getIconByProperty(IIconProperty.GAME_DICE_SMALL, dimensionProvider());
		infoPanel.add(new JLabel(dimensionProvider(), new ImageIcon(icon)));
		infoPanel.add(Box.createHorizontalStrut(5));
		infoPanel.add(messagePanel);
		infoPanel.add(Box.createHorizontalGlue());

		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_N) {
					select(null);
				} else if (sourcesByKey.containsKey(e.getKeyCode())) {
					select(sourcesByKey.get(e.getKeyCode()));
				}
			}
		};

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		for (ReRollSource option : options) {
			JButton button = extension.button(option, client.getGame(), dimensionProvider());
			button.addActionListener(e -> select(option));
			button.addKeyListener(keyListener);
			sourcesByKey.put(extension.mnemonic(option), option);
			buttonPanel.add(extension.wrapperPanel(button));
			buttonPanel.add(Box.createHorizontalStrut(5));
		}
		JButton buttonNoReRoll = new JButton(dimensionProvider(), "No Re-Roll", KeyEvent.VK_N);
		buttonNoReRoll.setMnemonic(KeyEvent.VK_N);
		buttonNoReRoll.setAlignmentY(Box.TOP_ALIGNMENT);
		buttonNoReRoll.addActionListener(e -> select(null));
		buttonNoReRoll.addKeyListener(keyListener);
		buttonPanel.add(extension.wrapperPanel(buttonNoReRoll));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();
	}

	private void select(ReRollSource reRollSource) {
		this.reRollSource = reRollSource;
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	@Override
	public DialogId getId() {
		return DialogId.RE_ROLL_FALLBACK;
	}

	/**
	 * @return the selected fallback or null if no further re-roll should be used
	 */
	public ReRollSource getReRollSource() {
		return reRollSource;
	}
}
