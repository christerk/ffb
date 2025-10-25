package com.fumbbl.ffb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface RulesCollection {

	enum Rules {
		COMMON(null),
		BB2016(COMMON),
		BB2020(COMMON),
		BB2025(BB2020);

		private final Rules extending;

		Rules(Rules extending) {
			this.extending = extending;
		}

		public boolean isOrExtends(Rules other) {
			if (this == other) {
				return true;
			}
			Rules extended = this.extending;
			while (extended != null) {
				if (extended == other) {
					return true;
				}
				extended = extended.extending;
			}
			return false;
		}

		public int getHierarchyLevel() {
			int level = 0;
			Rules extended = this.extending;
			while (extended != null) {
				level++;
				extended = extended.extending;
			}
			return level;
		}
	}

	Rules value() default Rules.COMMON;
}
