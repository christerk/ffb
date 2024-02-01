package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class AbstractDialogBlock extends Dialog {

	protected final Color colorOwnChoice = new Color(128, 0, 0);
	protected final Color colorOpponentChoice = new Color(12, 20, 136);


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
