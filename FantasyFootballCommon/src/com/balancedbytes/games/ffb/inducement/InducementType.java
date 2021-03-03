package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.option.IGameOption;

import java.util.Collections;
import java.util.Set;

public class InducementType implements INamedObject {

	private final String fName, fDescription, fSingular, fPlural, slotIconProperty;
	private final GameOptionId maxId, costId, reducedCostId;
	private final boolean usesGenericSlot;
	private final Usage usage;

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	                      GameOptionId costId, String slotIconProperty, Usage usage) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, true, slotIconProperty, usage);
	}

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	                       GameOptionId costId) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, false, null, Usage.UNSPECIFIC);
	}
	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, Usage usage) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, false, null, usage);
	}

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, GameOptionId reducedCostId, boolean usesGenericSlot, String slotIconProperty, Usage usage) {
		fName = pName;
		fDescription = pDescription;
		fSingular = pSingular;
		fPlural = pPlural;
		this.maxId = maxId;
		this.costId = costId;
		this.reducedCostId = reducedCostId;
		this.usesGenericSlot = usesGenericSlot;
		this.usage = usage;
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

	public Usage getUsage() {
		return usage;
	}

	public String getSlotIconProperty() {
		return slotIconProperty;
	}

	public GameOptionId getActualCostId(Roster roster) {
		return useReducedCostId(roster) ? getReducedCostId() : getCostId();
	}

	protected boolean useReducedCostId(Roster roster) {
		return false;
	}

	public int availability(Roster roster, GameOptions options) {
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
