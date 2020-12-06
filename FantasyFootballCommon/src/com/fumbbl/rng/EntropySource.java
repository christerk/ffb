package com.fumbbl.rng;

/**
 * 
 * @author Christer Kaivo-oja
 */
public interface EntropySource {
	public boolean hasEnoughEntropy();

	public byte getEntropy();
}
