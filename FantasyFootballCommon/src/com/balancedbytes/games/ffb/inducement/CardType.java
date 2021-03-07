package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.option.GameOptionId;

/**
 * 
 * @author Kalimar
 */
public interface CardType extends INamedObject {

	String getName();

	String getDeckName();

	String getInducementNameSingle();

	String getInducementNameMultiple();

	GameOptionId getMaxId();

	GameOptionId getCostId();

	String getCardFront();

	String getCardBack();
}
