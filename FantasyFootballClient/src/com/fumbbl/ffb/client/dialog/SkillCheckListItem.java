package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.model.skill.Skill;

public final class SkillCheckListItem {

	private final Skill skill;
	private boolean fSelected = false;

	public SkillCheckListItem(Skill skill) {
		this.skill = skill;
		setSelected(false);
	}

	public boolean isSelected() {
		return fSelected;
	}

	public void setSelected(boolean pSelected) {
		fSelected = pSelected;
	}

	public Skill getSkill() {
		return skill;
	}

	public String getText() {
		return skill.getName();
	}

}
