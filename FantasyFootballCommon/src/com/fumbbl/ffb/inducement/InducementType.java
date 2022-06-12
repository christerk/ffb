package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.option.IGameOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InducementType implements INamedObject {

	private final String fName, fDescription, fSingular, fPlural, slotIconProperty;
	private final GameOptionId maxId, costId, reducedCostId;
	private final boolean usesGenericSlot;
	private final Set<Usage> usages = new HashSet<>();

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	                      GameOptionId costId, String slotIconProperty, Usage usages) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, true, slotIconProperty, usages);
	}

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	                       GameOptionId costId) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, false, null, Usage.UNSPECIFIC);
	}
	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, Usage usages) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, false, null, usages);
	}

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, GameOptionId reducedCostId, boolean usesGenericSlot, String slotIconProperty, Usage... usages) {
		fName = pName;
		fDescription = pDescription;
		fSingular = pSingular;
		fPlural = pPlural;
		this.maxId = maxId;
		this.costId = costId;
		this.reducedCostId = reducedCostId;
		this.usesGenericSlot = usesGenericSlot;
		if (usages != null) {
			this.usages.addAll(Arrays.asList(usages));
		}
		this.slotIconProperty = slotIconProperty;
	}

	public String getDescription() {
		return fDescription;
	}

	public String getSingular() {
		return fSingular;
	}

	public String getPlural() {
		return fPlural;
	}

	public String getName() {
		return fName;
	}

	public GameOptionId getMaxId() {
		return maxId;
	}

	public GameOptionId getCostId() {
		return costId;
	}

	public GameOptionId getReducedCostId() {
		return reducedCostId;
	}

	public boolean isUsingGenericSlot() {
		return usesGenericSlot;
	}

	public Set<Usage> getUsages() {
		return usages;
	}

	public boolean hasUsage(Usage usage) {
		return usages.contains(usage);
	}

	public boolean hasSingleUsage(Usage usage) {
		return hasUsage(usage) && usages.size() == 1;
	}

	public String getSlotIconProperty() {
		return slotIconProperty;
	}

	public GameOptionId getActualCostId(Team team) {
		return useReducedCostId(team) ? getReducedCostId() : getCostId();
	}

	protected boolean useReducedCostId(Team team) {
		return false;
	}

	public int availability(Team team, GameOptions options) {
		IGameOption gameOption = options.getOptionWithDefault(getMaxId());

		if (gameOption instanceof GameOptionInt) {
			return ((GameOptionInt) gameOption).getValue();
		}

		return 0;
	}

	public Set<SpecialEffect> effects() {
		return Collections.emptySet();
	}

}
