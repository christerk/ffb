package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public interface INamedObject extends IKeyedItem {

	String getName();

	default String getKey() { return getName(); }
}
