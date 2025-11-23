package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;

public class MenuItemConfig {
	private final String title;
	private final String iconProperty;
	private final int keyEvent;

	public MenuItemConfig(String title, String iconProperty, int keyEvent) {
		this.title = title;
		this.iconProperty = iconProperty;
		this.keyEvent = keyEvent;
	}

	public MenuItemConfig(Player<?> player, ISkillProperty property, String iconProperty, int keyEvent) {
		Skill skill = player != null ? player.getSkillWithProperty(property) : null;
		this.title = (skill != null ? skill.getName() : "");
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
