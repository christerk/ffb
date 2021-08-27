package com.fumbbl.ffb.server.net;

import java.util.TimerTask;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.util.rng.NetworkEntropySource;

public class ServerNetworkEntropyTask extends TimerTask {

	private FantasyFootballServer fServer;
	private NetworkEntropySource fNetworkEntropySource;

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
			getServer().getDebugLog().log(anyException);
			System.exit(99);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

}
