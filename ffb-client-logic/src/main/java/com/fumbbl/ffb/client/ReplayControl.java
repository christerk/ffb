package com.fumbbl.ffb.client;

import com.fumbbl.ffb.IIconProperty;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
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
	private final UiDimensionProvider dimensionProvider;

	public ReplayControl(FantasyFootballClient pClient, UiDimensionProvider dimensionProvider) {

		fClient = pClient;
		this.dimensionProvider = dimensionProvider;

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	public void initLayout() {
		size = dimensionProvider.dimension(Component.REPLAY_CONTROL);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		iconGap = dimensionProvider.dimension(Component.REPLAY_ICON_GAP).width;
		iconWidth = dimensionProvider.dimension(Component.REPLAY_ICON).width;

		fButtonPause = new ReplayButton(new Point((int) ((size.width / 2.0f) - ((float) iconWidth / 2)), 1), IIconProperty.REPLAY_PAUSE,
			IIconProperty.REPLAY_PAUSE_ACTIVE, IIconProperty.REPLAY_PAUSE_SELECTED,  IIconProperty.REPLAY_PAUSE_DISABLED, isActive(fButtonPause) );
		fButtonPlayBackward = new ReplayButton(new Point(fButtonPause.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_PLAY_BACKWARD, IIconProperty.REPLAY_PLAY_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_PLAY_BACKWARD_SELECTED, IIconProperty.REPLAY_PLAY_BACKWARD_DISABLED, isActive(fButtonPlayBackward));
		fButtonFastBackward = new ReplayButton(new Point(fButtonPlayBackward.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_FAST_BACKWARD, IIconProperty.REPLAY_FAST_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_FAST_BACKWARD_SELECTED, IIconProperty.REPLAY_FAST_BACKWARD_DISABLED, isActive(fButtonFastBackward));
		fButtonSkipBackward = new ReplayButton(new Point(fButtonFastBackward.getPosition().x - iconWidth - iconGap, 1),
			IIconProperty.REPLAY_SKIP_BACKWARD, IIconProperty.REPLAY_SKIP_BACKWARD_ACTIVE,
			IIconProperty.REPLAY_SKIP_BACKWARD_SELECTED, IIconProperty.REPLAY_SKIP_BACKWARD_DISABLED, isActive(fButtonSkipBackward));
		fButtonPlayForward = new ReplayButton(new Point(fButtonPause.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_PLAY_FORWARD, IIconProperty.REPLAY_PLAY_FORWARD_ACTIVE,
			IIconProperty.REPLAY_PLAY_FORWARD_SELECTED, IIconProperty.REPLAY_PLAY_FORWARD_DISABLED, isActive(fButtonPlayForward));
		fButtonFastForward = new ReplayButton(new Point(fButtonPlayForward.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_FAST_FORWARD, IIconProperty.REPLAY_FAST_FORWARD_ACTIVE,
			IIconProperty.REPLAY_FAST_FORWARD_SELECTED, IIconProperty.REPLAY_FAST_FORWARD_DISABLED, isActive(fButtonFastForward));
		fButtonSkipForward = new ReplayButton(new Point(fButtonFastForward.getPosition().x + iconWidth + iconGap, 1),
			IIconProperty.REPLAY_SKIP_FORWARD, IIconProperty.REPLAY_SKIP_FORWARD_ACTIVE,
			IIconProperty.REPLAY_SKIP_FORWARD_SELECTED, IIconProperty.REPLAY_SKIP_FORWARD_DISABLED, isActive(fButtonSkipForward));

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
			FontCache fontCache = getClient().getUserInterface().getFontCache();
			g2d.setFont(fontCache.font(Font.BOLD, 12, dimensionProvider));
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

		private boolean active;
		private boolean fSelected;
		private boolean enabled;

		private final String fIconProperty;
		private final String fIconPropertyActive;
		private final String fIconPropertySelected;
		private final String iconPropertyDisabled;

		private final Point fPosition;

		public ReplayButton(Point pPosition, String pIconProperty, String pIconPropertyActive,
												String pIconPropertySelected, String iconPropertyDisabled, boolean active) {
			fPosition = pPosition;
			fIconProperty = pIconProperty;
			fIconPropertyActive = pIconPropertyActive;
			fIconPropertySelected = pIconPropertySelected;
			this.active = active;
			this.iconPropertyDisabled = iconPropertyDisabled;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean pActive) {
			active = pActive;
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
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
			if (!isEnabled()) {
				icon = iconCache.getIconByProperty(iconPropertyDisabled, dimensionProvider);
			} else if (isActive()) {
				icon = iconCache.getIconByProperty(fIconPropertyActive, dimensionProvider);
			} else if (isSelected()) {
				icon = iconCache.getIconByProperty(fIconPropertySelected, dimensionProvider);
			} else {
				icon = iconCache.getIconByProperty(fIconProperty, dimensionProvider);
			}
			pGraphics2D.drawImage(icon, fPosition.x, fPosition.y, null);
		}

		public boolean selectOnMouseOver(MouseEvent pMouseEvent) {
			if (!isEnabled()) {
				return false;
			}
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
			if (!isEnabled()) {
				return false;
			}
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

	public void showPlay(boolean forward) {
		deActivateAll();
		(forward ? fButtonPlayForward : fButtonPlayBackward).setActive(true);
		refresh();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isActive() {
		return fActive;
	}

	public void setActive(boolean pActive) {
		fActive = pActive;
		enableButtons(pActive);
	}

	private void enableButtons(boolean pActive) {
		fButtonSkipBackward.setEnabled(pActive);
		fButtonFastBackward.setEnabled(pActive);
		fButtonPlayBackward.setEnabled(pActive);
		fButtonPause.setEnabled(pActive);
		fButtonPlayForward.setEnabled(pActive);
		fButtonFastForward.setEnabled(pActive);
		fButtonSkipForward.setEnabled(pActive);
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		if (!isActive()) {
			return;
		}
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
		if (!isActive()) {
			return;
		}
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

	public void mouseReleased(MouseEvent pMouseEvent) {
		if (!isActive()) {
			return;
		}
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

	public FantasyFootballClient getClient() {
		return fClient;
	}

}
