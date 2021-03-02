package com.balancedbytes.games.ffb.inducement.bb2016;

import com.balancedbytes.games.ffb.option.GameOptionId;

import java.util.HashSet;
import java.util.Set;

public class InducementType implements com.balancedbytes.games.ffb.inducement.InducementType {
	private final Set<InducementType> types = new HashSet<InducementType>() {{
		add(new InducementType("bloodweiserBabes", "Bloodweiser Kegs", "Bloodweiser Keg", "Bloodweiser Kegs",
			GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEGS_COST));
		add(new InducementType("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX, GameOptionId.INDUCEMENT_BRIBES_COST,
			GameOptionId.INDUCEMENT_BRIBES_REDUCED_COST));
		add(new InducementType("extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings",
			GameOptionId.INDUCEMENT_EXTRA_TRAINING_MAX, GameOptionId.INDUCEMENT_EXTRA_TRAINING_COST));
		add(new InducementType("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs",
			GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEFS_COST,
			GameOptionId.INDUCEMENT_CHEFS_REDUCED_COST));
		add(new InducementType("igor", "Igor", "Igor", "Igors", GameOptionId.INDUCEMENT_IGORS_MAX, GameOptionId.INDUCEMENT_IGORS_COST));
		add(new InducementType("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries",
			GameOptionId.INDUCEMENT_APOS_MAX, GameOptionId.INDUCEMENT_APOS_COST));
		add(new InducementType("wizard", "Wizard", "Wizard", "Wizards", GameOptionId.INDUCEMENT_WIZARDS_MAX,
			GameOptionId.INDUCEMENT_WIZARDS_COST));
		add(new InducementType("starPlayers", "Star Players", "Star Player", "Star Players", GameOptionId.INDUCEMENT_STARS_MAX, null));
		add(new InducementType("mercenaries", "Mercenaries", "Mercenary", "Mercenaries", null, null));
		add(new InducementType("riotousRookies", "Riotous Rookies", "Riotous Rookies", "Riotous Rookies",
			GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_MAX, GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_COST));
		add(new InducementType("card", null, null, null, null, null));
	}};
	private final String fName;
	private final String fDescription;
	private final String fSingular;
	private final String fPlural;
	private final GameOptionId maxId;
	private final GameOptionId costId;
	private final GameOptionId reducedCostId;

	private InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId);
	}

	private InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, GameOptionId reducedCostId) {
		fName = pName;
		fDescription = pDescription;
		fSingular = pSingular;
		fPlural = pPlural;
		this.maxId = maxId;
		this.costId = costId;
		this.reducedCostId = reducedCostId;
	}

	@Override
	public String getDescription() {
		return fDescription;
	}

	@Override
	public String getSingular() {
		return fSingular;
	}

	@Override
	public String getPlural() {
		return fPlural;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public GameOptionId getMaxId() {
		return maxId;
	}

	@Override
	public GameOptionId getCostId() {
		return costId;
	}

	@Override
	public GameOptionId getReducedCostId() {
		return reducedCostId;
	}

	@Override
	public Set<InducementType> getTypes() {
		return types;
	}
}
