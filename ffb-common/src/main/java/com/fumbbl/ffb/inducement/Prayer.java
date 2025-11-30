package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;

public interface Prayer extends INamedObject {
	// expose enum method name() for serialization
	String name();

	boolean affectsBothTeams();

	String getDescription();

	InducementDuration getDuration();

	default TemporaryEnhancements enhancements(StatsMechanic mechanic) {
		return new TemporaryEnhancements();
	}

	default String eventMessage() {
		return "";
	}

	boolean isChangingPlayer();
}
