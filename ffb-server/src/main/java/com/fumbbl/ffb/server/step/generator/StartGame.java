package com.fumbbl.ffb.server.step.generator;

public abstract class StartGame extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	public StartGame() {
		super(Type.StartGame);
	}

	@Override
	public abstract void pushSequence(SequenceParams params);
}
