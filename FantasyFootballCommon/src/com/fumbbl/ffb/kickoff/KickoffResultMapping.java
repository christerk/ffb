package com.fumbbl.ffb.kickoff;

import com.fumbbl.ffb.IKeyedItem;

import java.util.Collection;

public abstract class KickoffResultMapping implements IKeyedItem {
	@Override
	public String getKey() {
		return getClass().getSimpleName();
	}

	public abstract KickoffResult getResult(int roll);
	public abstract Collection<KickoffResult> getValues();
}
