package com.fumbbl.ffb.net;

import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.json.IJsonSerializable;

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
