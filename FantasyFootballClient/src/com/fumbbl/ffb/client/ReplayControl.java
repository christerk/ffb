package com.fumbbl.ffb.client;

import com.fumbbl.ffb.IIconProperty;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
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

/**
 * @author Kalimar
 */
public class ReplayControl extends JPanel implements MouseInputListener {

	private final FantasyFootballClient fClient;
	private BufferedImage fImage;
	private boolean fActive;
	private ReplayButton fButtonSkipBackward;
	private ReplayButton fButtonFastBackward;
	private ReplayButton fButtonPlayBackward;
	private ReplayButton fButtonPause;
	private ReplayButton fButtonPlayForward;
	private ReplayButton fButtonFastForward;
	private ReplayButton fButtonSkipForward;
	private Dimension size;

	private int iconGap;

	private int iconWidth;

	public ReplayControl(FantasyFootballClient pClient) {

		fClient = pClient;

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	public void initLayout(DimensionProvider dimensionProvider) {
		size = dimensionProvider.dimension(DimensionProvider.Component.REPLAY_CONTROL);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		iconGap = dimensionProvider.dimension(DimensionProvider.Component.REPLAY_ICON_GAP).width;
		iconWidth = dimensionProvider.dimension(DimensionProvider.Component.REPLAY_ICON_WIDTH).width;

		fButtonPause = new ReplayButton(new Point((int) ((size.width / 2.0f) - (iconWidth / 2)), 1), IIconProperty.REPLAY_PAUSE,
			IIconProperty.REPLAY_PAUSE_ACTIVE, IIconProperty.REPLAY_PAUSE_SELECTED, isActive(fButtonPause));
		fButtonPlayBackward = new ReplayButton(new Point(fButtonPause.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_PLAY_BACKWARD, IIconProperty.REPLAY_PLAY_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_PLAY_BACKWARD_SELECTED, isActive(fButtonPlayBackward));
		fButtonFastBackward = new ReplayButton(new Point(fButtonPlayBackward.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_FAST_BACKWARD, IIconProperty.REPLAY_FAST_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_FAST_BACKWARD_SELECTED, isActive(fButtonFastBackward));
		fButtonSkipBackward = new ReplayButton(new Point(fButtonFastBackward.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_SKIP_BACKWARD, IIconProperty.REPLAY_SKIP_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_SKIP_BACKWARD_SELECTED, isActive(fButtonSkipBackward));
		fButtonPlayForward = new ReplayButton(new Point(fButtonPause.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_PLAY_FORWARD, IIconProperty.REPLAY_PLAY_FORWARD_ACTIVE,
			IIconProperty.REPLAY_PLAY_FORWARD_SELECTED, isActive(fButtonPlayForward));
		fButtonFastForward = new ReplayButton(new Point(fButtonPlayForward.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_FAST_FORWARD, IIconProperty.REPLAY_FAST_FORWARD_ACTIVE,
			IIconProperty.REPLAY_FAST_FORWARD_SELECTED, isActive(fButtonFastForward));
		fButtonSkipForward = new ReplayButton(new Point(fButtonFastForward.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_SKIP_FORWARD, IIconProperty.REPLAY_SKIP_FORWARD_ACTIVE,
			IIconProperty.REPLAY_SKIP_FORWARD_SELECTED, isActive(fButtonSkipForward));

	}

	private boolean isActive(ReplayButton button) {
		return button != null && button.isActive();
	}

	public void refresh() {
		ClientReplayer replayer = getClient().getReplayer();
		Graphics2D g2d = fImage.createGraphics();
		g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, size.width / 2.0f, 0, new Color(128, 128, 128), false));
		g2d.fillRect(0, 0, size.width / 2, size.height);
		g2d.setPaint(new GradientPaint(size.width / 2.0f, 0, new Color(128, 128, 128), size.width, 0, Color.WHITE, false));
		g2d.fillRect(size.width / 2, 0, size.width, size.height);
		if (replayer.isRunning()) {
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
			String speed = ((replayer.getReplaySpeed() > 0) ? replayer.getReplaySpeed() : "0.5") +
				"x";
			Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(speed, g2d);
			if (replayer.isReplayDirectionForward()) {
				g2d.drawString(speed, size.width - (int) bounds.getWidth() - 7, 18);
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

	private class ReplayButton {

		private boolean fActive;
		private boolean fSelected;

		private final String fIconProperty;
		private final String fIconPropertyActive;
		private final String fIconPropertySelected;

		private final Point fPosition;

		public ReplayButton(Point pPosition, String pIconProperty, String pIconPropertyActive,
												String pIconPropertySelected, boolean active) {
			fPosition = pPosition;
			fIconProperty = pIconProperty;
			fIconPropertyActive = pIconPropertyActive;
			fIconPropertySelected = pIconPropertySelected;
			fActive = active;
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
			return ((pMouseEvent.getX() >= (fPosition.x - (iconGap / 2)))
				&& (pMouseEvent.getX() < (fPosition.x + iconWidth + (iconGap / 2))));
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
