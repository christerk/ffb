package com.balancedbytes.games.ffb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface RulesCollection {
	
	public enum Rules {
		All,
		BB2020;
		
		public boolean matches(Rules other) {
			return this == All || other == All || this == other;
		}
	};
	
	Rules value() default Rules.All;
}
