package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.INamedObject;

public interface Prayer extends INamedObject, EnhancementProvider {
	// expose enum method name() for serialization
	String name();

	boolean affectsBothTeams();

	String getDescription();

	InducementDuration getDuration();

	default String eventMessage() {
		return "";
	}

	boolean isChangingPlayer();
}
