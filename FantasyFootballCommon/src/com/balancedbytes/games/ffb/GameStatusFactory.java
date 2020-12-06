package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class GameStatusFactory implements INamedObjectFactory {

	public GameStatus forName(String pName) {
		for (GameStatus status : GameStatus.values()) {
			if (status.getName().equalsIgnoreCase(pName)) {
				return status;
			}
		}
		return null;
	}

	public GameStatus forTypeString(String pTypeString) {
		for (GameStatus status : GameStatus.values()) {
			if (status.getTypeString().equals(pTypeString)) {
				return status;
			}
		}
		return null;
	}

}
