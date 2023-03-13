package com.fumbbl.ffb.client;

import java.awt.Color;

public class StyleProvider {

	private Color chatBackground = Color.WHITE;
	private Color logBackground = Color.WHITE;
	private Color frameBackground;
	private Color frame = Color.WHITE;
	private Color frameShadow = Color.BLACK;
	private Color text = Color.BLACK;
	private Color home = Color.RED;
	private Color away = Color.BLUE;
	private Color spec = new Color(0, 128, 0);
	private Color admin = new Color(128, 128, 0);
	private Color dev = new Color(128, 0, 128);

	private boolean swapTeamColors;

	public Color getChatBackground() {
		return chatBackground;
	}

	public void setChatBackground(Color chatBackground) {
		this.chatBackground = chatBackground;
	}

	public Color getLogBackground() {
		return logBackground;
	}

	public void setLogBackground(Color logBackground) {
		this.logBackground = logBackground;
	}

	public Color getFrameBackground() {
		return frameBackground;
	}

	public void setFrameBackground(Color frameBackground) {
		this.frameBackground = frameBackground;
	}

	public Color getFrame() {
		return frame;
	}

	public void setFrame(Color frame) {
		this.frame = frame;
	}

	public Color getFrameShadow() {
		return frameShadow;
	}

	public void setFrameShadow(Color frameShadow) {
		this.frameShadow = frameShadow;
	}

	public Color getText() {
		return text;
	}

	public void setText(Color text) {
		this.text = text;
	}

	public Color getHome() {
		return swapTeamColors ? away : home;
	}

	public void setHome(Color home) {
		this.home = home;
	}

	public Color getAway() {
		return swapTeamColors ? home : away;
	}

	public void setAway(Color away) {
		this.away = away;
	}

	public Color getSpec() {
		return spec;
	}

	public void setSpec(Color spec) {
		this.spec = spec;
	}

	public Color getAdmin() {
		return admin;
	}

	public void setAdmin(Color admin) {
		this.admin = admin;
	}

	public Color getDev() {
		return dev;
	}

	public void setDev(Color dev) {
		this.dev = dev;
	}

	public boolean isSwapTeamColors() {
		return swapTeamColors;
	}

	public void setSwapTeamColors(boolean swapTeamColors) {
		this.swapTeamColors = swapTeamColors;
	}
}
