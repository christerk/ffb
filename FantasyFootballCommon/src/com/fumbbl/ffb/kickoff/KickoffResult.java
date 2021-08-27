package com.fumbbl.ffb.kickoff;

import com.fumbbl.ffb.INamedObject;

public interface KickoffResult extends INamedObject {

	String getName();

	String getDescription();

	boolean isFanReRoll();

	boolean isCoachReRoll();
}
