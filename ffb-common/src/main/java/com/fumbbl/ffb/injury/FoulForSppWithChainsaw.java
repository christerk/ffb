package com.fumbbl.ffb.injury;

public class FoulForSppWithChainsaw extends FoulForSpp {

	public FoulForSppWithChainsaw() {
		super("foulForSppWithChainsaw");
	}

	@Override
	public boolean isChainsaw() {
		return true;
	}

}
