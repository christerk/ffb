package com.fumbbl.ffb.server.admin;

import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.commandline.InifileParamFilter;
import com.fumbbl.ffb.server.commandline.InifileParamFilterResult;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameStateConnector {

	private static final String _USAGE = "java com.fumbbl.ffb.server.admin.GameStateConnector behaviours <gameId>\n" +
		"java com.fumbbl.ffb.server.admin.GameStateConnector get <gameId> <fromDb> <includeLog>\n" +
		"  [fromDb being true, false or auto, where auto tries in memory first and falls back to db if needed]\n" +
		"  [includeLog being true, false or a positive integer, where an integer limits the log the last n entries, 0 has the same effect as true]\n" +
		"java com.fumbbl.ffb.server.admin.GameStateConnector result <gameId> <fromDb>\n" +
		"  [fromDb being true, false or auto, where auto tries in memory first and falls back to db if needed]\n" +
		"java com.fumbbl.ffb.server.admin.GameStateConnector set <file>\n" +
		"  [file being an unzipped json file containing the new gameState]\n";

	private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");

	public static void main(String[] origArgs) throws NoSuchAlgorithmException, IOException {

		InifileParamFilterResult filterResult = new InifileParamFilter().filterForInifile(origArgs);

		String[] args = filterResult.getFilteredArgs();

		if (!ArrayTool.isProvided(args) || !StringTool.isProvided(args[0])) {

			System.out.println(_USAGE);

		} else {

			Properties serverProperties = new Properties();
			try (BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(GameStateConnector.class.getResourceAsStream("/" + filterResult.getInifileName())))) {
				serverProperties.load(in);
			}

			String adminChallengeUrl = serverProperties.getProperty(IServerProperty.GAMESTATE_URL_CHALLENGE);
			System.out.println(adminChallengeUrl);
			String adminChallengeXml = UtilServerHttpClient.fetchPage(adminChallengeUrl);
			System.out.println(adminChallengeXml);

			String challenge = null;
			try (BufferedReader xmlReader = new BufferedReader(new StringReader(adminChallengeXml))) {
				String line;
				while ((line = xmlReader.readLine()) != null) {
					Matcher challengeMatcher = _PATTERN_CHALLENGE.matcher(line);
					if (challengeMatcher.find()) {
						challenge = challengeMatcher.group(1);
						break;
					}
				}
			}

			byte[] md5Password = PasswordChallenge
				.fromHexString(serverProperties.getProperty(IServerProperty.ADMIN_PASSWORD));
			String response = PasswordChallenge.createResponse(challenge, md5Password);

			if (GameStateServlet.BEHAVIOURS.equals(args[0])) {
				String url = StringTool.bind(serverProperties.getProperty(IServerProperty.GAMESTATE_URL_BEHAVIORS),
					response, args[1]);
				System.out.println(url);
				String servletResponse = UtilServerHttpClient.fetchPage(url);
				System.out.println(servletResponse);
			} else if (GameStateServlet.GET.equals(args[0])) {
				String url = StringTool.bind(serverProperties.getProperty(IServerProperty.GAMESTATE_URL_GET),
					response, args[1], args[2], args[3]);
				System.out.println(url);
				String servletResponse = UtilServerHttpClient.fetchPage(url);
				System.out.println(servletResponse);
			} else if (GameStateServlet.SET.equals(args[0])) {
				String url = StringTool.bind(serverProperties.getProperty(IServerProperty.GAMESTATE_URL_SET),
					response);
				System.out.println(url);
				String servletResponse = UtilServerHttpClient.post(url, new File(args[1]));
				System.out.println(servletResponse);
			} else if (GameStateServlet.RESULT.equals(args[0])) {
				String url = StringTool.bind(serverProperties.getProperty(IServerProperty.GAMESTATE_URL_RESULT),
					response, args[1], args[2]);
				System.out.println(url);
				String servletResponse = UtilServerHttpClient.fetchPage(url);
				System.out.println(servletResponse);
			}

		}

	}

}
