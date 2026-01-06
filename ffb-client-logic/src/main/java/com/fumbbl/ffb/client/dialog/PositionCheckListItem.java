package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.model.RosterPosition;

public final class PositionCheckListItem {

	private final RosterPosition position;
	private boolean selected = false;

	public PositionCheckListItem(RosterPosition position) {
		this.position = position;
		setSelected(false);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public RosterPosition getPosition() {
		return position;
	}

	public String getText() {
		return position.getName();
	}

}
