package com.balancedbytes.games.ffb.inducement.bb2016;

import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.inducement.Usage;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.GameOptionId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InducementCollection extends com.balancedbytes.games.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{

		add(new InducementType("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX, GameOptionId.INDUCEMENT_BRIBES_COST,
				GameOptionId.INDUCEMENT_BRIBES_REDUCED_COST, true, IIconProperty.RESOURCE_BRIBE, Usage.AVOID_BAN) {
			private final List<String> ROSTERS_WITH_CHEAP_BRIBES = new ArrayList<String>() {
				private static final long serialVersionUID = 4821878254834048284L;

				{
					add("Goblin");
					add("Snotling");
				}
			};

			@Override
			protected boolean useReducedCostId(Roster roster) {
				return ROSTERS_WITH_CHEAP_BRIBES.contains(roster.getName());
			}
		});


	}};

	protected Set<InducementType> getSubTypes() {
		return types;
	}
}
