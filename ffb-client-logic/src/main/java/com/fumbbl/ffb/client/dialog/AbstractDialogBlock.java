package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public abstract class AbstractDialogBlock extends Dialog {

	protected final Color colorOwnChoice = new Color(128, 0, 0);
	protected final Color colorOpponentChoice = new Color(12, 20, 136);

	protected final DialogExtensionMascot mascotExtension = new DialogExtensionMascot();


	public AbstractDialogBlock(FantasyFootballClient pClient, String pTitle, boolean pCloseable) {
		super(pClient, pTitle, pCloseable);
	}

	protected JPanel opponentChoicePanel() {
		return textPanel("Opponent's choice");
	}

	protected JPanel proTextPanel() {
		return textPanel("Pro Re-Rolls");
	}

	protected JPanel textPanel(String text) {
		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		JLabel label = new JLabel(dimensionProvider(), text);
		label.setFont(
			new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		textPanel.add(label);
		textPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		return textPanel;
	}

	protected JPanel blockRollPanel() {
		JPanel blockRollPanel = new JPanel();
		blockRollPanel.setOpaque(false);
		blockRollPanel.setLayout(new BoxLayout(blockRollPanel, BoxLayout.X_AXIS));
		blockRollPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return blockRollPanel;
	}

	protected JButton createReRollButton(String buttonName, char mnemonic, Runnable action) {
		JButton button = new JButton(dimensionProvider(), buttonName, mnemonic);
		button.setOpaque(false);
		button.addActionListener(e -> action.run());
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				action.run();
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	protected void handleReRollUse(ReRollSource source) {
	}

	protected void registerAnyDiceButton(JButton button) {
	}

	protected void registerTrrFallbackCheckbox(JCheckBox checkbox) {
	}

	protected JPanel wrapProButton(JButton proButton) {
		return mascotExtension.wrapperPanel(proButton);
	}

	protected void addReRollButtonsToPanel(
		JPanel panel,
		Mnemonics mnemonics,
		ReRollSource trrSource,
		boolean willUseMascot,
		boolean hasTrr,
		boolean ownChoice,
		int nrOfDice,
		ReRollSource singleDiePerActivationSource,
		ReRollSource singleDieSource,
		ReRollSource singleBlockDieSource,
		ReRollSource bothDownSource,
		ReRollSource skullSource,
		ReRollSource anyDiceSource,
		boolean anyDiceEnabled,
		boolean brawlerBeforeAnyDice,
		boolean addGlueAfterEach) {

		if (hasTrr || willUseMascot) {
			JButton trrButton = createReRollButton(trrSource.getName(getClient().getGame()), mnemonics.team,
				() -> handleReRollUse(ReRollSources.TEAM_RE_ROLL));
			if (willUseMascot) {
				JPanel mascotPanel = new JPanel();
				mascotPanel.setBackground(null);
				mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
				mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
				mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
				trrButton.setAlignmentX(Box.CENTER_ALIGNMENT);
				mascotPanel.add(trrButton);
				mascotPanel.setOpaque(false);
				if (hasTrr) {
					JCheckBox trrCheckbox = mascotExtension.checkBox("TRR fallback", mnemonics.trrFallback,
						java.awt.Color.WHITE, dimensionProvider(), null, null);
					trrCheckbox.setSelected(true);
					mascotPanel.add(trrCheckbox);
					registerTrrFallbackCheckbox(trrCheckbox);
				}
				panel.add(mascotPanel);
			} else {
				panel.add(mascotExtension.wrapperPanel(trrButton));
			}
			if (addGlueAfterEach) {
				panel.add(Box.createHorizontalGlue());
			}
		}

		if (nrOfDice == 1) {
			if (singleDiePerActivationSource != null) {
				JButton proButton = createReRollButton(singleDiePerActivationSource.getName(getClient().getGame()),
					mnemonics.pro.get(0), () -> handleReRollUse(singleDiePerActivationSource));
				panel.add(wrapProButton(proButton));
				if (addGlueAfterEach) {
					panel.add(Box.createHorizontalGlue());
				}
			}
			if (singleDieSource != null) {
				panel.add(mascotExtension.wrapperPanel(createReRollButton(singleDieSource.getName(getClient().getGame()),
					mnemonics.anyDie.get(0), () -> handleReRollUse(singleDieSource))));
				if (addGlueAfterEach) {
					panel.add(Box.createHorizontalGlue());
				}
			}
			if (singleBlockDieSource != null) {
				panel.add(mascotExtension.wrapperPanel(createReRollButton(singleBlockDieSource.getName(getClient().getGame()),
					mnemonics.singleBlockDie.get(0), () -> handleReRollUse(singleBlockDieSource))));
				if (addGlueAfterEach) {
					panel.add(Box.createHorizontalGlue());
				}
			}
		}

		if (brawlerBeforeAnyDice) {
			addBrawlerButton(panel, mnemonics, bothDownSource, addGlueAfterEach);
			addHatredButton(panel, mnemonics, skullSource, addGlueAfterEach);
			addAnyDiceButton(panel, mnemonics, anyDiceSource, anyDiceEnabled, addGlueAfterEach);
		} else {
			addAnyDiceButton(panel, mnemonics, anyDiceSource, anyDiceEnabled, addGlueAfterEach);
			addBrawlerButton(panel, mnemonics, bothDownSource, addGlueAfterEach);
			addHatredButton(panel, mnemonics, skullSource, addGlueAfterEach);
		}

		if (!ownChoice) {
			panel.add(mascotExtension.wrapperPanel(createReRollButton("No Re-Roll", mnemonics.none,
				() -> handleReRollUse(null))));
			if (addGlueAfterEach) {
				panel.add(Box.createHorizontalGlue());
			}
		}
	}

	private void addBrawlerButton(JPanel panel, Mnemonics mnemonics, ReRollSource bothDownSource,
		boolean addGlueAfterEach) {
		if (bothDownSource != null) {
			panel.add(mascotExtension.wrapperPanel(createReRollButton("Brawler Re-Roll", mnemonics.brawler,
				() -> handleReRollUse(bothDownSource))));
			if (addGlueAfterEach) {
				panel.add(Box.createHorizontalGlue());
			}
		}
	}

	private void addHatredButton(JPanel panel, Mnemonics mnemonics, ReRollSource skullSource,
		boolean addGlueAfterEach) {
		if (skullSource != null) {
			panel.add(mascotExtension.wrapperPanel(createReRollButton("Hatred Re-Roll", mnemonics.hatred,
				() -> handleReRollUse(skullSource))));
			if (addGlueAfterEach) {
				panel.add(Box.createHorizontalGlue());
			}
		}
	}

	private void addAnyDiceButton(JPanel panel, Mnemonics mnemonics, ReRollSource anyDiceSource,
		boolean anyDiceEnabled, boolean addGlueAfterEach) {
		if (anyDiceSource != null) {
			JButton anyDiceButton = createReRollButton(anyDiceSource.getName(getClient().getGame()),
				mnemonics.anyBlockDice, () -> handleReRollUse(anyDiceSource));
			anyDiceButton.setEnabled(anyDiceEnabled);
			registerAnyDiceButton(anyDiceButton);
			panel.add(mascotExtension.wrapperPanel(anyDiceButton));
			if (addGlueAfterEach) {
				panel.add(Box.createHorizontalGlue());
			}
		}
	}

	protected static class Mnemonics {
		final char team, brawler, hatred, none, anyBlockDice, trrFallback, proFallback, proTrrFallback;
		final List<Character> pro, anyDie, singleBlockDie;

		public Mnemonics(char team, char none, char brawler, char hatred, List<Character> pro, List<Character> anyDie,
			char anyBlockDice, char trrFallback, char proFallback, char proTrrFallback, List<Character> singleBlockDie) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.hatred = hatred;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
			this.trrFallback = trrFallback;
			this.proFallback = proFallback;
			this.proTrrFallback = proTrrFallback;
			this.singleBlockDie = singleBlockDie;
		}
	}

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

	protected static class BackgroundPanel extends JPanel {

		private final Color color;

		public BackgroundPanel(Color color) {
			this.color = color;
			setOpaque(true);
		}

		protected void paintComponent(Graphics pGraphics) {
			if (!isOpaque()) {
				super.paintComponent(pGraphics);
			} else {
				Graphics2D g2d = (Graphics2D) pGraphics;
				Dimension size = getSize();
				g2d.setPaint(new GradientPaint(0, 0, color, size.width - 1, 0, Color.WHITE, false));
				g2d.fillRect(0, 0, size.width, size.height);
				setOpaque(false);
				super.paintComponent(pGraphics);
				setOpaque(true);
			}
		}

	}

}
