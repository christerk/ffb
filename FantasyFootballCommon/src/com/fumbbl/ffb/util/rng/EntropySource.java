package com.fumbbl.ffb.util.rng;

/**
 * 
 * @author Christer Kaivo-oja
 */
public interface EntropySource {
	public boolean hasEnoughEntropy();

	public byte getEntropy();
}
