package com.fumbbl.ffb.net;

/**
 * 
 * @author Kalimar
 */
public class GameCoach {

	private String fGame;

	private String fCoach;

	private int fHashCode;

	public GameCoach(String pGame, String pCoach) {
		fGame = pGame;
		fCoach = pCoach;
	}

	public String getGame() {
		return fGame;
	}

	public String getCoach() {
		return fCoach;
	}

	public boolean equals(Object pObject) {
		boolean result = (pObject instanceof GameCoach);
		if (result) {
			GameCoach otherGameCoach = (GameCoach) pObject;
			result = getGame().equals(otherGameCoach.getGame()) && getCoach().equals(otherGameCoach.getCoach());
		}
		return result;
	}

	public int hashCode() {
		if (fHashCode == 0) {
			fHashCode = new StringBuilder().append(getGame()).append(':').append(getCoach()).toString().hashCode();
		}
		return fHashCode;
	}

}
