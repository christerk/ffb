package com.fumbbl.ffb.client.state;

public class MenuItemConfig {
	private final String title;
	private final String iconProperty;
	private final int keyEvent;

	public MenuItemConfig(String title, String iconProperty, int keyEvent) {
		this.title = title;
		this.iconProperty = iconProperty;
		this.keyEvent = keyEvent;
	}

	public String getTitle() {
		return title;
	}

	public String getIconProperty() {
		return iconProperty;
	}

	public int getKeyEvent() {
		return keyEvent;
	}
}
