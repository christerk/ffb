package com.fumbbl.ffb.client.dialog.inducements;

/**
 * Common contract for the inducement table models (star players, infamous staff and mercenaries) that lets the buy
 * dialog determine, without knowing the concrete model, whether another entry can still be bought and how much the
 * cheapest unselected entry would cost.
 */
public interface InducementTableModel {

	/**
	 * Returns {@code true} if another entry can be bought given the category specific maximum.
	 */
	boolean canBuyAnother();

	/**
	 * Returns the cost of the cheapest entry that is not selected yet, or {@link Integer#MAX_VALUE} if none is left.
	 */
	int cheapestUnselectedCost();

	/**
	 * Returns {@code true} if buying an entry occupies a free roster slot.
	 */
	boolean requiresFreeRosterSlot();

}
