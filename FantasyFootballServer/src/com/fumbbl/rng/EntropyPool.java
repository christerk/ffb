package com.fumbbl.rng;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Christer Kaivo-oja
 */
public class EntropyPool {

	private MessageDigest digest;
	private int byteCount;

	public EntropyPool() {
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void addEntropy(byte data) {
		digest.update(data);
		byteCount++;
	}

	public byte[] getEntropy() {
		byteCount = 0;
		return digest.digest();
	}

	public boolean hasEnoughEntropy() {
		return byteCount >= 32;
	}
}
