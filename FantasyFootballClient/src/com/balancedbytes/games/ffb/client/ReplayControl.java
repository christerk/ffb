package com.balancedbytes.games.ffb.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.ui.ChatComponent;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class ReplayControl extends JPanel implements MouseInputListener {

	public static final int HEIGHT = 26;
	public static final int WIDTH = ChatComponent.WIDTH;

	private static final int _ICON_WIDTH = 36;
	private static final int _ICON_GAP = 10;

	private class ReplayButton {

		private boolean fActive;
		private boolean fSelected;

		private String fIconProperty;
		private String fIconPropertyActive;
		private String fIconPropertySelected;

		private Point fPosition;

		public ReplayButton(Point pPosition, String pIconProperty, String pIconPropertyActive,
				String pIconPropertySelected) {
			fPosition = pPosition;
			fIconProperty = pIconProperty;
			fIconPropertyActive = pIconPropertyActive;
			fIconPropertySelected = pIconPropertySelected;
		}

		public boolean isActive() {
			return fActive;
		}

		public void setActive(boolean pActive) {
			fActive = pActive;
		}

		public boolean isSelected() {
			return fSelected;
		}

		public void setSelected(boolean pSelected) {
			fSelected = pSelected;
		}

		public Point getPosition() {
			return fPosition;
		}

		public void draw(Graphics2D pGraphics2D) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage icon;
			if (isActive()) {
				icon = iconCache.getIconByProperty(fIconPropertyActive);
			} else if (isSelected()) {
				icon = iconCache.getIconByProperty(fIconPropertySelected);
			} else {
				icon = iconCache.getIconByProperty(fIconProperty);
			}
			pGraphics2D.drawImage(icon, fPosition.x, fPosition.y, null);
		}

		public boolean selectOnMouseOver(MouseEvent pMouseEvent) {
			boolean changed = false;
			if (isMouseOver(pMouseEvent)) {
				if (!isSelected()) {
					setSelected(true);
					changed = true;
				}
			} else {
				if (isSelected()) {
					setSelected(false);
					changed = true;
				}
			}
			return changed;
		}

		public boolean isMouseOver(MouseEvent pMouseEvent) {
			return ((pMouseEvent.getX() >= (fPosition.x - (_ICON_GAP / 2)))
					&& (pMouseEvent.getX() < (fPosition.x + _ICON_WIDTH + (_ICON_GAP / 2))));
		}

		public boolean activateOnMouseOver(MouseEvent pMouseEvent) {
			boolean changed = false;
			if (isMouseOver(pMouseEvent)) {
				if (!isActive()) {
					setActive(true);
					changed = true;
				}
			}
			return changed;
		}

	}

	private FantasyFootballClient fClient;

	private BufferedImage fImage;
	private boolean fActive;

	private ReplayButton fButtonSkipBackward;
	private ReplayButton fButtonFastBackward;
	private ReplayButton fButtonPlayBackward;
	private ReplayButton fButtonPause;
	private ReplayButton fButtonPlayForward;
	private ReplayButton fButtonFastForward;
	private ReplayButton fButtonSkipForward;

	public ReplayControl(FantasyFootballClient pClient) {

		fClient = pClient;
		fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

		setLayout(null);
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		fButtonPause = new ReplayButton(new Point((WIDTH / 2) - (_ICON_WIDTH / 2), 1), IIconProperty.REPLAY_PAUSE,
				IIconProperty.REPLAY_PAUSE_ACTIVE, IIconProperty.REPLAY_PAUSE_SELECTED);
		fButtonPlayBackward = new ReplayButton(new Point(fButtonPause.getPosition().x - _ICON_WIDTH - _ICON_GAP, 1),
				IIconProperty.REPLAY_PLAY_BACKWARD, IIconProperty.REPLAY_PLAY_BACKWARD_ACTIVE,
				IIconProperty.REPLAY_PLAY_BACKWARD_SELECTED);
		fButtonFastBackward = new ReplayButton(new Point(fButtonPlayBackward.getPosition().x - _ICON_WIDTH - _ICON_GAP, 1),
				IIconProperty.REPLAY_FAST_BACKWARD, IIconProperty.REPLAY_FAST_BACKWARD_ACTIVE,
				IIconProperty.REPLAY_FAST_BACKWARD_SELECTED);
		fButtonSkipBackward = new ReplayButton(new Point(fButtonFastBackward.getPosition().x - _ICON_WIDTH - _ICON_GAP, 1),
				IIconProperty.REPLAY_SKIP_BACKWARD, IIconProperty.REPLAY_SKIP_BACKWARD_ACTIVE,
				IIconProperty.REPLAY_SKIP_BACKWARD_SELECTED);
		fButtonPlayForward = new ReplayButton(new Point(fButtonPause.getPosition().x + _ICON_WIDTH + _ICON_GAP, 1),
				IIconProperty.REPLAY_PLAY_FORWARD, IIconProperty.REPLAY_PLAY_FORWARD_ACTIVE,
				IIconProperty.REPLAY_PLAY_FORWARD_SELECTED);
		fButtonFastForward = new ReplayButton(new Point(fButtonPlayForward.getPosition().x + _ICON_WIDTH + _ICON_GAP, 1),
				IIconProperty.REPLAY_FAST_FORWARD, IIconProperty.REPLAY_FAST_FORWARD_ACTIVE,
				IIconProperty.REPLAY_FAST_FORWARD_SELECTED);
		fButtonSkipForward = new ReplayButton(new Point(fButtonFastForward.getPosition().x + _ICON_WIDTH + _ICON_GAP, 1),
				IIconProperty.REPLAY_SKIP_FORWARD, IIconProperty.REPLAY_SKIP_FORWARD_ACTIVE,
				IIconProperty.REPLAY_SKIP_FORWARD_SELECTED);

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	private void refresh() {
		ClientReplayer replayer = getClient().getReplayer();
		Graphics2D g2d = fImage.createGraphics();
		g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, WIDTH / 2, 0, new Color(128, 128, 128), false));
		g2d.fillRect(0, 0, WIDTH / 2, HEIGHT);
		g2d.setPaint(new GradientPaint(WIDTH / 2, 0, new Color(128, 128, 128), WIDTH, 0, Color.WHITE, false));
		g2d.fillRect(WIDTH / 2, 0, WIDTH, HEIGHT);
		if (replayer.isRunning()) {
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
			String speed = new StringBuilder().append((replayer.getReplaySpeed() > 0) ? replayer.getReplaySpeed() : "0.5")
					.append("x").toString();
			Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(speed, g2d);
			if (replayer.isReplayDirectionForward()) {
				g2d.drawString(speed, WIDTH - (int) bounds.getWidth() - 7, 18);
			} else {
				g2d.drawString(speed, 7, 18);
			}
		}
		fButtonSkipBackward.draw(g2d);
		fButtonFastBackward.draw(g2d);
		fButtonPlayBackward.draw(g2d);
		fButtonPause.draw(g2d);
		fButtonPlayForward.draw(g2d);
		fButtonFastForward.draw(g2d);
		fButtonSkipForward.draw(g2d);
		g2d.dispose();
		repaint();
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	private void deActivateAll() {
		fButtonSkipBackward.setActive(false);
		fButtonFastBackward.setActive(false);
		fButtonPlayBackward.setActive(false);
		fButtonPause.setActive(false);
		fButtonPlayForward.setActive(false);
		fButtonFastForward.setActive(false);
		fButtonSkipForward.setActive(false);
	}

	private void deSelectAll() {
		fButtonSkipBackward.setSelected(false);
		fButtonFastBackward.setSelected(false);
		fButtonPlayBackward.setSelected(false);
		fButtonPause.setSelected(false);
		fButtonPlayForward.setSelected(false);
		fButtonFastForward.setSelected(false);
		fButtonSkipForward.setSelected(false);
	}

	public void showPause() {
		deActivateAll();
		fButtonPause.setActive(true);
		refresh();
	}

	public boolean isActive() {
		return fActive;
	}

	public void setActive(boolean pActive) {
		fActive = pActive;
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		if (isActive()) {
			boolean changed = fButtonSkipBackward.selectOnMouseOver(pMouseEvent);
			changed |= fButtonFastBackward.selectOnMouseOver(pMouseEvent);
			changed |= fButtonPlayBackward.selectOnMouseOver(pMouseEvent);
			changed |= fButtonPause.selectOnMouseOver(pMouseEvent);
			changed |= fButtonPlayForward.selectOnMouseOver(pMouseEvent);
			changed |= fButtonFastForward.selectOnMouseOver(pMouseEvent);
			changed |= fButtonSkipForward.selectOnMouseOver(pMouseEvent);
			if (changed) {
				refresh();
				repaint();
			}
		}
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
	}

	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	public void mouseExited(MouseEvent pMouseEvent) {
		deSelectAll();
		refresh();
		repaint();
	}

	public void mousePressed(MouseEvent pMouseEvent) {
		if (isActive()) {
			boolean changed = fButtonSkipBackward.activateOnMouseOver(pMouseEvent);
			changed |= fButtonFastBackward.activateOnMouseOver(pMouseEvent);
			changed |= fButtonPlayBackward.activateOnMouseOver(pMouseEvent);
			changed |= fButtonPause.activateOnMouseOver(pMouseEvent);
			changed |= fButtonPlayForward.activateOnMouseOver(pMouseEvent);
			changed |= fButtonFastForward.activateOnMouseOver(pMouseEvent);
			changed |= fButtonSkipForward.activateOnMouseOver(pMouseEvent);
			if (changed) {
				refresh();
				repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		if (isActive()) {
			ClientReplayer replayer = getClient().getReplayer();
			if (fButtonSkipBackward.isActive()) {
				fButtonSkipBackward.setActive(false);
				replayer.skip(false);
			} else if (fButtonSkipForward.isActive()) {
				fButtonSkipForward.setActive(false);
				replayer.skip(true);
			} else if (fButtonPlayBackward.isActive()) {
				if (fButtonPause.isActive() && replayer.isRunning()) {
					replayer.pause();
					fButtonPlayBackward.setActive(false);
				} else if (fButtonPlayForward.isActive() && !replayer.isReplayDirectionForward()) {
					replayer.pause();
					fButtonPlayBackward.setActive(false);
					replayer.play(true);
				} else if (fButtonFastForward.isActive()) {
					replayer.decreaseReplaySpeed();
					fButtonFastForward.setActive(false);
				} else if (fButtonFastBackward.isActive()) {
					replayer.increaseReplaySpeed();
					fButtonFastBackward.setActive(false);
				} else {
					fButtonPlayForward.setActive(false);
					fButtonPause.setActive(false);
					replayer.play(false);
				}
			} else if (fButtonPlayForward.isActive()) {
				if (fButtonPause.isActive() && replayer.isRunning()) {
					replayer.pause();
					fButtonPlayForward.setActive(false);
				} else if (fButtonPlayBackward.isActive() && replayer.isReplayDirectionForward()) {
					replayer.pause();
					fButtonPlayForward.setActive(false);
					replayer.play(false);
				} else if (fButtonFastForward.isActive()) {
					replayer.increaseReplaySpeed();
					fButtonFastForward.setActive(false);
				} else if (fButtonFastBackward.isActive()) {
					replayer.decreaseReplaySpeed();
					fButtonFastBackward.setActive(false);
				} else {
					fButtonPlayBackward.setActive(false);
					fButtonPause.setActive(false);
					replayer.play(true);
				}
			} else {
				if (fButtonFastForward.isActive()) {
					fButtonFastForward.setActive(false);
				}
				if (fButtonFastBackward.isActive()) {
					fButtonFastBackward.setActive(false);
				}
			}
			refresh();
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

}
