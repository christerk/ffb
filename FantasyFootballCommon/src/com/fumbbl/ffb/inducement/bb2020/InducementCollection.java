package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.option.GameOptionId;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InducementCollection extends com.fumbbl.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{
		add(new InducementType("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX, GameOptionId.INDUCEMENT_BRIBES_COST,
			GameOptionId.INDUCEMENT_BRIBES_REDUCED_COST, true, IIconProperty.RESOURCE_BRIBE, Usage.AVOID_BAN) {

			@Override
			protected boolean useReducedCostId(Roster roster) {
				return roster.getSpecialRules().contains(SpecialRule.BRIBERY_AND_CORRUPTION);
			}
		});
		add(new InducementType("prayers", "Prayers", "Prayer", "Prayers", GameOptionId.INDUCEMENT_PRAYERS_MAX, GameOptionId.INDUCEMENT_PRAYERS_COST,
			GameOptionId.INDUCEMENT_PRAYERS_COST, false, IIconProperty.RESOURCE_PRAYER, Usage.GAME_MODIFICATION));

		add(new InducementType("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs",
			GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEFS_COST,
			GameOptionId.INDUCEMENT_CHEFS_REDUCED_COST, true, IIconProperty.RESOURCE_MASTER_CHEF, Usage.STEAL_REROLL) {
			@Override
			protected boolean useReducedCostId(Roster roster) {
				return roster.getSpecialRules().contains(SpecialRule.HALFLING_THIMBLE_CUP);
			}
		});


	}};

	protected Set<InducementType> getSubTypes() {
		return types;
	}
}
