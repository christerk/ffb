package com.fumbbl.ffb.server.admin;

import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.commandline.InifileParamFilter;
import com.fumbbl.ffb.server.commandline.InifileParamFilterResult;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
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
		+ "java com.fumbbl.ffb.server.admin.AdminConnector purgetest <limit> <perform>\n"
		+ "  [limit has to be a positive number]"
		+ "  [if perform is set to \"true\" (case insensitive) games are deleted, all other values and default are considered false]"
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
			try (BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(AdminConnector.class.getResourceAsStream("/" + filterResult.getIniFileName())))) {
				serverProperties.load(in);
			}

			if (StringTool.isProvided(filterResult.getOverrideFileName())) {
				try (FileInputStream fileInputStream = new FileInputStream(filterResult.getOverrideFileName());
						 BufferedInputStream propertyInputStream = new BufferedInputStream(fileInputStream)) {
					serverProperties.load(propertyInputStream);
				}
			}

			String adminChallengeUrl = ServerUrlProperty.ADMIN_URL_CHALLENGE.url(serverProperties);
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

			if (AdminServlet.SHUTDOWN.equals(args[0])) {
				String shutdownUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_SHUTDOWN.url(serverProperties),
						response);
				System.out.println(shutdownUrl);
				String shutdownXml = UtilServerHttpClient.fetchPage(shutdownUrl);
				System.out.println(shutdownXml);
			}

			if (AdminServlet.REFRESH.equals(args[0])) {
				String refreshUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_REFRESH.url(serverProperties), response);
				System.out.println(refreshUrl);
				String refreshXml = UtilServerHttpClient.fetchPage(refreshUrl);
				System.out.println(refreshXml);
			}

			if (AdminServlet.BLOCK.equals(args[0])) {
				String blockUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_BLOCK.url(serverProperties), response);
				System.out.println(blockUrl);
				String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
				System.out.println(blockXml);
			}

			if (AdminServlet.UNBLOCK.equals(args[0])) {
				String blockUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_UNBLOCK.url(serverProperties), response);
				System.out.println(blockUrl);
				String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
				System.out.println(blockXml);
			}

			if (AdminServlet.LOGLEVEL.equals(args[0])) {
				String logLevelUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_LOGLEVEL.url(serverProperties), response,
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
					adminListUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_LIST_ID.url(serverProperties), response,
							args[1]);
				} else {
					adminListUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_LIST_STATUS.url(serverProperties), response,
							args[1]);
				}
				System.out.println(adminListUrl);
				String adminListXml = UtilServerHttpClient.fetchPage(adminListUrl);
				System.out.println(adminListXml);
			}

			if (AdminServlet.CACHE.equals(args[0])) {
				String cacheUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_CACHE.url(serverProperties), response);
				System.out.println(cacheUrl);
				String cacheXml = UtilServerHttpClient.fetchPage(cacheUrl);
				System.out.println(cacheXml);
			}

			if (AdminServlet.STATS.equals(args[0])) {
				String statsUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_STATS.url(serverProperties), response,
						args[1]);
				System.out.println(statsUrl);
				String statsXml = UtilServerHttpClient.fetchPage(statsUrl);
				System.out.println(statsXml);
			}

			if (AdminServlet.CLOSE.equals(args[0])) {
				String closeUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_CLOSE.url(serverProperties), response,
					args[1]);
				System.out.println(closeUrl);
				String closeXml = UtilServerHttpClient.fetchPage(closeUrl);
				System.out.println(closeXml);
			}

			if (AdminServlet.FORCE_LOG.equals(args[0])) {
				String url = StringTool.bind(ServerUrlProperty.ADMIN_URL_FORCELOG.url(serverProperties), response,
					args[1]);
				System.out.println(url);
				String xml = UtilServerHttpClient.fetchPage(url);
				System.out.println(xml);
			}

			if (AdminServlet.CONCEDE.equals(args[0])) {
				String concedeUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_CONCEDE.url(serverProperties), response,
					args[1], args[2]);
				System.out.println(concedeUrl);
				String concedeXml = UtilServerHttpClient.fetchPage(concedeUrl);
				System.out.println(concedeXml);
			}

			if (AdminServlet.UPLOAD.equals(args[0])) {
				String uploadUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_UPLOAD.url(serverProperties), response,
						args[1]);
				System.out.println(uploadUrl);
				String uploadXml = UtilServerHttpClient.fetchPage(uploadUrl);
				System.out.println(uploadXml);
			}

			if (AdminServlet.BACKUP.equals(args[0])) {
				String backupUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_BACKUP.url(serverProperties), response,
						args[1]);
				System.out.println(backupUrl);
				String backupXml = UtilServerHttpClient.fetchPage(backupUrl);
				System.out.println(backupXml);
			}

			if (AdminServlet.DELETE.equals(args[0])) {
				String deleteUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_DELETE.url(serverProperties), response,
					args[1]);
				System.out.println(deleteUrl);
				String deleteXml = UtilServerHttpClient.fetchPage(deleteUrl);
				System.out.println(deleteXml);
			}

			if (AdminServlet.MESSAGE.equals(args[0])) {
				String message = URLEncoder.encode(args[1], "UTF-8");
				String messageUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_MESSAGE.url(serverProperties), response,
						message);
				System.out.println(messageUrl);
				String messageXml = UtilServerHttpClient.fetchPage(messageUrl);
				System.out.println(messageXml);
			}

			if (AdminServlet.SCHEDULE.equals(args[0])) {
				String scheduleUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_SCHEDULE.url(serverProperties), response,
					args[1], args[2]);
				System.out.println(scheduleUrl);
				String scheduleXml = UtilServerHttpClient.fetchPage(scheduleUrl);
				System.out.println(scheduleXml);
			}

			if (AdminServlet.PORTRAIT.equals(args[0])) {
				String portraitUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_PORTRAIT.url(serverProperties), response,
					args[1]);
				System.out.println(portraitUrl);
				String scheduleXml = UtilServerHttpClient.fetchPage(portraitUrl);
				System.out.println(scheduleXml);
			}

			if (AdminServlet.PURGE_TEST.equals(args[0])) {
				String perform = "false";
				if (args.length > 2) {
					perform = args[2];
				}
				String purgeUrl = StringTool.bind(ServerUrlProperty.ADMIN_URL_PURGE_TEST.url(serverProperties), response,
					args[1], perform);
				System.out.println(purgeUrl);
				String scheduleXml = UtilServerHttpClient.fetchPage(purgeUrl);
				System.out.println(scheduleXml);
			}
		}

	}

}
