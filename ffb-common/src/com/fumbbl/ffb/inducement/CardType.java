package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.option.GameOptionId;

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
