package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.option.IGameOption;

public class InducementType implements INamedObject {

	private final String fName;
	private final String fDescription;
	private final String fSingular;
	private final String fPlural;
	private final GameOptionId maxId;
	private final GameOptionId costId;
	private final GameOptionId reducedCostId;
	private final boolean usesGenericSlot, requiresExplicitSelection;
	private final Usage usage;

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	                       GameOptionId costId, boolean usesGenericSlot, boolean requiresExplicitSelection) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, usesGenericSlot, requiresExplicitSelection, Usage.UNSPECIFIC);
	}
	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, boolean usesGenericSlot, boolean requiresExplicitSelection, Usage usage) {
		this(pName, pDescription, pSingular, pPlural, maxId, costId, costId, usesGenericSlot, requiresExplicitSelection, usage);
	}

	public InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId,
	               GameOptionId costId, GameOptionId reducedCostId, boolean usesGenericSlot, boolean requiresExplicitSelection, Usage usage) {
		fName = pName;
		fDescription = pDescription;
		fSingular = pSingular;
		fPlural = pPlural;
		this.maxId = maxId;
		this.costId = costId;
		this.reducedCostId = reducedCostId;
		this.usesGenericSlot = usesGenericSlot;
		this.requiresExplicitSelection = requiresExplicitSelection;
		this.usage = usage;
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

	public boolean requiresExplicitSelection() {
		return requiresExplicitSelection;
	}

	public Usage getUsage() {
		return usage;
	}

	public enum Usage {
		UNSPECIFIC, KNOCKOUT_RECOVERY, AVOID_BAN
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
}
