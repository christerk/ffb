package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.option.GameOptionId;

import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public interface InducementType extends INamedObject {


	String getDescription();

	String getSingular();

	String getPlural();

	String getName();

	GameOptionId getMaxId();

	GameOptionId getCostId();

	GameOptionId getReducedCostId();

	Set<? extends InducementType> getTypes();
}
