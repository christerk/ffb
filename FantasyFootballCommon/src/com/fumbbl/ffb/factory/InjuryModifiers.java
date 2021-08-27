package com.fumbbl.ffb.factory;

import java.util.stream.Stream;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.modifiers.InjuryModifier;

public interface InjuryModifiers extends INamedObject {

	Stream<? extends InjuryModifier> values();

}
