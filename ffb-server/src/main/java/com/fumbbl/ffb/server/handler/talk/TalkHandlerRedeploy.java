package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import org.eclipse.jetty.websocket.api.Session;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

public class TalkHandlerRedeploy extends TalkHandler {

	private static final Pattern BRANCH_PATTERN = Pattern.compile("[-_a-zA-Z0-9]+");

	public TalkHandlerRedeploy() {
		super("/redeploy", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_SERVER, TalkRequirements.Privilege.DEV);
	}

	@Override
	@SuppressWarnings("ResultOfMethodCallIgnored")
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		String branch = server.getProperty(IServerProperty.SERVER_REDEPLOY_DEFAULT_BRANCH);
		if (commands.length > 1 && BRANCH_PATTERN.matcher(commands[1]).matches()) {
			branch = commands[1];
		}

		try {
			File file = new File(server.getProperty(IServerProperty.SERVER_REDEPLOY_FILE));
			if (!file.exists()) {
				file.createNewFile();
			}
			file.setWritable(true);
			Files.write(Paths.get(file.toURI()), branch.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
			System.exit(Integer.parseInt(server.getProperty(IServerProperty.SERVER_REDEPLOY_EXIT_CODE)));
		} catch (IOException e) {
			server.getDebugLog().logWithOutGameId(e);
		}
	}
}
