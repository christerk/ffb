package com.fumbbl.ffb.client;

import com.fumbbl.ffb.CommonProperty;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static com.fumbbl.ffb.CommonProperty.*;

public class StyleProvider {

	public static Map<CommonProperty, Color> defaults = new HashMap<CommonProperty, Color>() {{
		put(SETTING_BACKGROUND_CHAT, Color.WHITE);
		put(SETTING_BACKGROUND_LOG, Color.WHITE);
		put(SETTING_BACKGROUND_FRAME_COLOR, Color.WHITE);
		put(SETTING_FONT_COLOR_TEXT, Color.BLACK);
		put(SETTING_FONT_COLOR_AWAY, Color.BLUE);
		put(SETTING_FONT_COLOR_HOME, Color.RED);
		put(SETTING_FONT_COLOR_SPEC, new Color(0, 128, 0));
		put(SETTING_FONT_COLOR_DEV, new Color(128, 0, 128));
		put(SETTING_FONT_COLOR_ADMIN, new Color(128, 128, 0));
		put(SETTING_FONT_COLOR_FRAME, Color.WHITE);
		put(SETTING_FONT_COLOR_FRAME_SHADOW, Color.BLACK);
		put(SETTING_FONT_COLOR_INPUT, Color.BLACK);
	}};

	private Color chatBackground = defaults.get(SETTING_BACKGROUND_CHAT);
	private Color logBackground = defaults.get(SETTING_BACKGROUND_LOG);
	private Color frameBackground = defaults.get(SETTING_BACKGROUND_FRAME_COLOR);
	private Color frame = defaults.get(SETTING_FONT_COLOR_FRAME);
	private Color frameShadow = defaults.get(SETTING_FONT_COLOR_FRAME_SHADOW);
	private Color text = defaults.get(SETTING_FONT_COLOR_TEXT);
	private Color home = defaults.get(SETTING_FONT_COLOR_HOME);
	private Color away = defaults.get(SETTING_FONT_COLOR_AWAY);
	private Color spec = defaults.get(SETTING_FONT_COLOR_SPEC);
	private Color admin = defaults.get(SETTING_FONT_COLOR_ADMIN);
	private Color dev = defaults.get(SETTING_FONT_COLOR_DEV);
	private Color input = defaults.get(SETTING_FONT_COLOR_INPUT);

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

	public Color getHomeUnswapped() {
		return home;
	}

	public void setHome(Color home) {
		this.home = home;
	}

	public Color getAway() {
		return swapTeamColors ? home : away;
	}

	public Color getAwayUnswapped() {
		return away;
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

	public Color getInput() {
		return input;
	}

	public void setInput(Color input) {
		this.input = input;
	}
}
