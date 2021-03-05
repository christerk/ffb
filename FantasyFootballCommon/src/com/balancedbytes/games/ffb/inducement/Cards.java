package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.IKeyedItem;

import java.util.Set;

public interface Cards extends IKeyedItem {
	@Override
	default String getKey() {
		return getClass().getSimpleName();
	}

	Set<Card> allCards();
}
