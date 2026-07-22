package com.fumbbl.ffb.client.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

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
	private final double guiScale;

	public ClientLayoutResult(Dimension contentSize, Rectangle fieldBounds, Rectangle homeSidebarBounds,
																Rectangle homeReserveBoxBounds, Rectangle awaySidebarBounds,
																Rectangle scoreBarBounds, Rectangle logBounds, Rectangle chatBounds,
																double pitchScale, double guiScale) {
		this.contentSize = new Dimension(contentSize);
		this.fieldBounds = new Rectangle(fieldBounds);
		this.homeSidebarBounds = new Rectangle(homeSidebarBounds);
		this.awaySidebarBounds = new Rectangle(awaySidebarBounds);
		this.scoreBarBounds = new Rectangle(scoreBarBounds);
		this.logBounds = new Rectangle(logBounds);
		this.chatBounds = new Rectangle(chatBounds);
		this.homeReserveBoxBounds = new Rectangle(homeReserveBoxBounds);
		this.pitchScale = pitchScale;
		this.guiScale = guiScale;
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

	public double guiScale() {
		return guiScale;
	}
}
