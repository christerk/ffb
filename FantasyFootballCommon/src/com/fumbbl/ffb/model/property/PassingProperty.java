package com.fumbbl.ffb.model.property;

import com.fumbbl.ffb.modifiers.PassContext;

public class PassingProperty extends NamedProperty {

	public PassingProperty(String name) {
		super(name);
	}

	public boolean appliesToContext(PassContext context) {
		return true;
	}
}
