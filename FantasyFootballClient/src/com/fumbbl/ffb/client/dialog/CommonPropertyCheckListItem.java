package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;

public final class CommonPropertyCheckListItem {

	private final CommonProperty property;
	private final String category;
	private boolean fSelected = false;

	public CommonPropertyCheckListItem(String category) {
		this.category = category;
		this.property = null;
		setSelected(false);
	}

	public CommonPropertyCheckListItem(CommonProperty property) {
		this.property = property;
		this.category = null;
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
		return property.getDialogValue();
	}

	public String getCategory() {
		return category;
	}
}
