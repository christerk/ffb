package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.model.BlockPropertiesRoll;
import com.fumbbl.ffb.model.Player;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractDialogMultiBlockProperties extends AbstractDialogBlock {

	protected final List<List<Integer>> blockDieMnemonics = new ArrayList<List<Integer>>() {{
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

	protected final List<Map<String, Integer>> checkBoxMnemonics = new ArrayList<Map<String, Integer>>() {{
		add(new HashMap<String, Integer>() {{
			put("F1", KeyEvent.VK_F1);
			put("F2", KeyEvent.VK_F2);
			put("F3", KeyEvent.VK_F3);
		}});
		add(new HashMap<String, Integer>() {{
			put("F4", KeyEvent.VK_F4);
			put("F5", KeyEvent.VK_F5);
			put("F6", KeyEvent.VK_F6);
		}});
	}};

	protected String selectedTarget;
	protected int selectedIndex = -1;

	protected Map<String, JButton> anyDiceButtons = new HashMap<>();
	protected Map<String, List<JCheckBox>> anyDiceCheckBoxes = new HashMap<>();

	private DialogExtensionMascot mascotExtension = new DialogExtensionMascot();


	public AbstractDialogMultiBlockProperties(FantasyFootballClient pClient, String pTitle, boolean pCloseable) {
		super(pClient, pTitle, pCloseable);
	}

	private JButton dieButton(int blockRoll) {
		IconCache iconCache = getClient().getUserInterface().getIconCache();

		JButton button = new JButton(dimensionProvider());
		button.setOpaque(false);
		button.setBounds(0, 0, 45, 45);
		button.setFocusPainted(false);
		button.setMargin(new Insets(5, 5, 5, 5));
		button.setIcon(new ImageIcon(iconCache.getDiceIcon(blockRoll, dimensionProvider())));
		return button;
	}

	protected JPanel dicePanel(BlockPropertiesRoll blockRoll, boolean activeButtons, List<Integer> events,
		boolean addCheckboxes) {
		JPanel panel = blockRollPanel();

		Map<String, Integer> mnemonicMap = checkBoxMnemonics.remove(0);

		for (int i = 0; i < blockRoll.getBlockRoll().length; i++) {
			JButton dieButton = dieButton(blockRoll.getBlockRoll()[i]);

			if (activeButtons) {
				dieButton.setMnemonic(events.get(i));
				int index = i;
				dieButton.addActionListener(e -> {
					selectedTarget = blockRoll.getTargetId();
					selectedIndex = index;
					close();
				});
				this.addKeyListener(new PressedKeyListener(index) {
					@Override
					protected void handleKey() {
						selectedTarget = blockRoll.getTargetId();
						selectedIndex = index;
						close();
					}
				});
			}
			if (!blockRoll.needsSelection()) {
				dieButton.setEnabled(i == blockRoll.getSelectedIndex());
			}
			if (addCheckboxes) {
				JPanel checkboxPanel = new JPanel();
				checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
				checkboxPanel.setOpaque(false);
				checkboxPanel.add(dieButton);
				Optional<String> key = mnemonicMap.keySet().stream().sorted().findFirst();

				if (key.isPresent()) {
					String mnemonicString = key.get();
					int mnemonic = mnemonicMap.remove(mnemonicString);

					JCheckBox checkBox = new JCheckBox(dimensionProvider(), "( " + mnemonicString + " )");
					checkBox.setOpaque(false);
					checkBox.setEnabled(true);
					checkBox.setFocusPainted(false);
					checkBox.setMnemonic(mnemonic);
					checkBox.addItemListener(
						e -> anyDiceButtons.get(blockRoll.getTargetId()).setEnabled(anyDiceCheckBoxes.get(blockRoll.getTargetId()).stream().anyMatch(AbstractButton::isSelected)));
					checkboxPanel.add(checkBox);
					List<JCheckBox> checkBoxes = anyDiceCheckBoxes.getOrDefault(blockRoll.getTargetId(), new ArrayList<>());
					checkBoxes.add(checkBox);
					anyDiceCheckBoxes.put(blockRoll.getTargetId(), checkBoxes);
				}
				panel.add(checkboxPanel);
			} else {
				panel.add(dieButton);
			}
		}

		return panel;
	}

	protected JPanel namePanel(String target) {
		Player<?> defender = getClient().getGame().getPlayerById(target);
		JLabel nameLabel = new JLabel(dimensionProvider(), "<html>" + defender.getName() + "</html>");
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

		private final int keyCode;

		protected PressedKeyListener(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == keyCode) {
				handleKey();
			}
		}

		protected abstract void handleKey();

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

}
