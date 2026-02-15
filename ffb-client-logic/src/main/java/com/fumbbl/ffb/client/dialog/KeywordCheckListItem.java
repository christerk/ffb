package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.model.Keyword;

public final class KeywordCheckListItem {

	private final Keyword keyword;
	private boolean selected = false;

	public KeywordCheckListItem(Keyword keyword) {
		this.keyword = keyword;
		setSelected(false);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Keyword getKeyword() {
		return keyword;
	}

	public String getText() {
		return keyword.getName();
	}

}
