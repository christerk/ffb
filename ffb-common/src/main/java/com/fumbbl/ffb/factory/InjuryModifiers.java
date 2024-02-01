package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.modifiers.InjuryModifier;

import java.util.stream.Stream;

public interface InjuryModifiers extends INamedObject {

	Stream<? extends InjuryModifier> values();

	Stream<? extends InjuryModifier> allValues();

	void setUseAll(boolean useAll);
}
