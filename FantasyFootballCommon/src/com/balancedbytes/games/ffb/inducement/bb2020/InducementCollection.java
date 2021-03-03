package com.balancedbytes.games.ffb.inducement.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.InducementType;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InducementCollection implements com.balancedbytes.games.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{
	}};

	public Set<InducementType> getTypes() {
		return types;
	}
}
