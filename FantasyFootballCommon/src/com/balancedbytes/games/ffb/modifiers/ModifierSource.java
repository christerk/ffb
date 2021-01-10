package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Collection;

public interface ModifierSource {
  Collection<IRollModifier> modifier();
}
