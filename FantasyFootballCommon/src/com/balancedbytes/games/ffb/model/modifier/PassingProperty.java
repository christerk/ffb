package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.modifiers.PassContext;

public class PassingProperty extends NamedProperty {

	public PassingProperty(String name) {
		super(name);
	}

	public boolean appliesToContext(PassContext context) {
		return true;
	}
}
