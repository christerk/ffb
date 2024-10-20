package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;

public abstract class QuickBite extends SequenceGenerator<SequenceGenerator.SequenceParams> {
	public QuickBite() {
		super(Type.QuickBite);
	}

}
