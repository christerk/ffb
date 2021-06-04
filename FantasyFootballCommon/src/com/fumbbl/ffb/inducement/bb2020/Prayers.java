package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Prayer;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Prayers implements com.fumbbl.ffb.inducement.Prayers {
	@Override
	public Set<Prayer> allPrayers() {
		return Collections.emptySet();
	}
}
