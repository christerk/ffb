package com.fumbbl.ffb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RulesCollections {
	RulesCollection[] value();
}
