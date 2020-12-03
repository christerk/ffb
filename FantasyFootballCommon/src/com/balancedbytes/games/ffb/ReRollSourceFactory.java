package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ReRollSourceFactory implements INamedObjectFactory {

	static ReRollSources reRollSources = new ReRollSources();

	public ReRollSourceFactory()
	{
		reRollSources = new ReRollSources();
	}

	public ReRollSource forName(String pName) {
		return reRollSources.values().get(pName.toLowerCase());
	}

}
