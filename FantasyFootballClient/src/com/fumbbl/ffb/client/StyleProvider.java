package com.fumbbl.ffb.client;

import java.awt.Color;

public class StyleProvider {

	private Color chatBackground = Color.WHITE;
	private Color logBackground = Color.WHITE;

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
}
