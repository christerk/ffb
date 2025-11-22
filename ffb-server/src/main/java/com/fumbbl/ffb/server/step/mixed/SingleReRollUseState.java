package com.fumbbl.ffb.server.step.mixed;

import com.fumbbl.ffb.ReRollSource;

public interface SingleReRollUseState {

	void setReRollSource(ReRollSource reRollSource);

	String getId();

	void setId(String playerId);
}
