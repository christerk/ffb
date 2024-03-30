package com.fumbbl.ffb.server.net;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.util.rng.NetworkEntropySource;

import java.util.TimerTask;

public class ServerNetworkEntropyTask extends TimerTask {

	private final FantasyFootballServer fServer;
	private final NetworkEntropySource fNetworkEntropySource;

	public ServerNetworkEntropyTask(FantasyFootballServer server) {
		fServer = server;
		// The NetworkEntropySource defaults to pinging localhost.
		// Here we add some more hosts. This should be configurable.
		fNetworkEntropySource = new NetworkEntropySource();
		fNetworkEntropySource.addEndpoint("www.google.com");
		fNetworkEntropySource.addEndpoint("slashdot.org");
		fNetworkEntropySource.addEndpoint("192.168.0.18");
	}

	public void run() {
		try {
			if (fNetworkEntropySource.hasEnoughEntropy()) {
				getServer().getFortuna().addEntropy(fNetworkEntropySource.getEntropy());
			}
		} catch (Exception anyException) {
			getServer().getDebugLog().logWithOutGameId(anyException);
			System.exit(99);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

}
