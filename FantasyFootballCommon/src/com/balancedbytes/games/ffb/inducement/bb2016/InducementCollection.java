package com.balancedbytes.games.ffb.inducement.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InducementCollection implements com.balancedbytes.games.ffb.inducement.InducementCollection {
	private final Set<InducementType> types = new HashSet<InducementType>() {{

		add(new InducementType("bloodweiserKegs", "Bloodweiser Kegs", "Bloodweiser Keg", "Bloodweiser Kegs",
				GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEGS_COST, true, false, InducementType.Usage.KNOCKOUT_RECOVERY));

		add(new InducementType("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX, GameOptionId.INDUCEMENT_BRIBES_COST,
				GameOptionId.INDUCEMENT_BRIBES_REDUCED_COST, true, false, InducementType.Usage.AVOID_BAN) {
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

		add(new InducementType("extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings",
				GameOptionId.INDUCEMENT_EXTRA_TRAINING_MAX, GameOptionId.INDUCEMENT_EXTRA_TRAINING_COST, false, false));

		add(new InducementType("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs",
				GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEFS_COST,
				GameOptionId.INDUCEMENT_CHEFS_REDUCED_COST, true, false, InducementType.Usage.UNSPECIFIC) {
			@Override
			protected boolean useReducedCostId(Roster roster) {
				return "Halfling".equals(roster.getName());
			}
		});

		add(new InducementType("igor", "Igor", "Igor", "Igors", GameOptionId.INDUCEMENT_IGORS_MAX, GameOptionId.INDUCEMENT_IGORS_COST, true, false) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				if (roster.hasApothecary()) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries",
				GameOptionId.INDUCEMENT_APOS_MAX, GameOptionId.INDUCEMENT_APOS_COST, false, false) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				if (!roster.hasApothecary()) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("wizard", "Wizard", "Wizard", "Wizards", GameOptionId.INDUCEMENT_WIZARDS_MAX,
				GameOptionId.INDUCEMENT_WIZARDS_COST, true, false) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				IGameOption wizardOption = options.getOptionWithDefault(getMaxId());
				if (!wizardOption.isChanged()) {
					return ((GameOptionBoolean) options.getOptionWithDefault(GameOptionId.WIZARD_AVAILABLE)).isEnabled() ? 1
							: 0;
				}

				return super.availability(roster, options);
			}
		});

		add(new InducementType("starPlayers", "Star Players", "Star Player", "Star Players", GameOptionId.INDUCEMENT_STARS_MAX, null, false, true));

		add(new InducementType("mercenaries", "Mercenaries", "Mercenary", "Mercenaries", null, null, false, true));

		add(new InducementType("riotousRookies", "Riotous Rookies", "Riotous Rookies", "Riotous Rookies",
				GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_MAX, GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_COST, false, false) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				if (!StringTool.isProvided(roster.getRiotousPositionId()) || "0".equals(roster.getRiotousPositionId())) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("card", null, null, null, null, null, true, false));
	}};

	public Set<InducementType> getTypes() {
		return types;
	}
}
