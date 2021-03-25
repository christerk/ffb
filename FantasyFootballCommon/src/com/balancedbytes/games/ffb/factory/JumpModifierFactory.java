package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.JumpModifierCollection;

public abstract class JumpModifierFactory<T extends JumpModifierCollection> extends GenerifiedModifierFactory<JumpContext, JumpModifier, T> {
}
