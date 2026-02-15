package com.fumbbl.ffb.kickoff;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.EnhancementProvider;

public interface KickoffResult extends INamedObject, EnhancementProvider {

	String getName();

	String getDescription();

	boolean isFanReRoll();

	boolean isCoachReRoll();
}
