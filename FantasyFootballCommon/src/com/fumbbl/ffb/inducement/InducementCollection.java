package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InducementCollection implements IKeyedItem {
	private final Set<InducementType> types = new HashSet<InducementType>() {{
		add(new InducementType("bloodweiserBabes", "Bloodweiser Kegs", "Bloodweiser Keg", "Bloodweiser Kegs",
			GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEGS_COST, IIconProperty.RESOURCE_BLOODWEISER_KEG, Usage.KNOCKOUT_RECOVERY));

		add(new InducementType("extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings",
			GameOptionId.INDUCEMENT_EXTRA_TRAINING_MAX, GameOptionId.INDUCEMENT_EXTRA_TRAINING_COST, Usage.REROLL));

		add(new InducementType("card", null, null, null, null, null));

		add(new InducementType("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries",
			GameOptionId.INDUCEMENT_APOS_MAX, GameOptionId.INDUCEMENT_APOS_COST, Usage.APOTHECARY) {
			@Override
			public int availability(Team team, GameOptions options) {
				if (!team.getRoster().hasApothecary()) {
					return 0;
				}
				return super.availability(team, options);
			}
		});

		add(new InducementType("starPlayers", "Star Players", "Star Player", "Star Players", GameOptionId.INDUCEMENT_STARS_MAX, null, Usage.STAR));

		add(new InducementType("mercenaries", "Mercenaries", "Mercenary", "Mercenaries", null, null, Usage.LONER));

		add(new InducementType("wizard", "Wizard", "Wizard", "Wizards", GameOptionId.INDUCEMENT_WIZARDS_MAX,
			GameOptionId.INDUCEMENT_WIZARDS_COST, null, false, IIconProperty.RESOURCE_WIZARD, Usage.SPELL) {
			@Override
			public int availability(Team team, GameOptions options) {
				IGameOption wizardOption = options.getOptionWithDefault(getMaxId());
				if (!wizardOption.isChanged()) {
					return ((GameOptionBoolean) options.getOptionWithDefault(GameOptionId.WIZARD_AVAILABLE)).isEnabled() ? 1
						: 0;
				}

				return super.availability(team, options);
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
	}

	protected abstract Set<InducementType> getSubTypes();
}
