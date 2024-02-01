package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.modifiers.ArmorModifier;

import java.util.stream.Stream;

public interface ArmorModifiers extends INamedObject {

	Stream<? extends ArmorModifier> values();

	Stream<? extends ArmorModifier> allValues();

	void setUseAll(boolean useAll);
}
