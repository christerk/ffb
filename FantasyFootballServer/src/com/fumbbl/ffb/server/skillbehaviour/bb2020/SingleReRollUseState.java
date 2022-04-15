package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRollSource;

public interface SingleReRollUseState {

	void setReRollSource(ReRollSource reRollSource);

	String getId();

	void setId(String playerId);
}
