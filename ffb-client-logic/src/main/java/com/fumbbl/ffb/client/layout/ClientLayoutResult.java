package com.fumbbl.ffb.client.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Immutable result of one client layout pass.
 *
 * Contains the runtime content size, component bounds, and pitch scale that
 * should be applied to Swing components and viewports.
 */

public class ClientLayoutResult {

	private final Dimension contentSize;
	private final Rectangle fieldBounds;
	private final Rectangle homeSidebarBounds;
	private final Rectangle awaySidebarBounds;
	private final Rectangle scoreBarBounds;
	private final Rectangle logBounds;
	private final Rectangle chatBounds;
	private final Rectangle homeReserveBoxBounds;
	private final double pitchScale;
	private final double runtimeGuiScale;

	public ClientLayoutResult(Dimension contentSize, Rectangle fieldBounds, Rectangle homeSidebarBounds,
																Rectangle homeReserveBoxBounds, Rectangle awaySidebarBounds,
																Rectangle scoreBarBounds, Rectangle logBounds, Rectangle chatBounds,
																double pitchScale, double runtimeGuiScale) {
		this.contentSize = new Dimension(contentSize);
		this.fieldBounds = new Rectangle(fieldBounds);
		this.homeSidebarBounds = new Rectangle(homeSidebarBounds);
		this.awaySidebarBounds = new Rectangle(awaySidebarBounds);
		this.scoreBarBounds = new Rectangle(scoreBarBounds);
		this.logBounds = new Rectangle(logBounds);
		this.chatBounds = new Rectangle(chatBounds);
		this.homeReserveBoxBounds = new Rectangle(homeReserveBoxBounds);
		this.pitchScale = pitchScale;
		this.runtimeGuiScale = runtimeGuiScale;
	}

	public Dimension contentSize() {
		return new Dimension(contentSize);
	}

	public Rectangle fieldBounds() {
		return new Rectangle(fieldBounds);
	}

	public Rectangle homeSidebarBounds() {
		return new Rectangle(homeSidebarBounds);
	}

	public Rectangle awaySidebarBounds() {
		return new Rectangle(awaySidebarBounds);
	}

	public Rectangle scoreBarBounds() {
		return new Rectangle(scoreBarBounds);
	}

	public Rectangle logBounds() {
		return new Rectangle(logBounds);
	}

	public Rectangle chatBounds() {
		return new Rectangle(chatBounds);
	}

	public Rectangle homeReserveBoxBounds() {
		return new Rectangle(homeReserveBoxBounds);
	}

	public double pitchScale() {
		return pitchScale;
	}

	public double runtimeGuiScale() {
		return runtimeGuiScale;
	}
}
