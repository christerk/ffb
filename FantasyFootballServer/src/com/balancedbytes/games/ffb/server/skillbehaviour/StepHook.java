package com.balancedbytes.games.ffb.server.skillbehaviour;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface StepHook {
	public enum HookPoint {
		PASS_INTERCEPT,
	}
	
	HookPoint value();
}
