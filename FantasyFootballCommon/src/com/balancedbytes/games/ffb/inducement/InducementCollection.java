package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.util.StringTool;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InducementCollection implements IKeyedItem {
	private Set<InducementType> types = new HashSet<InducementType>() {{
		add(new InducementType("bloodweiserBabes", "Bloodweiser Kegs", "Bloodweiser Keg", "Bloodweiser Kegs",
			GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEGS_COST, IIconProperty.RESOURCE_BLOODWEISER_KEG, Usage.KNOCKOUT_RECOVERY));

		add(new InducementType("extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings",
			GameOptionId.INDUCEMENT_EXTRA_TRAINING_MAX, GameOptionId.INDUCEMENT_EXTRA_TRAINING_COST, Usage.REROLL));

		add(new InducementType("card", null, null, null, null, null));

		add(new InducementType("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries",
			GameOptionId.INDUCEMENT_APOS_MAX, GameOptionId.INDUCEMENT_APOS_COST, Usage.APOTHECARY) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				if (!roster.hasApothecary()) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("igor", "Igor", "Igor", "Igors", GameOptionId.INDUCEMENT_IGORS_MAX,
			GameOptionId.INDUCEMENT_IGORS_COST, IIconProperty.RESOURCE_IGOR, Usage.REGENERATION) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				//TODO this might need change for the new rules depending on how the site will handle Sylvanian Spotlight
				if (roster.hasApothecary()) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("riotousRookies", "Riotous Rookies", "Riotous Rookies", "Riotous Rookies",
			GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_MAX, GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_COST, Usage.ADD_LINEMEN) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				//TODO this might need change for the new rules depending on how the site will handle Low Cost Linemen
				if (!StringTool.isProvided(roster.getRiotousPositionId()) || "0".equals(roster.getRiotousPositionId())) {
					return 0;
				}
				return super.availability(roster, options);
			}
		});

		add(new InducementType("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs",
			GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEFS_COST,
			GameOptionId.INDUCEMENT_CHEFS_REDUCED_COST, true, IIconProperty.RESOURCE_MASTER_CHEF, Usage.STEAL_REROLL) {
			@Override
			protected boolean useReducedCostId(Roster roster) {
				//TODO this might need change for the new rules depending on how the site will handle Halfling Thimble Cup
				return "Halfling".equals(roster.getName());
			}
		});

		add(new InducementType("starPlayers", "Star Players", "Star Player", "Star Players", GameOptionId.INDUCEMENT_STARS_MAX, null, Usage.STAR));

		add(new InducementType("mercenaries", "Mercenaries", "Mercenary", "Mercenaries", null, null, Usage.LONER));

		add(new InducementType("wizard", "Wizard", "Wizard", "Wizards", GameOptionId.INDUCEMENT_WIZARDS_MAX,
			GameOptionId.INDUCEMENT_WIZARDS_COST, IIconProperty.RESOURCE_WIZARD, Usage.SPELL) {
			@Override
			public int availability(Roster roster, GameOptions options) {
				IGameOption wizardOption = options.getOptionWithDefault(getMaxId());
				if (!wizardOption.isChanged()) {
					return ((GameOptionBoolean) options.getOptionWithDefault(GameOptionId.WIZARD_AVAILABLE)).isEnabled() ? 1
						: 0;
				}

				return super.availability(roster, options);
			}

			@Override
			public Set<SpecialEffect> effects() {
				return new HashSet<SpecialEffect>() {{
					add(SpecialEffect.FIREBALL);
					add(SpecialEffect.ZAP);
				}};
			}
		});
	}};

	public String getKey() {
		return getClass().getSimpleName();
	}

	public Set<InducementType> getTypes() {
		return Stream.concat(types.stream(), getSubTypes().stream()).collect(Collectors.toSet());
	};

	protected abstract Set<InducementType> getSubTypes();
}
