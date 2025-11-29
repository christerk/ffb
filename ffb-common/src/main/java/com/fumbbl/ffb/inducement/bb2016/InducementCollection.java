package com.fumbbl.ffb.inducement.bb2016;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InducementCollection extends com.fumbbl.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{

		add(new InducementType("bloodweiserBabes", "Bloodweiser Kegs", "Bloodweiser Keg", "Bloodweiser Kegs",
			GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEGS_COST, IIconProperty.RESOURCE_BLOODWEISER_KEG, Usage.KNOCKOUT_RECOVERY));

		add(new InducementType("card", null, null, null, null, null));

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
			protected boolean useReducedCostId(Team team) {
				return ROSTERS_WITH_CHEAP_BRIBES.contains(team.getRoster().getName());
			}
		});

		add(new InducementType("igor", "Igor", "Igor", "Igors", GameOptionId.INDUCEMENT_IGORS_MAX,
			GameOptionId.INDUCEMENT_IGORS_COST, IIconProperty.RESOURCE_IGOR, Usage.REGENERATION) {
			@Override
			public int availability(Team team, GameOptions options) {
				if (team.getRoster().hasApothecary()) {
					return 0;
				}
				return super.availability(team, options);
			}
		});

		add(new InducementType("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs",
			GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEFS_COST,
			GameOptionId.INDUCEMENT_CHEFS_REDUCED_COST, true, IIconProperty.RESOURCE_MASTER_CHEF, Usage.STEAL_REROLL) {
			@Override
			protected boolean useReducedCostId(Team team) {
				return "Halfling".equals(team.getRoster().getName());
			}
		});

		add(new InducementType("riotousRookies", "Riotous Rookies", "Riotous Rookies", "Riotous Rookies",
			GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_MAX, GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_COST, Usage.ADD_LINEMEN) {
			@Override
			public int availability(Team team, GameOptions options) {
				if (!StringTool.isProvided(team.getRoster().getRiotousPositionId()) || "0".equals(team.getRoster().getRiotousPositionId())) {
					return 0;
				}
				return super.availability(team, options);
			}
		});
	}};

	protected Set<InducementType> getSubTypes() {
		return types;
	}
}
