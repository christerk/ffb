package com.fumbbl.ffb.test;

import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.DbUpdater;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.util.rng.Fortuna;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class TestServer {
	private final FantasyFootballServer server;
	private final Path tmpDir;

	public TestServer() throws IOException {
		tmpDir = Files.createTempDirectory("ffb-statetest");
		tmpDir.toFile().deleteOnExit();

		File logDir = new File(tmpDir.toFile(), "logs");
		logDir.mkdirs();
		logDir.deleteOnExit();

		Properties properties = new Properties();
		properties.setProperty("server.log.file", new File(tmpDir.toFile(), "test.log").getAbsolutePath());
		properties.setProperty("server.log.folder", logDir.getAbsolutePath());

		server = new FantasyFootballServer(ServerMode.STANDALONE, properties);
		server.setDebugLog(new DebugLog(server, new File(tmpDir.toFile(), "test.log"), logDir, 0));
		server.setSessionManager(new SessionManager());
		server.setCommunication(new ServerCommunication(server));
		server.setFortuna(new Fortuna());
		server.setGameCache(new GameCache(server));
		server.setDbUpdater(new DbUpdater(server));
	}

	public FantasyFootballServer getServer() {
		return server;
	}

	public GameState getGameState() {
		return new GameState(server);
	}
}
