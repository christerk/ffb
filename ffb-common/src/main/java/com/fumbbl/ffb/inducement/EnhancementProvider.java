package com.fumbbl.ffb.inducement;

import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;

public interface EnhancementProvider {
	default TemporaryEnhancements enhancements(StatsMechanic mechanic) {
		return new TemporaryEnhancements();
	}
}
