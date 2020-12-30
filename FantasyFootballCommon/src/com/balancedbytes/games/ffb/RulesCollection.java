package com.balancedbytes.games.ffb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface RulesCollection {
	
	public enum Rules {
		COMMON,
		BB2016,
		BB2020;
		
		public boolean matches(Rules other) {
			return this == COMMON || other == COMMON || this == other;
		}
	};
	
	Rules value() default Rules.COMMON;
}
