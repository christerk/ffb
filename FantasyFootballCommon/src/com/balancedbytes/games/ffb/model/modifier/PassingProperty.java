package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.PassingModifiers.PassContext;

public class PassingProperty extends NamedProperty {

	public PassingProperty(String name) {
		super(name);
	}

	public boolean appliesToContext(PassContext context) {
		return true;
	}
}
