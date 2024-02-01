package com.fumbbl.ffb.inducement;

import java.util.Set;

import com.fumbbl.ffb.IKeyedItem;

public interface Cards extends IKeyedItem {
	@Override
	default String getKey() {
		return getClass().getSimpleName();
	}

	Set<Card> allCards();
}
