package com.fumbbl.ffb.injury;

public class FoulWithChainsaw extends Foul {

	public FoulWithChainsaw() {
		super("foulWithChainsaw");
	}

	@Override
	public boolean shouldPlayFallSound() {
		return false;
	}

	@Override
	public boolean isFoul() {
		return true;
	}

	@Override
	public boolean isChainsaw() {
		return true;
	}
}
