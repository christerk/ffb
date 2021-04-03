package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;

import java.util.stream.Stream;

public interface InjuryModifiers extends INamedObject {

	Stream<? extends InjuryModifier> values();

}
