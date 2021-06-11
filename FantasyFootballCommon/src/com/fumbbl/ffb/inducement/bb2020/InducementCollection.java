package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.option.GameOptionId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InducementCollection extends com.fumbbl.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{
		add(new InducementType("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX, GameOptionId.INDUCEMENT_BRIBES_COST,
			GameOptionId.INDUCEMENT_BRIBES_REDUCED_COST, true, IIconProperty.RESOURCE_BRIBE, Usage.AVOID_BAN) {
			private final List<String> ROSTERS_WITH_CHEAP_BRIBES = new ArrayList<String>() {
				private static final long serialVersionUID = 4821878254834048284L;

				{
					add("Goblin");
					add("Snotling");
					add("Underworld Denizens");
				}
			};

			@Override
			protected boolean useReducedCostId(Roster roster) {
				return ROSTERS_WITH_CHEAP_BRIBES.contains(roster.getName());
			}
		});
		add(new InducementType("prayers", "Prayers", "Prayer", "Prayers", GameOptionId.INDUCEMENT_PRAYERS_MAX, GameOptionId.INDUCEMENT_PRAYERS_COST,
			GameOptionId.INDUCEMENT_PRAYERS_COST, false, IIconProperty.RESOURCE_PRAYER, Usage.GAME_MODIFICATION));
	}};

	protected Set<InducementType> getSubTypes() {
		return types;
	}
}
