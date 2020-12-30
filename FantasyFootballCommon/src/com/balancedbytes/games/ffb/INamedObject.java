package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface INamedObject extends IKeyedItem {

	String getName();

	default Object getKey() { return getName(); }
}
