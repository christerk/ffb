package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.IKeyedItem;

import java.util.Set;

public interface InducementCollection extends IKeyedItem {
	default String getKey() {
		return getClass().getSimpleName();
	}

	Set<InducementType> getTypes();

}
