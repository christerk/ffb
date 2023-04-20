package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;

public final class CommonPropertyCheckListItem {

	private final CommonProperty property;
	private boolean fSelected = false;

	public CommonPropertyCheckListItem(CommonProperty property) {
		this.property = property;
		setSelected(false);
	}

	public boolean isSelected() {
		return fSelected;
	}

	public void setSelected(boolean pSelected) {
		fSelected = pSelected;
	}

	public CommonProperty getProperty() {
		return property;
	}

	public String getText() {
		return property.getQualifiedValue();
	}

}
