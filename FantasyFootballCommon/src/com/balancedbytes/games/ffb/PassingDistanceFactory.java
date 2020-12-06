package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PassingDistanceFactory implements INamedObjectFactory {

	public PassingDistance forName(String pName) {
		for (PassingDistance distance : PassingDistance.values()) {
			if (distance.getName().equalsIgnoreCase(pName)) {
				return distance;
			}
		}
		return null;
	}

	public PassingDistance forShortcut(char pShortcut) {
		for (PassingDistance distance : PassingDistance.values()) {
			if (distance.getShortcut() == pShortcut) {
				return distance;
			}
		}
		return null;
	}

}
