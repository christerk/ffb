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
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Kalimar
 */
public class AdminConnector {

	private static final String _USAGE = "java com.fumbbl.ffb.server.admin.AdminConnector backup <gameId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector block\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector cache\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector stats\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector close <gameId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector concede <gameId> <teamId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector delete <gameId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector forcelog <gameId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector list <status>\n"
		+ "  [status being one of: scheduled, starting, active, paused, finished, uploaded or backuped]\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector list <gameId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector loglevel <value>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector message <message>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector portrait <coach>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector refresh\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector shutdown\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector schedule <teamHomeId> <teamAwayId>\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector unblock\n"
		+ "java com.fumbbl.ffb.server.admin.AdminConnector upload <gameId>";

	private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");

	public static void main(String[] origArgs) throws NoSuchAlgorithmException, IOException {

		InifileParamFilterResult filterResult = new InifileParamFilter().filterForInifile(origArgs);

		String[] args = filterResult.getFilteredArgs();

		if (!ArrayTool.isProvided(args) || !StringTool.isProvided(args[0])) {

			System.out.println(_USAGE);

		} else {

			Properties serverProperties = new Properties();
			try (BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(AdminConnector.class.getResourceAsStream("/" + filterResult.getInifileName())))) {
				serverProperties.load(in);
			}

			String adminChallengeUrl = serverProperties.getProperty(IServerProperty.ADMIN_URL_CHALLENGE);
			System.out.println(adminChallengeUrl);
			String adminChallengeXml = UtilServerHttpClient.fetchPage(adminChallengeUrl);
			System.out.println(adminChallengeXml);

			String challenge = null;
			try (BufferedReader xmlReader = new BufferedReader(new StringReader(adminChallengeXml))) {
				String line = null;
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

			if (AdminServlet.SHUTDOWN.equals(args[0])) {
				String shutdownUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_SHUTDOWN),
						response);
				System.out.println(shutdownUrl);
				String shutdownXml = UtilServerHttpClient.fetchPage(shutdownUrl);
				System.out.println(shutdownXml);
			}

			if (AdminServlet.REFRESH.equals(args[0])) {
				String refreshUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_REFRESH), response);
				System.out.println(refreshUrl);
				String refreshXml = UtilServerHttpClient.fetchPage(refreshUrl);
				System.out.println(refreshXml);
			}

			if (AdminServlet.BLOCK.equals(args[0])) {
				String blockUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_BLOCK), response);
				System.out.println(blockUrl);
				String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
				System.out.println(blockXml);
			}

			if (AdminServlet.UNBLOCK.equals(args[0])) {
				String blockUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_UNBLOCK), response);
				System.out.println(blockUrl);
				String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
				System.out.println(blockXml);
			}

			if (AdminServlet.LOGLEVEL.equals(args[0])) {
				String logLevelUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_LOGLEVEL), response,
						args[1]);
				System.out.println(logLevelUrl);
				String logLevelXml = UtilServerHttpClient.fetchPage(logLevelUrl);
				System.out.println(logLevelXml);
			}

			if (AdminServlet.LIST.equals(args[0])) {
				long gameId;
				try {
					gameId = Long.parseLong(args[1]);
				} catch (NumberFormatException pNfe) {
					gameId = 0;
				}
				String adminListUrl;
				if (gameId > 0) {
					adminListUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_LIST_ID), response,
							args[1]);
				} else {
					adminListUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_LIST_STATUS), response,
							args[1]);
				}
				System.out.println(adminListUrl);
				String adminListXml = UtilServerHttpClient.fetchPage(adminListUrl);
				System.out.println(adminListXml);
			}

			if (AdminServlet.CACHE.equals(args[0])) {
				String cacheUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_CACHE), response,
						args[1]);
				System.out.println(cacheUrl);
				String cacheXml = UtilServerHttpClient.fetchPage(cacheUrl);
				System.out.println(cacheXml);
			}

			if (AdminServlet.STATS.equals(args[0])) {
				String statsUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_STATS), response,
						args[1]);
				System.out.println(statsUrl);
				String statsXml = UtilServerHttpClient.fetchPage(statsUrl);
				System.out.println(statsXml);
			}

			if (AdminServlet.CLOSE.equals(args[0])) {
				String closeUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_CLOSE), response,
					args[1]);
				System.out.println(closeUrl);
				String closeXml = UtilServerHttpClient.fetchPage(closeUrl);
				System.out.println(closeXml);
			}

			if (AdminServlet.FORCE_LOG.equals(args[0])) {
				String url = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_FORCELOG), response,
					args[1]);
				System.out.println(url);
				String xml = UtilServerHttpClient.fetchPage(url);
				System.out.println(xml);
			}

			if (AdminServlet.CONCEDE.equals(args[0])) {
				String concedeUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_CONCEDE), response,
					args[1], args[2]);
				System.out.println(concedeUrl);
				String concedeXml = UtilServerHttpClient.fetchPage(concedeUrl);
				System.out.println(concedeXml);
			}

			if (AdminServlet.UPLOAD.equals(args[0])) {
				String uploadUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_UPLOAD), response,
						args[1]);
				System.out.println(uploadUrl);
				String uploadXml = UtilServerHttpClient.fetchPage(uploadUrl);
				System.out.println(uploadXml);
			}

			if (AdminServlet.BACKUP.equals(args[0])) {
				String backupUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_BACKUP), response,
						args[1]);
				System.out.println(backupUrl);
				String backupXml = UtilServerHttpClient.fetchPage(backupUrl);
				System.out.println(backupXml);
			}

			if (AdminServlet.DELETE.equals(args[0])) {
				String deleteUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_DELETE), response,
					args[1]);
				System.out.println(deleteUrl);
				String deleteXml = UtilServerHttpClient.fetchPage(deleteUrl);
				System.out.println(deleteXml);
			}

			if (AdminServlet.MESSAGE.equals(args[0])) {
				String message = URLEncoder.encode(args[1], "UTF-8");
				String messageUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_MESSAGE), response,
						message);
				System.out.println(messageUrl);
				String messageXml = UtilServerHttpClient.fetchPage(messageUrl);
				System.out.println(messageXml);
			}

			if (AdminServlet.SCHEDULE.equals(args[0])) {
				String scheduleUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_SCHEDULE), response,
					args[1], args[2]);
				System.out.println(scheduleUrl);
				String scheduleXml = UtilServerHttpClient.fetchPage(scheduleUrl);
				System.out.println(scheduleXml);
			}

			if (AdminServlet.PORTRAIT.equals(args[0])) {
				String portraitUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_PORTRAIT), response,
					args[1]);
				System.out.println(portraitUrl);
				String scheduleXml = UtilServerHttpClient.fetchPage(portraitUrl);
				System.out.println(scheduleXml);
			}

		}

	}

}
