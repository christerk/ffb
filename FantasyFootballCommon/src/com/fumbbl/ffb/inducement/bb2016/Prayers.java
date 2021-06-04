package com.fumbbl.ffb.inducement.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Prayer;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class Prayers implements com.fumbbl.ffb.inducement.Prayers {
	@Override
	public Set<Prayer> allPrayers() {
		return Collections.emptySet();
	}
}
