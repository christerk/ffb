package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.IKeyedItem;

import java.util.Set;

public interface Prayers extends IKeyedItem {
	@Override
	default String getKey() { return getClass().getSimpleName(); }

	Set<Prayer> allPrayers();
}
