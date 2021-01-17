package com.fumbbl.rng;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Christer Kaivo-oja
 */
public class NetworkEntropySource implements EntropySource {

	private Set<InetAddress> endpoints;

	public NetworkEntropySource() {
		endpoints = new HashSet<>();
		try {
			endpoints.add(InetAddress.getLocalHost());
		} catch (UnknownHostException uhe) {
		}
	}

	public void addEndpoint(InetAddress addr) {
		endpoints.add(addr);
	}

	public void addEndpoint(String addr) {
		try {
			endpoints.add(InetAddress.getByName(addr));
		} catch (UnknownHostException uhe) {
			System.err.println("Unknown endpoint address: " + addr);
		}
	}

	public byte getEntropy() {
		byte b = 0;

		b |= System.currentTimeMillis() & 0xff;

		for (InetAddress addr : endpoints) {
			try {
				addr.isReachable(100);
			} catch (IOException ioe) {
			}
		}

		b |= System.currentTimeMillis() & 0xff;

		return b;
	}

	public boolean hasEnoughEntropy() {
		return true;
	}

}
