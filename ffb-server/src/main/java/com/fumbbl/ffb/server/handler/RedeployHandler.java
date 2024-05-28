package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.StringTool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

public class RedeployHandler {

	private static final Pattern BRANCH_PATTERN = Pattern.compile("[-_a-zA-Z0-9]+");

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void redeploy(FantasyFootballServer server, String branch) {
		if (!StringTool.isProvided(branch) || !isValidBranch(branch)) {
			branch = server.getProperty(IServerProperty.SERVER_REDEPLOY_DEFAULT_BRANCH);
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

	private boolean isValidBranch(String branch) {
		return BRANCH_PATTERN.matcher(branch).matches();
	}
}
