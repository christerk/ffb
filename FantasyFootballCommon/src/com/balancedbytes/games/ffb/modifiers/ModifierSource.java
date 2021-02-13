package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;

import java.util.Collection;

public interface ModifierSource {
  Collection<IRollModifier> modifier(Game game);
}
