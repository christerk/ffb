package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.FactoryType.FactoryContext;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public abstract class NetCommand implements IJsonSerializable {

	public abstract NetCommandId getId();
	public abstract FactoryContext getContext();

	public boolean isInternal() {
		return false;
	}

}
