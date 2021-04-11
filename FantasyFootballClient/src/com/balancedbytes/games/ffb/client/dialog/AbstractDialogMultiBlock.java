package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.model.Player;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDialogMultiBlock extends AbstractDialogBlock {

	protected final List<List<Integer>> keyEvents = new ArrayList<List<Integer>>() {{
		add(new ArrayList<Integer>() {{
			add(KeyEvent.VK_1);
			add(KeyEvent.VK_2);
			add(KeyEvent.VK_3);
		}});
		add(new ArrayList<Integer>() {{
			add(KeyEvent.VK_4);
			add(KeyEvent.VK_5);
			add(KeyEvent.VK_6);
		}});
	}};

	protected String selectedTarget;
	protected Integer selectedIndex;


	public AbstractDialogMultiBlock(FantasyFootballClient pClient, String pTitle, boolean pCloseable) {
		super(pClient, pTitle, pCloseable);
	}

	protected JButton dieButton(int blockRoll) {
		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JButton button = new JButton();
		button.setOpaque(false);
		button.setBounds(0, 0, 45, 45);
		button.setFocusPainted(false);
		button.setMargin(new Insets(5, 5, 5, 5));
		button.setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll)));
		return button;
	}

	protected JPanel dicePanel(List<Integer> blockRoll, String targetId, boolean activeButtons, List<Integer> events) {
		JPanel panel = blockRollPanel();

		for (int i = 0; i < blockRoll.size(); i++) {
			JButton dieButton = dieButton(blockRoll.get(i));

			if (activeButtons) {
				dieButton.setMnemonic(events.get(i));
				int index = i;
				dieButton.addActionListener(e -> {
					selectedTarget = targetId;
					selectedIndex = index;
					close();
				});
				dieButton.addKeyListener(new DialogOpponentBlockSelection.PressedKeyListener() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == index) {
							selectedTarget = targetId;
							selectedIndex = index;
							close();
						}
					}
				});
			}
			panel.add(dieButton);
		}

		return panel;
	}

	protected JPanel namePanel(String target) {
		Player<?> defender = getClient().getGame().getPlayerById(target);
		JLabel nameLabel = new JLabel("<html>"+ defender.getName() +"</html>");
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setOpaque(false);
		JPanel panel = new JPanel();
		panel.add(nameLabel);
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.setOpaque(false);
		return panel;
	}

	protected abstract void close();

	protected static abstract class PressedKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

}
